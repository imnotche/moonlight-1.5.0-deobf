package me.twerknation28.moonlight.mixin.accessor;

import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ChatHud.class})
public interface AccessorChatHud {
    @Accessor(value="visibleMessages")
    public List<ChatHudLine.Visible> getVisibleMessages();

    @Accessor(value="messages")
    public List<ChatHudLine> getMessages();
}
