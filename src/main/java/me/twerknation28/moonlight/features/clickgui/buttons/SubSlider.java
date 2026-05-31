package me.twerknation28.moonlight.features.clickgui.buttons;

import me.twerknation28.moonlight.features.clickgui.BaseButton;
import me.twerknation28.moonlight.features.clickgui.buttons.Button;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.MathUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;

public class SubSlider
extends BaseButton {
    private final Button parent;
    private final Setting<Number> option;
    private float value;
    private int currentWidth;
    private boolean dragging = false;

    public SubSlider(Button parent, Setting<Number> option) {
        super(parent.getWindow().getX() + 4, parent.getY() + 4, parent.getWindow().getWidth() - 7, 14);
        parent.getSubEntries().add(this);
        this.parent = parent;
        this.window = parent.getWindow();
        if (option != null) {
            this.value = option.getValue().floatValue();
        }
        this.option = option;
    }

    @Override
    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (!this.isMouseHovered()) {
            return;
        }
        if (button == 0) {
            this.dragging = true;
            Util.mc.getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
        }
    }

    @Override
    public void processMouseRelease(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (!this.dragging) {
            return;
        }
        if (button == 0) {
            this.dragging = false;
            this.setWidthFromValue();
        }
    }

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY) {
        if (this.dragging) {
            this.currentWidth = mouseX - this.getX();
            if (this.currentWidth < 0) {
                this.currentWidth = 0;
            } else if (this.currentWidth > 93) {
                this.currentWidth = 93;
            }
            this.updateValueFromWidth();
        }
        this.y = this.window.getRenderYButton();
        this.x = this.window.getX() + 4;
        this.updateIsMouseHovered(mouseX, mouseY);
        RenderUtil.rect(context.getMatrices(), this.getX(), this.getY(), -1 + this.getX(), this.getHeight() + this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        RenderUtil.rect(context.getMatrices(), this.getX(), this.getY(), this.currentWidth + this.getX(), this.getHeight() + this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255));
        FontUtil.drawStringWithShadow(this.option.getName().toLowerCase() + ": " + MathUtil.getRounded(this.value), this.getX() + 2, this.getY() + 3, -1, context);
    }

    @Override
    public int getColor() {
        return ColorUtil.getColorForGuiEntry(2, this.isMouseHovered(), false);
    }

    @Override
    public boolean shouldRender() {
        return this.parent.isOpen() && this.parent.shouldRender() && this.option.isVisible();
    }

    @Override
    public void openGui() {
        if (this.option != null) {
            this.value = this.option.getValue().floatValue();
        }
        this.setWidthFromValue();
    }

    protected void setWidthFromValue() {
        float val = this.value;
        val -= this.getMin();
        this.currentWidth = (int)RenderUtil.reCheckSliderRange((val /= this.getMax() - this.getMin()) * 93.0f, 0.0f, 93.0f);
    }

    protected void updateValueFromWidth() {
        float val = (float)this.currentWidth / 92.0f;
        val *= this.getMax() - this.getMin();
        val += this.getMin();
        val = RenderUtil.roundSliderStep(val, this.getStep());
        val = this.value = RenderUtil.reCheckSliderRange(val, this.getMin(), this.getMax());
        Double roundedValue = RenderUtil.roundSliderForConfig(val);
        if (this.option.getValue() instanceof Long) {
            this.option.setValue(roundedValue.longValue());
        } else if (this.option.getValue() instanceof Integer) {
            this.option.setValue(roundedValue.intValue());
        } else if (this.option.getValue() instanceof Float) {
            this.option.setValue(Float.valueOf(roundedValue.floatValue()));
        } else if (this.option.getValue() instanceof Double) {
            this.option.setValue(roundedValue);
        }
    }

    public Button getParent() {
        return this.parent;
    }

    public float getMax() {
        Number inc = this.option.getMax();
        return inc == null ? 100.0f : inc.floatValue();
    }

    public float getMin() {
        Number inc = this.option.getMin();
        return inc == null ? 0.0f : inc.floatValue();
    }

    public float getStep() {
        Number inc = this.option.getInc();
        return inc == null ? 1.0f : inc.floatValue();
    }
}
