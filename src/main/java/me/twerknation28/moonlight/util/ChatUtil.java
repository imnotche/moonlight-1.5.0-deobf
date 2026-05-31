package me.twerknation28.moonlight.util;

import me.twerknation28.moonlight.features.api.AngelChat;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;

public class ChatUtil {
    public static final MessageIndicator ANGEL = new MessageIndicator(0xAA00AA, (MessageIndicator.Icon)null, Text.of((String)"mmoon light"), "Angel");

    public static void send(Text message) {
        ((AngelChat)Util.mc.inGameHud.getChatHud()).angel$remove(ANGEL, false);
        ((AngelChat)Util.mc.inGameHud.getChatHud()).invokeAddMessage(message, null, ANGEL);
    }
}
