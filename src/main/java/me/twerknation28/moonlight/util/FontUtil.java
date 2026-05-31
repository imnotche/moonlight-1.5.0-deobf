package me.twerknation28.moonlight.util;

import org.apache.commons.lang3.StringUtils;
import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import net.minecraft.client.gui.DrawContext;

public class FontUtil implements Util
{
    public static float drawStringWithShadow(String text, final int x, final int y, final int color, final DrawContext context) {
        if (NameProtect.INSTANCE.isEnabled()) {
            text = StringUtils.replace(text, String.valueOf(FontUtil.mc.player.getName()), (String)NameProtect.INSTANCE.newName.getValue());
        }
        return (float)context.drawTextWithShadow(FontUtil.mc.textRenderer, text, x, y, color);
    }
}
