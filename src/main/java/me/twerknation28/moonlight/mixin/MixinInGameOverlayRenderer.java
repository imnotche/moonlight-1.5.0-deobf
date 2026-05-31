package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ InGameOverlayRenderer.class })
public class MixinInGameOverlayRenderer
{
    @Inject(method = { "renderFireOverlay" }, at = { @At("HEAD") }, cancellable = true)
    private static void onRenderFireOverlay(final MinecraftClient minecraftClient, final MatrixStack matrixStack, final CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fireOverlay.getValue()) {
            info.cancel();
        }
    }
}
