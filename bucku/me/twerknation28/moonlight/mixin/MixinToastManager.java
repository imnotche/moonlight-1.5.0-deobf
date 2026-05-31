package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ToastManager.class})
public abstract class MixinToastManager {
    @Inject(method={"draw"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawHook(DrawContext context, CallbackInfo ci) {
        if (NoRender.getInstance().toasts.getValue().booleanValue() && NoRender.getInstance().isEnabled()) {
            ci.cancel();
        }
    }
}
