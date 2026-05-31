package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.RenderLabelEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityRenderer.class })
public class MixinEntityRenderer
{
    @Inject(method = { "renderLabelIfPresent" }, at = { @At("HEAD") }, cancellable = true)
    public void renderLabelIfPresent(final Entity entity, final Text text, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final float tickDelta, final CallbackInfo ci) {
        final RenderLabelEvent renderLabelEvent = new RenderLabelEvent(entity);
        Util.EVENT_BUS.post(renderLabelEvent);
        if (renderLabelEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
