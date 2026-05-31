package me.twerknation28.moonlight.util;

import net.minecraft.entity.Entity;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class FakePlayerEntity extends OtherClientPlayerEntity implements Util
{
    public static final AtomicInteger CURRENT_ID;
    private final PlayerEntity player;

    public FakePlayerEntity(final PlayerEntity player, final String name) {
        super(MinecraftClient.getInstance().world, new GameProfile(UUID.fromString("2c83d964-298e-4559-b1bf-314f9ad63f7b"), name));
        this.copyPositionAndRotation((Entity)(this.player = player));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.headYaw = player.headYaw;
        this.prevHeadYaw = this.headYaw;
        this.bodyYaw = player.bodyYaw;
        this.prevBodyYaw = this.bodyYaw;
        final Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(FakePlayerEntity.CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(final PlayerEntity player, final GameProfile profile) {
        super(MinecraftClient.getInstance().world, profile);
        this.copyPositionAndRotation((Entity)(this.player = player));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.headYaw = player.headYaw;
        this.prevHeadYaw = this.headYaw;
        this.bodyYaw = player.bodyYaw;
        this.prevBodyYaw = this.bodyYaw;
        final Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(FakePlayerEntity.CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(final PlayerEntity player) {
        this(player, player.getName().getString());
    }

    public void spawnPlayer() {
        if (FakePlayerEntity.mc.world != null) {
            this.unsetRemoved();
            FakePlayerEntity.mc.world.addEntity((Entity)this);
        }
    }

    public void despawnPlayer() {
        if (FakePlayerEntity.mc.world != null) {
            FakePlayerEntity.mc.world.removeEntity(this.getId(), Entity.RemovalReason.DISCARDED);
            this.setRemoved(Entity.RemovalReason.DISCARDED);
        }
    }

    public boolean isDead() {
        return false;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    static {
        CURRENT_ID = new AtomicInteger(1000000);
    }
}
