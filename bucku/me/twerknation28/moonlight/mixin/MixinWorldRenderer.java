package me.twerknation28.moonlight.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={WorldRenderer.class})
public class MixinWorldRenderer {
    @Inject(method={"render"}, at={@At(value="RETURN")})
    private void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci, @Local MatrixStack stack) {
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Util.mc.gameRenderer.getCamera().getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Util.mc.gameRenderer.getCamera().getYaw() + 180.0f));
        MinecraftClient.getInstance().getProfiler().push("moonlight-render-3d");
        RenderSystem.clear((int)256, (boolean)MinecraftClient.IS_SYSTEM_MAC);
        Render3DEvent event = new Render3DEvent(stack, tickCounter.getTickDelta(true));
        Util.EVENT_BUS.post(event);
        MinecraftClient.getInstance().getProfiler().pop();
    }

    @Inject(method={"drawEntityOutlinesFramebuffer"}, at={@At(value="HEAD")}, cancellable=true)
    public void drawEntityOutlinesFramebufferHook(CallbackInfo ci) {
        if (NoRender.getInstance().glow.getValue().booleanValue() && NoRender.getInstance().isEnabled()) {
            ci.cancel();
        }
    }
}
