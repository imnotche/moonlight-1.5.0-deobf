package me.twerknation28.moonlight.features.modules.render;

import java.util.Iterator;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class DeathEffects extends Module
{
    public final Setting<Boolean> sound;
    
    public DeathEffects() {
        super("DeathEffects", "Effects when you death.", Category.RENDER, true, false, false);
        this.sound = this.register(new Setting<Boolean>("Sound", true));
    }
    
    @Override
    public void onTick() {
        for (final PlayerEntity player : DeathEffects.mc.world.getPlayers()) {
            if (player.isDead() || player.getHealth() == 0.0f) {
                int i = 0;
                if (i == 0) {
                    final LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, (World)DeathEffects.mc.world);
                    bolt.updatePositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                    DeathEffects.mc.world.addEntity((Entity)bolt);
                    player.remove(Entity.RemovalReason.KILLED);
                    ++i;
                }
                if (!this.sound.getValue()) {
                    continue;
                }
                DeathEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
            }
        }
    }
}
