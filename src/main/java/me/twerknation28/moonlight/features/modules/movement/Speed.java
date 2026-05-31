package me.twerknation28.moonlight.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.PlayerMoveEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;

public class Speed
extends Module {
    private boolean jumping;
    public Setting<speedMode> mode = this.register(new Setting<speedMode>("Mode", speedMode.OnGround));
    public Setting<Float> speedFactor = this.register(new Setting<Float>("Speed", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(30.0f), Float.valueOf(1.0f)));
    public Setting<Boolean> autoJump = this.register(new Setting<Boolean>("AutoJump", Boolean.valueOf(true), v -> this.mode.getValue() == speedMode.OnGround));

    public Speed() {
        super("Speed", "Makes you faster", Category.PLAYER, true, false, false);
    }

    @Subscribe
    public void onMove(PlayerMoveEvent event) {
        if (Speed.mc.options.jumpKey.isPressed() || (double)Speed.mc.player.fallDistance > 0.25) {
            return;
        }
        float speed = 1.0f + this.speedFactor.getValue().floatValue() / 30.0f;
        if (this.jumping && Speed.mc.player.getY() >= Speed.mc.player.prevY + 0.399994) {
            Speed.mc.player.setVelocity(Speed.mc.player.getVelocity().x, -0.9, Speed.mc.player.getVelocity().z);
            Speed.mc.player.setPos(Speed.mc.player.getX(), Speed.mc.player.prevY, Speed.mc.player.getZ());
            this.jumping = false;
        }
        if (Speed.mc.player.forwardSpeed != 0.0f && !Speed.mc.player.horizontalCollision) {
            if (Speed.mc.player.verticalCollision) {
                Speed.mc.player.setVelocity(Speed.mc.player.getVelocity().x * (double)speed, Speed.mc.player.getVelocity().y, Speed.mc.player.getVelocity().z * (double)speed);
                if (this.autoJump.getValue().booleanValue()) {
                    this.jumping = true;
                    Speed.mc.player.jump();
                }
            }
            if (this.jumping && Speed.mc.player.getY() >= Speed.mc.player.prevY + 0.399994) {
                Speed.mc.player.setVelocity(Speed.mc.player.getVelocity().x, -100.0, Speed.mc.player.getVelocity().z);
                this.jumping = false;
            }
        }
    }

    public static enum speedMode {
        OnGround;

    }
}
