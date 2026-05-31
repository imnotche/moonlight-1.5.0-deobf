package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.util.math.MathHelper;
import me.twerknation28.moonlight.features.modules.render.Fullbright;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ LightmapTextureManager.class })
public class MixinLightmapTextureManager
{
    @Inject(method = { "getBrightness" }, at = { @At("HEAD") }, cancellable = true)
    private static void getBrightnessHook(final DimensionType type, final int lightLevel, final CallbackInfoReturnable<Float> cir) {
        if (Fullbright.getInstance().isOn()) {
            final float f = lightLevel / 15.0f;
            final float g = f / (4.0f - 3.0f * f);
            cir.setReturnValue(Math.max(MathHelper.lerp(type.ambientLight(), g, 1.0f), 0.5f));
        }
    }
}
