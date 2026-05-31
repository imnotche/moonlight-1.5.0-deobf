package me.twerknation28.moonlight.manager;

import java.util.ArrayList;
import java.util.List;
import me.twerknation28.moonlight.manager.NetworkManager;
import me.twerknation28.moonlight.util.MathUtil;
import me.twerknation28.moonlight.util.Rotation;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;

public class RotationManager
implements Util {
    private float yaw;
    private float pitch;
    private Rotation rotation;
    private static final List<Rotation> requests = new ArrayList<Rotation>();

    public void updateRotations() {
        this.yaw = RotationManager.mc.player.getYaw();
        this.pitch = RotationManager.mc.player.getPitch();
    }

    public void restoreRotations() {
        RotationManager.mc.player.setYaw(this.yaw);
        RotationManager.mc.player.headYaw = this.yaw;
        RotationManager.mc.player.setPitch(this.pitch);
    }

    public static BlockPos from(Position vec) {
        return RotationManager.from(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BlockPos from(double x, double y, double z) {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    public static BlockPos from(float x, float y, float z) {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    public static void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.player.setYaw(yaw);
        RotationManager.mc.player.headYaw = yaw;
        RotationManager.mc.player.setPitch(pitch);
    }

    public void setPlayerYaw(float yaw) {
        RotationManager.mc.player.setYaw(yaw);
        RotationManager.mc.player.headYaw = yaw;
    }

    public static float[] getRotationsTo(Vec3d src, Vec3d dest) {
        float yaw = (float)(Math.toDegrees(Math.atan2(dest.subtract((Vec3d)src).z, dest.subtract((Vec3d)src).x)) - 90.0);
        float pitch = (float)Math.toDegrees(-Math.atan2(dest.subtract((Vec3d)src).y, Math.hypot(dest.subtract((Vec3d)src).x, dest.subtract((Vec3d)src).z)));
        return new float[]{MathHelper.wrapDegrees((float)yaw), MathHelper.wrapDegrees((float)pitch)};
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), new Vec3d((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f)));
        RotationManager.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        RotationManager.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getEyePos(), entity.getEyePos());
        RotationManager.setPlayerRotations(angle[0], angle[1]);
    }

    public static Entity getCrosshairTarget(float yaw, float pitch, float distance, boolean ignoreWalls) {
        Box box;
        Entity targetedEntity = null;
        HitResult result = ignoreWalls ? null : RotationManager.rayTrace(distance, yaw, pitch);
        Vec3d vec3d = RotationManager.mc.player.getPos().add(0.0, (double)RotationManager.mc.player.getEyeHeight(RotationManager.mc.player.getPose()), 0.0);
        double distancePow2 = Math.pow(distance, 2.0);
        if (result != null) {
            distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        }
        Vec3d vec3d2 = RotationManager.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * (double)distance, vec3d2.y * (double)distance, vec3d2.z * (double)distance);
        EntityHitResult entityHitResult = ProjectileUtil.raycast((Entity)RotationManager.mc.player, (Vec3d)vec3d, (Vec3d)vec3d3, (Box)(box = RotationManager.mc.player.getBoundingBox().stretch(vec3d2.multiply((double)distance)).expand(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() && entity.canHit(), (double)distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if ((g < distancePow2 || result == null) && entity2 instanceof LivingEntity) {
                targetedEntity = entity2;
                return targetedEntity;
            }
        }
        return targetedEntity;
    }

    @NotNull
    public static Vec3d getRotationVector(float yaw, float pitch) {
        return new Vec3d((double)(MathHelper.sin((float)(-pitch * ((float)Math.PI / 180))) * MathHelper.cos((float)(yaw * ((float)Math.PI / 180)))), (double)(-MathHelper.sin((float)(yaw * ((float)Math.PI / 180)))), (double)(MathHelper.cos((float)(-pitch * ((float)Math.PI / 180))) * MathHelper.cos((float)(yaw * ((float)Math.PI / 180)))));
    }

    public static HitResult rayTrace(double dst, float yaw, float pitch) {
        Vec3d vec3d = RotationManager.mc.player.getCameraPosVec(1.0f);
        Vec3d vec3d2 = RotationManager.getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return RotationManager.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)RotationManager.mc.player));
    }

    public static void setRotationSilentSync(boolean grim) {
        float yaw = RotationManager.mc.player.getYaw();
        float pitch = RotationManager.mc.player.getPitch();
        if (grim) {
            RotationManager.setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.Full(RotationManager.mc.player.getX(), RotationManager.mc.player.getY(), RotationManager.mc.player.getZ(), yaw, pitch, RotationManager.mc.player.isOnGround()));
        } else {
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
    }

    public static void setRotation(Rotation rotation) {
        if (rotation.getPriority() == Integer.MAX_VALUE) {
            // empty if block
        }
        Rotation finalRotation = rotation;
        Rotation request = requests.stream().filter(r -> finalRotation.getPriority() == r.getPriority()).findFirst().orElse(null);
        if (request == null) {
            requests.add(rotation);
        } else {
            request.setYaw(rotation.getYaw());
            request.setPitch(rotation.getPitch());
        }
    }

    public static void setRotationSilent(float yaw, float pitch, boolean grim) {
        if (grim) {
            RotationManager.setRotation(new Rotation(Integer.MAX_VALUE, yaw, pitch, true));
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.Full(RotationManager.mc.player.getX(), RotationManager.mc.player.getY(), RotationManager.mc.player.getZ(), yaw, pitch, RotationManager.mc.player.isOnGround()));
        } else {
            NetworkManager.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, RotationManager.mc.player.isOnGround()));
        }
    }

    public void setPlayerPitch(float pitch) {
        RotationManager.mc.player.setPitch(pitch);
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
