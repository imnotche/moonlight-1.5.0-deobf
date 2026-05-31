package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DeathEffects
extends Module {
    public final Setting<Boolean> sound = this.register(new Setting<Boolean>("Sound", true));

    public DeathEffects() {
        super("DeathEffects", "Effects when you death.", Category.RENDER, true, false, false);
    }

    @Override
    public void onTick() {
        for (PlayerEntity player : DeathEffects.mc.world.getPlayers()) {
            if (!player.isDead() && player.getHealth() != 0.0f) continue;
            int i = 0;
            if (i == 0) {
                LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, (World)DeathEffects.mc.world);
                bolt.updatePositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                DeathEffects.mc.world.addEntity((Entity)bolt);
                player.remove(Entity.RemovalReason.KILLED);
                ++i;
            }
            if (!this.sound.getValue().booleanValue()) continue;
            DeathEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
        }
    }
}
