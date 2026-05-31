package me.twerknation28.moonlight.util;

import net.minecraft.network.message.MessageSignatureData;
import me.twerknation28.moonlight.features.api.AngelChat;
import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.MessageIndicator;

public class ChatUtil
{
    public static final MessageIndicator ANGEL;
    
    public static void send(final Text message) {
        ((AngelChat)Util.mc.inGameHud.getChatHud()).angel$remove(ChatUtil.ANGEL, false);
        ((AngelChat)Util.mc.inGameHud.getChatHud()).invokeAddMessage(message, null, ChatUtil.ANGEL);
    }
    
    static {
        ANGEL = new MessageIndicator(11141290, (MessageIndicator.Icon)null, Text.of("mmoon light"), "Angel");
    }
}
