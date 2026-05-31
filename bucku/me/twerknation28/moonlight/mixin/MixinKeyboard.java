package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.KeyEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Keyboard.class})
public class MixinKeyboard {
    @Inject(method={"onKey"}, at={@At(value="TAIL")}, cancellable=true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        KeyEvent event = new KeyEvent(key);
        Util.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
