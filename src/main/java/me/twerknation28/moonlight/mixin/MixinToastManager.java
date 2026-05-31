package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ToastManager.class })
public abstract class MixinToastManager
{
    @Inject(method = { "draw" }, at = { @At("HEAD") }, cancellable = true)
    public void drawHook(final DrawContext context, final CallbackInfo ci) {
        if (NoRender.getInstance().toasts.getValue() && NoRender.getInstance().isEnabled()) {
            ci.cancel();
        }
    }
}
