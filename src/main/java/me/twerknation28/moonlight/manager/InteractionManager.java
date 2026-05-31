package me.twerknation28.moonlight.manager;

import java.util.HashSet;
import java.util.Set;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class InteractionManager
implements Util {
    public static Direction getPlaceDirectionGrim(BlockPos blockPos) {
        Set<Direction> directions = InteractionManager.getPlaceDirectionsGrim(InteractionManager.mc.player.getPos(), blockPos);
        return directions.stream().findAny().orElse(Direction.UP);
    }

    public static Set<Direction> getPlaceDirectionsGrim(Vec3d eyePos, BlockPos blockPos) {
        return InteractionManager.getPlaceDirectionsGrim(eyePos.x, eyePos.y, eyePos.z, blockPos);
    }

    public static Set<Direction> getPlaceDirectionsGrim(double x, double y, double z, BlockPos pos) {
        HashSet<Direction> dirs = new HashSet<Direction>(6);
        Box combined = InteractionManager.getCombinedBox(pos);
        Box eyePositions = new Box(x, y + 0.4, z, x, y + 1.62, z).expand(2.0E-4);
        if (eyePositions.minZ <= combined.minZ) {
            dirs.add(Direction.NORTH);
        }
        if (eyePositions.maxZ >= combined.maxZ) {
            dirs.add(Direction.SOUTH);
        }
        if (eyePositions.maxX >= combined.maxX) {
            dirs.add(Direction.EAST);
        }
        if (eyePositions.minX <= combined.minX) {
            dirs.add(Direction.WEST);
        }
        if (eyePositions.maxY >= combined.maxY) {
            dirs.add(Direction.UP);
        }
        if (eyePositions.minY <= combined.minY) {
            dirs.add(Direction.DOWN);
        }
        return dirs;
    }

    private static Box getCombinedBox(BlockPos pos) {
        VoxelShape shape = InteractionManager.mc.world.getBlockState(pos).getCollisionShape((BlockView)InteractionManager.mc.world, pos).offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        Box combined = new Box(pos);
        for (Box box : shape.getBoundingBoxes()) {
            double minX = Math.max(box.minX, combined.minX);
            double minY = Math.max(box.minY, combined.minY);
            double minZ = Math.max(box.minZ, combined.minZ);
            double maxX = Math.min(box.maxX, combined.maxX);
            double maxY = Math.min(box.maxY, combined.maxY);
            double maxZ = Math.min(box.maxZ, combined.maxZ);
            combined = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return combined;
    }

    public static float calcBlockBreakingDelta(BlockState state, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = InteractionManager.canHarvest(state) ? 30 : 100;
        return InteractionManager.getBlockBreakingSpeed(state) / f / (float)i;
    }

    private static boolean canHarvest(BlockState state) {
        if (state.isToolRequired()) {
            int tool = InventoryManager.getBestTool(state);
            return InteractionManager.mc.player.getInventory().getStack(tool).isSuitableFor(state);
        }
        return true;
    }

    private static float getBlockBreakingSpeed(BlockState block) {
        int tool = InventoryManager.getBestTool(block);
        float f = InteractionManager.mc.player.getInventory().getStack(tool).getMiningSpeedMultiplier(block);
        if (f > 1.0f) {
            ItemStack stack = InteractionManager.mc.player.getInventory().getStack(tool);
            int i = EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)InteractionManager.mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get()), (ItemStack)stack);
            if (i > 0 && !stack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }
        if (StatusEffectUtil.hasHaste((LivingEntity)InteractionManager.mc.player)) {
            f *= 1.0f + (float)(StatusEffectUtil.getHasteAmplifier((LivingEntity)InteractionManager.mc.player) + 1) * 0.2f;
        }
        if (InteractionManager.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float g = switch (InteractionManager.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            };
            f *= g;
        }
        if (InteractionManager.mc.player.isSubmergedIn(FluidTags.WATER) && !InteractionManager.hasAquaAffinity((PlayerEntity)InteractionManager.mc.player)) {
            f /= 5.0f;
        }
        if (!InteractionManager.mc.player.isOnGround()) {
            f /= 5.0f;
        }
        return f;
    }

    public static boolean hasAquaAffinity(PlayerEntity player) {
        for (ItemStack armor : player.getArmorItems()) {
            ItemEnchantmentsComponent enchants = EnchantmentHelper.getEnchantments((ItemStack)armor);
            if (!enchants.getEnchantments().contains(InteractionManager.mc.world.getRegistryManager().get(Enchantments.AQUA_AFFINITY.getRegistryRef()).getEntry(Enchantments.PROTECTION).get())) continue;
            return true;
        }
        return false;
    }
}
