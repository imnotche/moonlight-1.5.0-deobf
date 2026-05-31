package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.Moonlight;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.gui.Element;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import java.util.Objects;
import net.minecraft.client.gui.widget.TextWidget;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.gui.screen.Screen;

@Mixin({ TitleScreen.class })
public class MixinTitleScreen extends Screen
{
    @Unique
    private String message;
    
    public MixinTitleScreen() {
        super(Text.of("moonlight"));
        this.message = "waohack elite oh yeah";
    }
    
    @Inject(method = { "init" }, at = { @At("RETURN") })
    public void initHook(final CallbackInfo ci) {
        final String bussin = Moonlight.NAME + Moonlight.NAME;
        final TextWidget textWidget = new TextWidget(Text.of(bussin + ": " + this.message), this.textRenderer);
        final int var5 = this.client.getWindow().getScaledHeight();
        Objects.requireNonNull(this.textRenderer);
        textWidget.setPosition(2, var5 - 18 - 2);
        textWidget.setTextColor(ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        this.addDrawableChild(textWidget);
    }
}
