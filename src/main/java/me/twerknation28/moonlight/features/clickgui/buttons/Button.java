package me.twerknation28.moonlight.features.clickgui.buttons;

import java.util.Objects;
import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import me.twerknation28.moonlight.util.Util;
import java.util.ArrayList;
import me.twerknation28.moonlight.features.clickgui.Window;
import me.twerknation28.moonlight.features.modules.Module;
import java.util.List;
import me.twerknation28.moonlight.features.clickgui.BaseButton;

public class Button extends BaseButton
{
    private final List<BaseButton> subEntries;
    private boolean isOpen;
    private final Module module;
    
    public Button(final Window window, final Module module) {
        super(window.getX() + 2, window.getY() + 2, window.getWidth() - 4, 14);
        this.subEntries = new ArrayList<BaseButton>();
        this.isOpen = false;
        this.window = window;
        this.module = module;
    }
    
    @Override
    public void processMouseClick(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (!this.isMouseHovered()) {
            return;
        }
        if (button == 0) {
            this.module.toggle();
            Util.mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        else if (button == 1) {
            this.isOpen = !this.isOpen;
            Util.mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
    
    @Override
    public void draw(final DrawContext context, final int mouseX, final int mouseY) {
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 2;
        this.updateIsMouseHovered(mouseX, mouseY);
        if (NewGui.getInstance().boxes.getValue()) {
            RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)this.getY(), (float)(this.getWidth() + this.getX()), (float)(this.getHeight() + this.getY()), ColorUtil.getColorForGuiEntry(4, this.isMouseHovered(), this.module.isEnabled()));
        }
        FontUtil.drawStringWithShadow(this.module.getName().toLowerCase(), this.getX() + 2, this.getY() + 3, this.getColor(), context);
        if (this.isMouseHovered()) {
            RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)this.getY(), (float)(this.getWidth() + this.getX()), (float)(this.getHeight() + this.getY()), ColorUtil.toARGB(100, 100, 100, 50));
            if (!Objects.equals(this.module.getDescription(), "") && NewGui.getInstance().description.getValue()) {
                final String text = this.module.getDescription();
                int drawX = 15;
                int drawY = Util.mc.getWindow().getScaledHeight() - 30;
                if (NewGui.getInstance().descriptionMode.getValue() == NewGui.DescriptionMode.mouse) {
                    if ((drawX = mouseX) < Util.mc.getWindow().getScaledWidth() / 2) {
                        drawX -= Util.mc.textRenderer.getWidth(text) + 6;
                    }
                    drawY = mouseY - 4;
                }
                else if (NewGui.getInstance().descriptionMode.getValue() == NewGui.DescriptionMode.top) {
                    drawX = Util.mc.getWindow().getScaledWidth() / 2 - Util.mc.textRenderer.getWidth(text) / 2;
                    drawY = 3;
                }
                final int globalColor = ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
                if (NewGui.getInstance().descriptionMode.getValue() != NewGui.DescriptionMode.mouse) {
                    RenderUtil.rect(context.getMatrices(), drawX - 0.5f, drawY - 0.5f, drawX + Util.mc.textRenderer.getWidth(text) + 5.5f, drawY + 13.5f, globalColor, 1.0f);
                    RenderUtil.rect(context.getMatrices(), (float)drawX, (float)drawY, (float)(drawX + Util.mc.textRenderer.getWidth(text) + 6), (float)(drawY + 13), ColorUtil.toARGB(255, 255, 255, 30));
                }
                else {
                    RenderUtil.rect(context.getMatrices(), (float)drawX, (float)drawY, (float)(drawX + Util.mc.textRenderer.getWidth(text) + 6), (float)(drawY + 13), 1996488704);
                }
                context.drawTextWithShadow(Util.mc.textRenderer, text, drawX + 3, drawY + 3, 16777215);
            }
        }
    }
    
    @Override
    public int getColor() {
        return ColorUtil.getColorForGuiEntry(0, this.isMouseHovered(), this.module.isEnabled());
    }
    
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    public List<BaseButton> getSubEntries() {
        return this.subEntries;
    }
}
