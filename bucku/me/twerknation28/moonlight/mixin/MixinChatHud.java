package me.twerknation28.moonlight.mixin;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import me.twerknation28.moonlight.features.api.AngelChat;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ChatHud.class})
public abstract class MixinChatHud
implements AngelChat {
    @Final
    @Shadow
    private List<ChatHudLine> messages = Lists.newArrayList();

    @Shadow
    public abstract int getWidth();

    @Shadow
    protected abstract void refresh();

    @Override
    @Invoker
    public abstract void invokeAddMessage(Text var1, @Nullable MessageSignatureData var2, @Nullable MessageIndicator var3);

    @Override
    public void angel$remove(@Nullable MessageIndicator indicator, boolean all) {
        if (indicator != null) {
            ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
            boolean changed = false;
            while (listIterator.hasNext()) {
                ChatHudLine message = listIterator.next();
                if (!indicator.equals((Object)message.indicator())) continue;
                listIterator.remove();
                changed = true;
                if (all) continue;
                break;
            }
            if (changed) {
                this.refresh();
            }
        }
    }
}
