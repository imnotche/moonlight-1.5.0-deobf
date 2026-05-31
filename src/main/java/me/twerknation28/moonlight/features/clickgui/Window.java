package me.twerknation28.moonlight.features.clickgui;

import me.twerknation28.moonlight.features.clickgui.buttons.SubButton;
import me.twerknation28.moonlight.features.clickgui.buttons.SubSlider;
import me.twerknation28.moonlight.features.clickgui.buttons.SubMode;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.clickgui.buttons.SubBind;
import me.twerknation28.moonlight.features.clickgui.buttons.Button;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import net.minecraft.client.gui.DrawContext;
import java.util.Iterator;
import me.twerknation28.moonlight.features.api.Category;
import java.util.ArrayList;

public class Window extends BaseButton
{
    public boolean isOpen;
    private final String text;
    private final ArrayList<BaseButton> buttons;
    private int renderYButton;
    private boolean isDragging;
    private int dragX;
    private int dragY;
    private final Category category;
    
    public Window(final int x, final int y, final String name, final Category category) {
        super(x, y, 100, 12);
        this.isOpen = true;
        this.buttons = new ArrayList<BaseButton>();
        this.renderYButton = 0;
        this.isDragging = false;
        this.dragX = 0;
        this.dragY = 0;
        this.text = name;
        this.category = category;
    }
    
    @Override
    public void processMouseClick(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered() && button == 1) {
            this.isOpen = !this.isOpen;
        }
        for (final BaseButton baseButton : this.buttons) {
            if (baseButton.shouldRender()) {
                baseButton.processMouseClick(mouseX, mouseY, button);
            }
        }
    }
    
    @Override
    public void processMouseRelease(final int mouseX, final int mouseY, final int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isDragging) {
            this.isDragging = false;
        }
        for (final BaseButton baseButton : this.buttons) {
            if (baseButton.shouldRender()) {
                baseButton.processMouseRelease(mouseX, mouseY, button);
            }
        }
    }
    
    @Override
    public void processKeyPress(final char character, final int key) {
        for (final BaseButton button : this.buttons) {
            if (button.shouldRender()) {
                button.processKeyPress(character, key);
            }
        }
    }
    
    @Override
    public void draw(final DrawContext context, final int mouseX, final int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }
        this.updateIsMouseHovered(mouseX, mouseY);
        this.renderYButton = this.getY() + 15;
        RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)this.getY(), (float)(this.getWidth() + this.getX()), (float)(13 + this.getY()), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 175));
        FontUtil.drawStringWithShadow(this.text, this.getX() + 2, this.getY() + 2, -1, context);
        final String str = "[" + Moonlight.moduleManager.getModulesByCategory(this.getCategory()).size();
        if (NewGui.getInstance().numbers.getValue()) {
            FontUtil.drawStringWithShadow(str, this.getWidth() + this.getX() - Util.mc.textRenderer.getWidth(str) - 2, this.getY() + 2, -1, context);
        }
        if (this.isOpen) {
            RenderUtil.rect(context.getMatrices(), (float)this.getX(), (float)(this.getY() + 13), (float)(this.getWidth() + this.getX()), (float)(this.getHeight() + 4 + this.getY()), this.getColor());
            RenderUtil.rect(context.getMatrices(), this.getX() - 0.5f, this.getY() - 0.5f, (float)(this.getWidth() + this.getX()), this.getHeight() + 4.0f + this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255), 1.0f);
            for (final BaseButton button : this.buttons) {
                if (button.shouldRender()) {
                    button.draw(context, mouseX, mouseY);
                    this.renderYButton += button.getHeight() + 1;
                }
            }
        }
    }
    
    @Override
    public int getHeight() {
        int i = this.height;
        for (final BaseButton button : this.buttons) {
            if (button.shouldRender() && !(button instanceof Window)) {
                i += button.getHeight() + 1;
            }
        }
        return i;
    }
    
    @Override
    public void openGui() {
        for (final BaseButton button : this.buttons) {
            button.openGui();
        }
    }
    
    @Override
    public boolean shouldRender() {
        return true;
    }
    
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }
    
    @Override
    public int getColor() {
        return ColorUtil.getColorForGuiEntry(1, this.isMouseHovered(), false);
    }
    
    public void init(final Category category) {
        for (final Module module : Moonlight.moduleManager.getModulesByCategory(category)) {
            final Button b = this.addButton(new Button(this, module));
            if (module.getCategory() != Category.CLIENT) {
                this.addSubBind(new SubBind(b));
            }
            for (final Setting<?> setting : module.getSettings().reversed()) {
                final Object value = setting.getValue();
                if (value.getClass().isEnum()) {
                    this.addSubMode(new SubMode(b, (Setting<Enum>)setting));
                }
                else if (value instanceof Number) {
                    this.addSubSlider(new SubSlider(b, (Setting<Number>)setting));
                }
                else {
                    if (!(value instanceof Boolean) || setting.getName().equals("Enabled") || setting.getName().equals("Drawn")) {
                        continue;
                    }
                    this.addSubButton(new SubButton(b, (Setting<Boolean>)setting));
                }
            }
        }
    }
    
    private Button addButton(final Button b) {
        this.buttons.add(b);
        return b;
    }
    
    private void addSubButton(final SubButton b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }
    
    private void addSubBind(final SubBind b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }
    
    private void addSubMode(final SubMode b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }
    
    private void addSubSlider(final SubSlider slider) {
        this.buttons.add(this.buttons.indexOf(slider.getParent()) + 1, slider);
    }
    
    public int getRenderYButton() {
        return this.renderYButton;
    }
    
    public Category getCategory() {
        return this.category;
    }
}
