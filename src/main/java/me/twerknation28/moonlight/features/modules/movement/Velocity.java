package me.twerknation28.moonlight.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.MathUtil;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class Velocity
extends Module {
    public Setting<veloMode> velocityMode = this.register(new Setting<veloMode>("Mode", veloMode.anarchy));
    public Setting<Float> jumpChance = this.register(new Setting<Float>("Chance", Float.valueOf(100.0f), Float.valueOf(1.0f), Float.valueOf(100.0f), Float.valueOf(1.0f), v -> this.velocityMode.getValue() == veloMode.jump));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", Boolean.valueOf(true), v -> this.velocityMode.getValue() == veloMode.anarchy));
    public static Velocity INSTANCE;

    public Velocity() {
        super("Velocity", "Reduces knockback", Category.PLAYER, true, false, false);
        INSTANCE = this;
    }

    @Override
    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if ((event.getPacket() instanceof EntityVelocityUpdateS2CPacket || event.getPacket() instanceof ExplosionS2CPacket) && this.velocityMode.getValue() == veloMode.anarchy) {
            event.cancel();
        }
    }

    @Override
    public void onTick() {
        if (!(Velocity.fullNullCheck() || this.velocityMode.getValue() != veloMode.jump || Velocity.mc.player.isBlocking() || Velocity.mc.player.isUsingItem() || Velocity.mc.currentScreen instanceof HandledScreen || !Velocity.mc.player.isOnGround() || Velocity.mc.player.maxHurtTime == 0 || Velocity.mc.player.hurtTime == 0 || !(Velocity.mc.player.getAttacker() instanceof PlayerEntity) || Velocity.mc.player.isInsideWaterOrBubbleColumn() || Velocity.mc.player.isInsideWall() || Velocity.mc.player.isTouchingWater())) {
            float chance = this.jumpChance.getValue().floatValue();
            int randomNumber = MathUtil.getRandomInt(100, 1);
            if (Velocity.mc.player.hurtTime == Velocity.mc.player.maxHurtTime - 1 && (float)randomNumber <= chance) {
                Velocity.mc.player.jump();
            }
        }
    }

    public String getMetadata() {
        return String.valueOf(this.jumpChance.getValue()) + "%";
    }

    public static enum veloMode {
        anarchy,
        jump;

    }
}
