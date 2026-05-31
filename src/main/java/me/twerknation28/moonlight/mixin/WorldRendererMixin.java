package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.WorldRenderEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.joml.Matrix4f;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ WorldRenderer.class })
public class WorldRendererMixin
{
    @Inject(method = { "render" }, at = { @At("HEAD") })
    public void render(final RenderTickCounter tickCounter, final boolean renderBlockOutline, final Camera camera, final GameRenderer gameRenderer, final LightmapTextureManager lightmapTextureManager, final Matrix4f matrix4f, final Matrix4f matrix4f2, final CallbackInfo ci) {
        final WorldRenderEvent worldRenderEvent = new WorldRenderEvent();
        Util.EVENT_BUS.post(worldRenderEvent);
    }
}
