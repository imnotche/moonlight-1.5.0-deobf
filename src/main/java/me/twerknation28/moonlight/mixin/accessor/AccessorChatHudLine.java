package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;
import net.minecraft.text.Text;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ChatHudLine.class })
public interface AccessorChatHudLine
{
    @Mutable
    @Accessor("content")
    void setContent(final Text p0);
}
