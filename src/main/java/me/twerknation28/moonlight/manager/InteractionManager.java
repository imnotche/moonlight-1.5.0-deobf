package me.twerknation28.moonlight.manager;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Box;
import java.util.HashSet;
import net.minecraft.util.math.Vec3d;
import java.util.Set;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import me.twerknation28.moonlight.util.Util;

public class InteractionManager implements Util
{
    public static Direction getPlaceDirectionGrim(final BlockPos blockPos) {
        final Set<Direction> directions = getPlaceDirectionsGrim(InteractionManager.mc.player.getPos(), blockPos);
        return directions.stream().findAny().orElse(Direction.UP);
    }
    
    public static Set<Direction> getPlaceDirectionsGrim(final Vec3d eyePos, final BlockPos blockPos) {
        return getPlaceDirectionsGrim(eyePos.x, eyePos.y, eyePos.z, blockPos);
    }
    
    public static Set<Direction> getPlaceDirectionsGrim(final double x, final double y, final double z, final BlockPos pos) {
        final Set<Direction> dirs = new HashSet<Direction>(6);
        final Box combined = getCombinedBox(pos);
        final Box eyePositions = new Box(x, y + 0.4, z, x, y + 1.62, z).expand(2.0E-4);
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
    
    private static Box getCombinedBox(final BlockPos pos) {
        final VoxelShape shape = InteractionManager.mc.world.getBlockState(pos).getCollisionShape((BlockView)InteractionManager.mc.world, pos).offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
        Box combined = new Box(pos);
        for (final Box box : shape.getBoundingBoxes()) {
            final double minX = Math.max(box.minX, combined.minX);
            final double minY = Math.max(box.minY, combined.minY);
            final double minZ = Math.max(box.minZ, combined.minZ);
            final double maxX = Math.min(box.maxX, combined.maxX);
            final double maxY = Math.min(box.maxY, combined.maxY);
            final double maxZ = Math.min(box.maxZ, combined.maxZ);
            combined = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return combined;
    }
    
    public static float calcBlockBreakingDelta(final BlockState state, final BlockView world, final BlockPos pos) {
        final float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        final int i = canHarvest(state) ? 30 : 100;
        return getBlockBreakingSpeed(state) / f / i;
    }
    
    private static boolean canHarvest(final BlockState state) {
        if (state.isToolRequired()) {
            final int tool = InventoryManager.getBestTool(state);
            return InteractionManager.mc.player.getInventory().getStack(tool).isSuitableFor(state);
        }
        return true;
    }
    
    private static float getBlockBreakingSpeed(final BlockState block) {
        final int tool = InventoryManager.getBestTool(block);
        float f = InteractionManager.mc.player.getInventory().getStack(tool).getMiningSpeedMultiplier(block);
        if (f > 1.0f) {
            final ItemStack stack = InteractionManager.mc.player.getInventory().getStack(tool);
            final int i = EnchantmentHelper.getLevel((RegistryEntry)InteractionManager.mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get(), stack);
            if (i > 0 && !stack.isEmpty()) {
                f += i * i + 1;
            }
        }
        if (StatusEffectUtil.hasHaste((LivingEntity)InteractionManager.mc.player)) {
            f *= 1.0f + (StatusEffectUtil.getHasteAmplifier((LivingEntity)InteractionManager.mc.player) + 1) * 0.2f;
        }
        if (InteractionManager.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float var10000 = 0.0f;
            switch (InteractionManager.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    var10000 = 0.3f;
                    break;
                }
                case 1: {
                    var10000 = 0.09f;
                    break;
                }
                case 2: {
                    var10000 = 0.0027f;
                    break;
                }
                default: {
                    var10000 = 8.1E-4f;
                    break;
                }
            }
            final float g = var10000;
            f *= g;
        }
        if (InteractionManager.mc.player.isSubmergedIn(FluidTags.WATER) && !hasAquaAffinity((PlayerEntity)InteractionManager.mc.player)) {
            f /= 5.0f;
        }
        if (!InteractionManager.mc.player.isOnGround()) {
            f /= 5.0f;
        }
        return f;
    }
    
    public static boolean hasAquaAffinity(final PlayerEntity player) {
        for (final ItemStack armor : player.getArmorItems()) {
            final ItemEnchantmentsComponent enchants = EnchantmentHelper.getEnchantments(armor);
            if (enchants.getEnchantments().contains(InteractionManager.mc.world.getRegistryManager().get(Enchantments.AQUA_AFFINITY.getRegistryRef()).getEntry(Enchantments.PROTECTION).get())) {
                return true;
            }
        }
        return false;
    }
}
