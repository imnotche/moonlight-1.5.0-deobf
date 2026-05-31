package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.hud.ChatHudLine;
import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ChatHud.class })
public interface AccessorChatHud
{
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> getVisibleMessages();
    
    @Accessor("messages")
    List<ChatHudLine> getMessages();
}
