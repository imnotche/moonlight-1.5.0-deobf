package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ChatScreen.class })
public interface AccessorChatScreen
{
    @Accessor("chatField")
    TextFieldWidget getChatField();
}
