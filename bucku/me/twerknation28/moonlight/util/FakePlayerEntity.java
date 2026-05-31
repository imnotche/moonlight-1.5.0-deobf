package me.twerknation28.moonlight.util;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class FakePlayerEntity
extends OtherClientPlayerEntity
implements Util {
    public static final AtomicInteger CURRENT_ID = new AtomicInteger(1000000);
    private final PlayerEntity player;

    public FakePlayerEntity(PlayerEntity player, String name) {
        super(MinecraftClient.getInstance().world, new GameProfile(UUID.fromString("2c83d964-298e-4559-b1bf-314f9ad63f7b"), name));
        this.player = player;
        this.copyPositionAndRotation((Entity)player);
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.prevHeadYaw = this.headYaw = player.headYaw;
        this.prevBodyYaw = this.bodyYaw = player.bodyYaw;
        Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, (Object)playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(PlayerEntity player, GameProfile profile) {
        super(MinecraftClient.getInstance().world, profile);
        this.player = player;
        this.copyPositionAndRotation((Entity)player);
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
        this.prevHeadYaw = this.headYaw = player.headYaw;
        this.prevBodyYaw = this.bodyYaw = player.bodyYaw;
        Byte playerModel = (Byte)player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        this.dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, (Object)playerModel);
        this.getAttributes().setFrom(player.getAttributes());
        this.setPose(player.getPose());
        this.setHealth(player.getHealth());
        this.setAbsorptionAmount(player.getAbsorptionAmount());
        this.getInventory().clone(player.getInventory());
        this.setId(CURRENT_ID.incrementAndGet());
        this.age = 100;
    }

    public FakePlayerEntity(PlayerEntity player) {
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
}
