package me.twerknation28.moonlight.features.clickgui.buttons;

import me.twerknation28.moonlight.features.clickgui.BaseButton;
import me.twerknation28.moonlight.features.clickgui.buttons.Button;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;

public class SubButton
extends BaseButton {
    private final Button parent;
    private final Setting<Boolean> option;

    public SubButton(Button parent, Setting<Boolean> option) {
        super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 7, 14);
        parent.getSubEntries().add(this);
        this.parent = parent;
        this.window = parent.getWindow();
        this.option = option;
    }

    @Override
    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (!this.isMouseHovered()) {
            return;
        }
        if (button == 0) {
            this.option.setValue(this.option.getValue() == false);
            Util.mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY) {
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 4;
        this.updateIsMouseHovered(mouseX, mouseY);
        RenderUtil.rect(context.getMatrices(), this.getX(), this.getY(), this.getWidth() + this.getX(), this.getHeight() + this.getY(), this.getColor());
        RenderUtil.rect(context.getMatrices(), this.getX(), this.getY(), -1 + this.getX(), this.getHeight() + this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        FontUtil.drawStringWithShadow(this.option.getName().toLowerCase(), this.getX() + 2, this.getY() + 3, -1, context);
    }

    @Override
    public int getColor() {
        return ColorUtil.getColorForGuiEntry(3, this.isMouseHovered(), this.option.getValue());
    }

    @Override
    public boolean shouldRender() {
        return this.parent.isOpen() && this.parent.shouldRender() && this.option.isVisible();
    }

    public Button getParent() {
        return this.parent;
    }
}
