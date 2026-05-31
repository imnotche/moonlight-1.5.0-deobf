package me.twerknation28.moonlight.features.modules.movement;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public class ReverseStep
extends Module {
    public ReverseStep() {
        super("ReverseStep", "step but reversed..", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (ReverseStep.nullCheck()) {
            return;
        }
        if (ReverseStep.mc.player.isInLava() || ReverseStep.mc.player.isTouchingWater() || !ReverseStep.mc.player.isOnGround()) {
            return;
        }
        ReverseStep.mc.player.addVelocity(0.0, -1.0, 0.0);
    }
}
