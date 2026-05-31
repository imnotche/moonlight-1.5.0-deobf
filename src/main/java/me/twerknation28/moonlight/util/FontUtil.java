package me.twerknation28.moonlight.util;

import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.StringUtils;

public class FontUtil
implements Util {
    public static float drawStringWithShadow(String text, int x, int y, int color, DrawContext context) {
        if (NameProtect.INSTANCE.isEnabled()) {
            text = StringUtils.replace((String)text, (String)String.valueOf(FontUtil.mc.player.getName()), (String)NameProtect.INSTANCE.newName.getValue());
        }
        return context.drawTextWithShadow(FontUtil.mc.textRenderer, text, x, y, color);
    }
}
