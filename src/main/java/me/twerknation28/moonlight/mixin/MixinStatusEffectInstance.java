package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.NoRender;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={StatusEffectInstance.class})
public class MixinStatusEffectInstance {
    @Inject(at={@At(value="HEAD")}, method={"shouldShowIcon"}, cancellable=true)
    private void init(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().potionHUD.getValue().booleanValue()) {
            cir.setReturnValue((Object)false);
        }
    }
}
