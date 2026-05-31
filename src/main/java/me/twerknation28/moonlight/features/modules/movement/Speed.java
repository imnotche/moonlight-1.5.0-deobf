package me.twerknation28.moonlight.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.PlayerMoveEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Speed extends Module
{
    private boolean jumping;
    public Setting<speedMode> mode;
    public Setting<Float> speedFactor;
    public Setting<Boolean> autoJump;
    
    public Speed() {
        super("Speed", "Makes you faster", Category.PLAYER, true, false, false);
        this.mode = this.register(new Setting<speedMode>("Mode", speedMode.OnGround));
        this.speedFactor = this.register(new Setting<Float>("Speed", 1.0f, 1.0f, 30.0f, 1.0f));
        this.autoJump = this.register(new Setting<Boolean>("AutoJump", true, v -> this.mode.getValue() == speedMode.OnGround));
    }
    
    @Subscribe
    public void onMove(final PlayerMoveEvent event) {
        if (Speed.mc.options.jumpKey.isPressed() || Speed.mc.player.fallDistance > 0.25) {
            return;
        }
        final float speed = 1.0f + this.speedFactor.getValue() / 30.0f;
        if (this.jumping && Speed.mc.player.getY() >= Speed.mc.player.prevY + 0.399994) {
            Speed.mc.player.setVelocity(Speed.mc.player.getVelocity().x, -0.9, Speed.mc.player.getVelocity().z);
            Speed.mc.player.setPos(Speed.mc.player.getX(), Speed.mc.player.prevY, Speed.mc.player.getZ());
            this.jumping = false;
        }
        if (Speed.mc.player.forwardSpeed != 0.0f && !Speed.mc.player.horizontalCollision) {
            if (Speed.mc.player.verticalCollision) {
                Speed.mc.player.setVelocity(Speed.mc.player.getVelocity().x * speed, Speed.mc.player.getVelocity().y, Speed.mc.player.getVelocity().z * speed);
                if (this.autoJump.getValue()) {
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
    
    public enum speedMode
    {
        OnGround;
    }
}
