package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.PlayerMoveEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class})
public class MixinEntity {
    @Final
    @Shadow
    protected DataTracker dataTracker;

    @Inject(method={"move"}, at={@At(value="HEAD")})
    private void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
        if (this == Util.mc.player) {
            PlayerMoveEvent event = new PlayerMoveEvent();
            Util.EVENT_BUS.post(event);
        }
    }
}
