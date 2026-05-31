package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.WorldRenderEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={WorldRenderer.class})
public class WorldRendererMixin {
    @Inject(method={"render"}, at={@At(value="HEAD")})
    public void render(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        WorldRenderEvent worldRenderEvent = new WorldRenderEvent();
        Util.EVENT_BUS.post(worldRenderEvent);
    }
}
