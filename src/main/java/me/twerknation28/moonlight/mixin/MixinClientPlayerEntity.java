package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.movement.Velocity;
import me.twerknation28.moonlight.event.impl.UpdateWalkingPlayerEvent;
import me.twerknation28.moonlight.event.Stage;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import me.twerknation28.moonlight.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientPlayerEntity.class })
public class MixinClientPlayerEntity
{
    @Inject(method = { "tick" }, at = { @At("TAIL") })
    private void tickHook(final CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateEvent());
    }
    
    @Inject(method = { "tick" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER) })
    private void tickHook2(final CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateWalkingPlayerEvent(Stage.PRE));
    }
    
    @Inject(method = { "tick" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift = At.Shift.AFTER) })
    private void tickHook3(final CallbackInfo ci) {
        Util.EVENT_BUS.post(new UpdateWalkingPlayerEvent(Stage.POST));
    }
    
    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    private void onPushOutOfBlocks(final double x, final double d, final CallbackInfo info) {
        if (Velocity.INSTANCE.isOn() && Velocity.INSTANCE.blocks.getValue() && Velocity.INSTANCE.velocityMode.getValue() == Velocity.veloMode.anarchy) {
            info.cancel();
        }
    }
}
