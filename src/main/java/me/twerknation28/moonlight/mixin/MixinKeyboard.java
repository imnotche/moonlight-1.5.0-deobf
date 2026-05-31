package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.KeyEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Keyboard.class })
public class MixinKeyboard
{
    @Inject(method = { "onKey" }, at = { @At("TAIL") }, cancellable = true)
    private void onKey(final long windowPointer, final int key, final int scanCode, final int action, final int modifiers, final CallbackInfo ci) {
        final KeyEvent event = new KeyEvent(key);
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
