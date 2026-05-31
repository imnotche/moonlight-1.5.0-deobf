package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.Stage;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import me.twerknation28.moonlight.event.impl.UpdateWalkingPlayerEvent;
import me.twerknation28.moonlight.features.modules.movement.Velocity;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPlayerEntity.class})
public class MixinClientPlayerEntity {
    @Inject(method={"tick"}, at={@At(value="TAIL")})
    private void tickHook(CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateEvent());
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift=At.Shift.AFTER)})
    private void tickHook2(CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateWalkingPlayerEvent(Stage.PRE));
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift=At.Shift.AFTER)})
    private void tickHook3(CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateWalkingPlayerEvent(Stage.POST));
    }

    @Inject(method={"pushOutOfBlocks"}, at={@At(value="HEAD")}, cancellable=true)
    private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
        if (Velocity.INSTANCE.isOn() && Velocity.INSTANCE.blocks.getValue().booleanValue() && Velocity.INSTANCE.velocityMode.getValue() == Velocity.veloMode.anarchy) {
            info.cancel();
        }
    }
}
