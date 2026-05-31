package me.twerknation28.moonlight.mixin.accessor;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ChatHudLine.class})
public interface AccessorChatHudLine {
    @Mutable
    @Accessor(value="content")
    public void setContent(Text var1);
}
