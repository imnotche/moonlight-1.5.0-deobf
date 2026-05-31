package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.RenderLabelEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public class MixinEntityRenderer {
    @Inject(method={"renderLabelIfPresent"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderLabelIfPresent(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
        RenderLabelEvent renderLabelEvent = new RenderLabelEvent(entity);
        Util.EVENT_BUS.post(renderLabelEvent);
        if (renderLabelEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
