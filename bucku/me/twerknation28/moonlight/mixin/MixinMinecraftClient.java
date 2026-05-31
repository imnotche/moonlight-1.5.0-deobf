package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.AttackEvent;
import me.twerknation28.moonlight.event.impl.HandleBlockBreakingEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={MinecraftClient.class})
public class MixinMinecraftClient {
    @Inject(method={"handleBlockBreaking"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleBlockBreakingHook(boolean breaking, CallbackInfo ci) {
        HandleBlockBreakingEvent event = new HandleBlockBreakingEvent();
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method={"doAttack"}, at={@At(value="HEAD")}, cancellable=true)
    private void doAttackHook(CallbackInfoReturnable<Boolean> cir) {
        AttackEvent event = new AttackEvent(null, true);
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue((Object)false);
        }
    }
}
