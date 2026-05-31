package me.twerknation28.moonlight.features.api;

import net.minecraft.client.gui.hud.MessageIndicator;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public interface AngelChat
{
    void invokeAddMessage(final Text p0, @Nullable final MessageSignatureData p1, @Nullable final MessageIndicator p2);
    
    void angel$remove(final MessageIndicator p0, final boolean p1);
}
