package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import me.twerknation28.moonlight.util.Util;

@Mixin({ TextVisitFactory.class })
public class MixinTextVisitFactory implements Util
{
    @ModifyArg(method = { "visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"))
    private static String modifyText(final String text) {
        if (NameProtect.INSTANCE.isEnabled()) {
            return text.replace(MixinTextVisitFactory.mc.getSession().getUsername(), NameProtect.INSTANCE.getFakeName());
        }
        return text;
    }
}
