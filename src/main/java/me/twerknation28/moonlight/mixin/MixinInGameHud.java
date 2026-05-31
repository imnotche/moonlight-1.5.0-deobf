package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import org.lwjgl.opengl.GL11;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ InGameHud.class })
public class MixinInGameHud
{
    @Inject(method = { "render" }, at = { @At("RETURN") })
    public void render(final DrawContext context, final RenderTickCounter tickCounter, final CallbackInfo ci) {
        if (MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud()) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.disableCull();
        GL11.glEnable(2848);
        final Render2DEvent event = new Render2DEvent(context, tickCounter.getTickDelta(true));
        Util.EVENT_BUS.post(event);
        RenderSystem.enableDepthTest();
        GL11.glDisable(2848);
    }
}
