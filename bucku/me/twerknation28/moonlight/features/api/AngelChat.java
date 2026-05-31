package me.twerknation28.moonlight.features.api;

import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface AngelChat {
    public void invokeAddMessage(Text var1, @Nullable MessageSignatureData var2, @Nullable MessageIndicator var3);

    public void angel$remove(MessageIndicator var1, boolean var2);
}
