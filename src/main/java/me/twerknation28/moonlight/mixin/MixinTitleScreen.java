package me.twerknation28.moonlight.mixin;

import java.util.Objects;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.util.ColorUtil;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={TitleScreen.class})
public class MixinTitleScreen
extends Screen {
    @Unique
    private String message = "waohack elite oh yeah";

    public MixinTitleScreen() {
        super(Text.of((String)"moonlight"));
    }

    @Inject(method={"init"}, at={@At(value="RETURN")})
    public void initHook(CallbackInfo ci) {
        String bussin = "moonlight 1.5.0-beta";
        TextWidget textWidget = new TextWidget(Text.of((String)(bussin + ": " + this.message)), this.textRenderer);
        int var5 = this.client.getWindow().getScaledHeight();
        Objects.requireNonNull(this.textRenderer);
        textWidget.setPosition(2, var5 - 18 - 2);
        textWidget.setTextColor(ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        this.addDrawableChild((Element)textWidget);
    }
}
