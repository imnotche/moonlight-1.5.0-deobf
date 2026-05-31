package me.twerknation28.moonlight.features.modules.movement;

import me.twerknation28.moonlight.util.MathUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import me.twerknation28.moonlight.features.Feature;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Velocity extends Module
{
    public Setting<veloMode> velocityMode;
    public Setting<Float> jumpChance;
    public Setting<Boolean> blocks;
    public static Velocity INSTANCE;
    
    public Velocity() {
        super("Velocity", "Reduces knockback", Category.PLAYER, true, false, false);
        this.velocityMode = this.register(new Setting<veloMode>("Mode", veloMode.anarchy));
        this.jumpChance = this.register(new Setting<Float>("Chance", 100.0f, 1.0f, 100.0f, 1.0f, v -> this.velocityMode.getValue() == veloMode.jump));
        this.blocks = this.register(new Setting<Boolean>("Blocks", true, v -> this.velocityMode.getValue() == veloMode.anarchy));
        Velocity.INSTANCE = this;
    }
    
    @Subscribe
    @Override
    public void onPacketReceive(final PacketEvent.Receive event) {
        if ((event.getPacket() instanceof EntityVelocityUpdateS2CPacket || event.getPacket() instanceof ExplosionS2CPacket) && this.velocityMode.getValue() == veloMode.anarchy) {
            event.cancel();
        }
    }
    
    @Override
    public void onTick() {
        if (!Feature.fullNullCheck() && this.velocityMode.getValue() == veloMode.jump && !Velocity.mc.player.isBlocking() && !Velocity.mc.player.isUsingItem() && !(Velocity.mc.currentScreen instanceof HandledScreen) && Velocity.mc.player.isOnGround() && Velocity.mc.player.maxHurtTime != 0 && Velocity.mc.player.hurtTime != 0 && Velocity.mc.player.getAttacker() instanceof PlayerEntity && !Velocity.mc.player.isInsideWaterOrBubbleColumn() && !Velocity.mc.player.isInsideWall() && !Velocity.mc.player.isTouchingWater()) {
            final float chance = this.jumpChance.getValue();
            final int randomNumber = MathUtil.getRandomInt(100, 1);
            if (Velocity.mc.player.hurtTime == Velocity.mc.player.maxHurtTime - 1 && randomNumber <= chance) {
                Velocity.mc.player.jump();
            }
        }
    }
    
    public String getMetadata() {
        return String.valueOf(this.jumpChance.getValue());
    }
    
    public enum veloMode
    {
        anarchy, 
        jump;
    }
}
