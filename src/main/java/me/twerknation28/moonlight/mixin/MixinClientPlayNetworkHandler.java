package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.ChatEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientPlayNetworkHandler.class })
public class MixinClientPlayNetworkHandler
{
    @Inject(method = { "sendChatMessage" }, at = { @At("HEAD") }, cancellable = true)
    private void sendChatMessageHook(final String content, final CallbackInfo ci) {
        final ChatEvent event = new ChatEvent(content);
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
