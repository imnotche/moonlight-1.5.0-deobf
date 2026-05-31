package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.AttackEvent;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.HandleBlockBreakingEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ MinecraftClient.class })
public class MixinMinecraftClient
{
    @Inject(method = { "handleBlockBreaking" }, at = { @At("HEAD") }, cancellable = true)
    private void handleBlockBreakingHook(final boolean breaking, final CallbackInfo ci) {
        final HandleBlockBreakingEvent event = new HandleBlockBreakingEvent();
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    
    @Inject(method = { "doAttack" }, at = { @At("HEAD") }, cancellable = true)
    private void doAttackHook(final CallbackInfoReturnable<Boolean> cir) {
        final AttackEvent event = new AttackEvent(null, true);
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
