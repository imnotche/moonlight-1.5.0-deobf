package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={InGameOverlayRenderer.class})
public class MixinInGameOverlayRenderer {
    @Inject(method={"renderFireOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    private static void onRenderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fireOverlay.getValue().booleanValue()) {
            info.cancel();
        }
    }
}
