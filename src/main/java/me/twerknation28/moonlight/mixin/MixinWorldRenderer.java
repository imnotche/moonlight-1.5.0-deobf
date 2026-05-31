package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.util.math.RotationAxis;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.joml.Matrix4f;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ WorldRenderer.class })
public class MixinWorldRenderer
{
    @Inject(method = { "render" }, at = { @At("RETURN") })
    private void render(final RenderTickCounter tickCounter, final boolean renderBlockOutline, final Camera camera, final GameRenderer gameRenderer, final LightmapTextureManager lightmapTextureManager, final Matrix4f matrix4f, final Matrix4f matrix4f2, final CallbackInfo ci, @Local final MatrixStack stack) {
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Util.mc.gameRenderer.getCamera().getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Util.mc.gameRenderer.getCamera().getYaw() + 180.0f));
        MinecraftClient.getInstance().getProfiler().push("moonlight-render-3d");
        RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
        final Render3DEvent event = new Render3DEvent(stack, tickCounter.getTickDelta(true));
        Util.EVENT_BUS.post(event);
        MinecraftClient.getInstance().getProfiler().pop();
    }
    
    @Inject(method = { "drawEntityOutlinesFramebuffer" }, at = { @At("HEAD") }, cancellable = true)
    public void drawEntityOutlinesFramebufferHook(final CallbackInfo ci) {
        if (NoRender.getInstance().glow.getValue() && NoRender.getInstance().isEnabled()) {
            ci.cancel();
        }
    }
}
