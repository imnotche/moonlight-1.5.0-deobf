package me.twerknation28.moonlight.manager;

import java.util.ArrayList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.Entity;
import me.twerknation28.moonlight.util.MathUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import java.util.List;
import me.twerknation28.moonlight.util.Rotation;
import me.twerknation28.moonlight.util.Util;

public class RotationManager implements Util
{
    private float yaw;
    private float pitch;
    private Rotation rotation;
    private static final List<Rotation> requests;
    
    public void updateRotations() {
        this.yaw = RotationManager.mc.player.getYaw();
        this.pitch = RotationManager.mc.player.getPitch();
    }
    
    public void restoreRotations() {
        RotationManager.mc.player.setYaw(this.yaw);
        RotationManager.mc.player.headYaw = this.yaw;
        RotationManager.mc.player.setPitch(this.pitch);
    }
    
    public static BlockPos from(final Position vec) {
        return from(vec.getX(), vec.getY(), vec.getZ());
    }
    
    public static BlockPos from(final double x, final double y, final double z) {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }
    
    public static BlockPos from(final float x, final float y, final float z) {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }
    
    public static void setPlayerRotations(final float yaw, final float pitch) {
        RotationManager.mc.player.setYaw(yaw);
        RotationManager.mc.player.headYaw = yaw;
        RotationManager.mc.player.setPitch(pitch);
    }
    
    public void setPlayerYaw(final float yaw) {
        RotationManager.mc.player.setYaw(yaw);
        RotationManager.mc.player.headYaw = yaw;
    }
    
    public static float[] getRotationsTo(final Vec3d src, final Vec3d dest) {
        final float yaw = (float)(Math.toDegrees(Math.atan2(dest.subtract(src).z, dest.subtract(src).x)) - 90.0);
        final float pitch = (float)Math.toDegrees(-Math.atan2(dest.subtract(src).y, Math.hypot(dest.subtract(src).x, dest.subtract(src).z)));
        return new float[] { MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch) };
    }
    
    public void lookAtPos(final BlockPos pos) {
        final float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public void lookAtVec3d(final Vec3d vec3d) {
        final float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public void lookAtVec3d(final double x, final double y, final double z) {
        final Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }
    
    public void lookAtEntity(final Entity entity) {
        final float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), entity.getEyePos());
        setPlayerRotations(angle[0], angle[1]);
    }
    
    public static Entity getCrosshairTarget(final float yaw, final float pitch, final float distance, final boolean ignoreWalls) {
        Entity targetedEntity = null;
        final HitResult result = ignoreWalls ? null : rayTrace(distance, yaw, pitch);
        final Vec3d vec3d = RotationManager.mc.player.getPos().add(0.0, (double)RotationManager.mc.player.getEyeHeight(RotationManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        if (result != null) {
            distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        }
        final Vec3d vec3d2 = getRotationVector(pitch, yaw);
        final Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        final Box box = RotationManager.mc.player.getBoundingBox().stretch(vec3d2.multiply((double)distance)).expand(1.0, 1.0, 1.0);
        final EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)RotationManager.mc.player, vec3d, vec3d3, box, entity -> !entity.isSpectator() && entity.canHit(), distancePow2);
        if (entityHitResult != null) {
            final Entity entity2 = entityHitResult.getEntity();
            final Vec3d vec3d4 = entityHitResult.getPos();
            final double g = vec3d.squaredDistanceTo(vec3d4);
            if ((g < distancePow2 || result == null) && entity2 instanceof LivingEntity) {
                targetedEntity = entity2;
                return targetedEntity;
            }
        }
        return targetedEntity;
    }
    
    @NotNull
    public static Vec3d getRotationVector(final float yaw, final float pitch) {
        return new Vec3d((double)(MathHelper.sin(-pitch * 0.017453292f) * MathHelper.cos(yaw * 0.017453292f)), (double)(-MathHelper.sin(yaw * 0.017453292f)), (double)(MathHelper.cos(-pitch * 0.017453292f) * MathHelper.cos(yaw * 0.017453292f)));
    }
    
    public static HitResult rayTrace(final double dst, final float yaw, final float pitch) {
        final Vec3d vec3d = RotationManager.mc.player.getCameraPosVec(1.0f);
        final Vec3d vec3d2 = getRotationVector(pitch, yaw);
        final Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return (HitResult)RotationManager.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)RotationManager.mc.player));
    }
    
    public static void setRotationSilentSync(final boolean grim) {
        final float yaw = RotationManager.mc.player.getYaw();
        final float pitch = RotationManager.mc.player.getPitch();
        if (grim) {
            setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket((Packet<?>)new PlayerMoveC2SPacket.Full(RotationManager.mc.player.getX(), RotationManager.mc.player.getY(), RotationManager.mc.player.getZ(), yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
        else {
            NetworkManager.sendPacket((Packet<?>)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
    }
    
    public static void setRotation(Rotation rotation) {
        if (rotation.getPriority() == Integer.MAX_VALUE) {
            rotation = rotation;
        }
        final Rotation finalRotation = rotation;
        final Rotation request = RotationManager.requests.stream().filter(r -> finalRotation.getPriority() == r.getPriority()).findFirst().orElse(null);
        if (request == null) {
            RotationManager.requests.add(rotation);
        }
        else {
            request.setYaw(rotation.getYaw());
            request.setPitch(rotation.getPitch());
        }
    }
    
    public static void setRotationSilent(final float yaw, final float pitch, final boolean grim) {
        if (grim) {
            setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket((Packet<?>)new PlayerMoveC2SPacket.Full(RotationManager.mc.player.getX(), RotationManager.mc.player.getY(), RotationManager.mc.player.getZ(), yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
        else {
            NetworkManager.sendPacket((Packet<?>)new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
    }
    
    public void setPlayerPitch(final float pitch) {
        RotationManager.mc.player.setPitch(pitch);
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public void setYaw(final float yaw) {
        this.yaw = yaw;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
    
    static {
        requests = new ArrayList<Rotation>();
    }
}
