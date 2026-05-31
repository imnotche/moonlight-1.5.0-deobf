package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value={TextVisitFactory.class})
public class MixinTextVisitFactory
implements Util {
    @ModifyArg(method={"visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, at=@At(value="INVOKE", target="Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"))
    private static String modifyText(String text) {
        if (NameProtect.INSTANCE.isEnabled()) {
            return text.replace(mc.getSession().getUsername(), NameProtect.INSTANCE.getFakeName());
        }
        return text;
    }
}
