package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.Moonlight;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderTickCounter.Dynamic.class })
public class MixinRenderTickCounter
{
    @Shadow
    private float lastFrameDuration;
    
    @Inject(method = { "beginRenderTick(J)I" }, at = { @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;prevTimeMillis:J") })
    public void beginRenderTick(final long timeMillis, final CallbackInfoReturnable<Integer> cir) {
        this.lastFrameDuration *= Moonlight.TIMER;
    }
}
