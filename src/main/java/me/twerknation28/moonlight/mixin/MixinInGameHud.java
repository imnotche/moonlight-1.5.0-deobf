package me.twerknation28.moonlight.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={InGameHud.class})
public class MixinInGameHud {
    @Inject(method={"render"}, at={@At(value="RETURN")})
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud()) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((int)770, (int)771);
        RenderSystem.disableCull();
        GL11.glEnable((int)2848);
        Render2DEvent event = new Render2DEvent(context, tickCounter.getTickDelta(true));
        Util.EVENT_BUS.post(event);
        RenderSystem.enableDepthTest();
        GL11.glDisable((int)2848);
    }
}
