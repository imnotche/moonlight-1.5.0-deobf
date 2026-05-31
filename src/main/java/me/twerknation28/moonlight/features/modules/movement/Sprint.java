package me.twerknation28.moonlight.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.UpdateWalkingPlayerEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;

public class Sprint
extends Module {
    public Setting<sprintMode> mode = this.register(new Setting<sprintMode>("Mode", sprintMode.LEGIT));

    public Sprint() {
        super("Sprint", "Automatically sprints", Category.PLAYER, true, false, false);
    }

    @Subscribe
    private void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.mode.getValue() == sprintMode.RAGE) {
            if (Sprint.mc.player.forwardSpeed != 0.0f || Sprint.mc.player.horizontalSpeed != 0.0f) {
                Sprint.mc.player.setSprinting(true);
            }
        } else if (Sprint.mc.player.forwardSpeed >= 0.8f && (float)Sprint.mc.player.getHungerManager().getFoodLevel() > 6.0f) {
            Sprint.mc.player.setSprinting(true);
        }
    }

    public static enum sprintMode {
        LEGIT,
        RAGE;

    }
}
