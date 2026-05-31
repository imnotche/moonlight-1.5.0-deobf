package me.twerknation28.moonlight.util;

import java.util.Iterator;
import me.twerknation28.moonlight.util.EnchantmentUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class InteractionUtil
implements Util {
    public static boolean canBreak(BlockPos blockPos, BlockState state) {
        if (!InteractionUtil.mc.player.isCreative() && state.getHardness((BlockView)InteractionUtil.mc.world, blockPos) < 0.0f) {
            return false;
        }
        return state.getOutlineShape((BlockView)InteractionUtil.mc.world, blockPos) != VoxelShapes.empty();
    }

    public static boolean isPlaceable(BlockPos pos, boolean entityCheck) {
        if (!InteractionUtil.mc.world.getBlockState(pos).isReplaceable()) {
            return false;
        }
        Iterator iterator = InteractionUtil.mc.world.getEntitiesByClass(Entity.class, new Box(pos), e -> !(e instanceof ExperienceBottleEntity) && !(e instanceof ItemEntity) && !(e instanceof ExperienceOrbEntity)).iterator();
        if (iterator.hasNext()) {
            Entity e2 = (Entity)iterator.next();
            if (e2 instanceof PlayerEntity) {
                return false;
            }
            return !entityCheck;
        }
        return true;
    }

    public static boolean breakBlock(BlockPos pos) {
        if (!InteractionUtil.canBreak(pos, InteractionUtil.mc.world.getBlockState(pos))) {
            return false;
        }
        BlockPos bp = pos instanceof BlockPos.Mutable ? new BlockPos((Vec3i)pos) : pos;
        mc.getNetworkHandler().sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, bp, Direction.UP));
        InteractionUtil.mc.player.swingHand(Hand.MAIN_HAND);
        mc.getNetworkHandler().sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, bp, Direction.UP));
        mc.getNetworkHandler().sendPacket((Packet)new HandSwingC2SPacket(Hand.MAIN_HAND));
        return true;
    }

    public static void useItem(BlockPos pos) {
        InteractionUtil.useItem(pos, Hand.MAIN_HAND);
    }

    public static void useItem(BlockPos pos, Hand hand) {
        if (InteractionUtil.mc.world == null || InteractionUtil.mc.player == null || InteractionUtil.mc.interactionManager == null) {
            return;
        }
        Direction direction = InteractionUtil.mc.crosshairTarget != null ? ((BlockHitResult)InteractionUtil.mc.crosshairTarget).getSide() : Direction.DOWN;
        ActionResult result = InteractionUtil.mc.interactionManager.interactBlock(InteractionUtil.mc.player, hand, new BlockHitResult(Vec3d.ofCenter((Vec3i)pos), direction, pos, false));
        if (result.shouldSwingHand()) {
            InteractionUtil.mc.player.networkHandler.sendPacket((Packet)new HandSwingC2SPacket(hand));
        }
    }

    public static boolean place(BlockPos pos, boolean airPlace) {
        return InteractionUtil.place(pos, airPlace, Hand.MAIN_HAND);
    }

    public static boolean place(BlockPos pos, boolean airPlace, Hand hand) {
        BlockPos bp;
        ActionResult result;
        if (InteractionUtil.mc.world == null || InteractionUtil.mc.player == null || InteractionUtil.mc.interactionManager == null) {
            return false;
        }
        if (!InteractionUtil.isPlaceable(pos, false)) {
            return false;
        }
        Direction direction = InteractionUtil.calcSide(pos);
        if (direction == null) {
            if (airPlace) {
                direction = InteractionUtil.mc.crosshairTarget != null ? ((BlockHitResult)InteractionUtil.mc.crosshairTarget).getSide() : Direction.DOWN;
            } else {
                return false;
            }
        }
        if ((result = InteractionUtil.mc.interactionManager.interactBlock(InteractionUtil.mc.player, hand, new BlockHitResult(airPlace ? Vec3d.ofCenter((Vec3i)pos) : Vec3d.ofCenter((Vec3i)pos).offset(direction.getOpposite(), 0.5), airPlace ? direction : direction.getOpposite(), bp = airPlace ? pos : pos.offset(direction), false))).shouldSwingHand()) {
            InteractionUtil.mc.player.networkHandler.sendPacket((Packet)new HandSwingC2SPacket(hand));
        }
        return true;
    }

    public static Direction calcSide(BlockPos pos) {
        for (Direction d : Direction.values()) {
            if (InteractionUtil.mc.world.getBlockState(pos.add(d.getVector())).isReplaceable()) continue;
            return d;
        }
        return null;
    }

    public static double getBlockBreakingSpeed(int slot, BlockPos pos) {
        return InteractionUtil.getBlockBreakingSpeed(slot, InteractionUtil.mc.world.getBlockState(pos));
    }

    public static double getBlockBreakingSpeed(int slot, BlockState block) {
        float hardness;
        ItemStack tool;
        int efficiency;
        double speed = ((ItemStack)InteractionUtil.mc.player.getInventory().main.get(slot)).getMiningSpeedMultiplier(block);
        if (speed > 1.0 && (efficiency = EnchantmentUtil.getLevel((RegistryKey<Enchantment>)Enchantments.EFFICIENCY, tool = InteractionUtil.mc.player.getInventory().getStack(slot))) > 0 && !tool.isEmpty()) {
            speed += (double)(efficiency * efficiency + 1);
        }
        if (StatusEffectUtil.hasHaste((LivingEntity)InteractionUtil.mc.player)) {
            speed *= (double)(1.0f + (float)(StatusEffectUtil.getHasteAmplifier((LivingEntity)InteractionUtil.mc.player) + 1) * 0.2f);
        }
        if (InteractionUtil.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float k = switch (InteractionUtil.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1E-4f;
            };
            speed *= (double)k;
        }
        if (InteractionUtil.mc.player.isSubmergedIn(FluidTags.WATER) && EnchantmentUtil.has((RegistryKey<Enchantment>)Enchantments.AQUA_AFFINITY, EquipmentSlot.HEAD)) {
            speed /= 5.0;
        }
        if (!InteractionUtil.mc.player.isOnGround()) {
            speed /= 5.0;
        }
        if ((hardness = block.getHardness(null, null)) == -1.0f) {
            return 0.0;
        }
        float ticks = (float)(Math.floor(1.0 / (speed /= (double)(hardness / (float)(!block.isToolRequired() || ((ItemStack)InteractionUtil.mc.player.getInventory().main.get(slot)).isSuitableFor(block) ? 30 : 100)))) + 1.0);
        return (long)(ticks / 20.0f * 1000.0f);
    }

    public static Direction right(Direction direction) {
        return switch (direction) {
            case Direction.EAST -> Direction.SOUTH;
            case Direction.SOUTH -> Direction.WEST;
            case Direction.WEST -> Direction.NORTH;
            case Direction.NORTH -> Direction.EAST;
            default -> throw new IllegalStateException("Unexpected value: " + String.valueOf(direction));
        };
    }

    public static Direction left(Direction direction) {
        return switch (direction) {
            case Direction.EAST -> Direction.NORTH;
            case Direction.NORTH -> Direction.WEST;
            case Direction.WEST -> Direction.SOUTH;
            case Direction.SOUTH -> Direction.EAST;
            default -> throw new IllegalStateException("Unexpected value: " + String.valueOf(direction));
        };
    }
}
