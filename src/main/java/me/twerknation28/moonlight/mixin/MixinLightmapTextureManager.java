package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LightmapTextureManager.class})
public class MixinLightmapTextureManager {
    @Inject(method={"getBrightness"}, at={@At(value="HEAD")}, cancellable=true)
    private static void getBrightnessHook(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> cir) {
        if (Fullbright.getInstance().isOn()) {
            float f = (float)lightLevel / 15.0f;
            float g = f / (4.0f - 3.0f * f);
            cir.setReturnValue((Object)Float.valueOf(Math.max(MathHelper.lerp((float)type.ambientLight(), (float)g, (float)1.0f), 0.5f)));
        }
    }
}
