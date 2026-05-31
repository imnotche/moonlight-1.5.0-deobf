package me.twerknation28.moonlight.mixin.accessor;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ChatScreen.class})
public interface AccessorChatScreen {
    @Accessor(value="chatField")
    public TextFieldWidget getChatField();
}
