package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.event.impl.PlayerMoveEvent;
import me.twerknation28.moonlight.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Entity.class })
public class MixinEntity
{
    @Final
    @Shadow
    protected DataTracker dataTracker;
    
    @Inject(method = { "move" }, at = { @At("HEAD") })
    private void onMove(final MovementType type, final Vec3d movement, final CallbackInfo info) {
        if ((Object) this == Util.mc.player) {
            final PlayerMoveEvent event = new PlayerMoveEvent();
            Util.EVENT_BUS.post(event);
        }
    }
}
