package me.twerknation28.moonlight.features.modules.movement;

import me.twerknation28.moonlight.mixin.accessor.AccessorLivingEntity;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public final class NoJumpDelay extends Module
{
    public NoJumpDelay() {
        super("NoJumpDelay", "Removes delay from jumping", Category.PLAYER, true, false, true);
    }
    
    @Override
    public void onUpdate() {
        assert NoJumpDelay.mc.player != null;
        ((AccessorLivingEntity)NoJumpDelay.mc.player).setLastJumpCooldown(0);
    }
}
