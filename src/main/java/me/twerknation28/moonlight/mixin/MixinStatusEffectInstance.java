package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ StatusEffectInstance.class })
public class MixinStatusEffectInstance
{
    @Inject(at = { @At("HEAD") }, method = { "shouldShowIcon" }, cancellable = true)
    private void init(final CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().potionHUD.getValue()) {
            cir.setReturnValue(false);
        }
    }
}
