package me.twerknation28.moonlight.features.clickgui;

import java.util.ArrayList;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.clickgui.BaseButton;
import me.twerknation28.moonlight.features.clickgui.buttons.Button;
import me.twerknation28.moonlight.features.clickgui.buttons.SubBind;
import me.twerknation28.moonlight.features.clickgui.buttons.SubButton;
import me.twerknation28.moonlight.features.clickgui.buttons.SubMode;
import me.twerknation28.moonlight.features.clickgui.buttons.SubSlider;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.util.FontUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.DrawContext;

public class Window
extends BaseButton {
    public boolean isOpen = true;
    private final String text;
    private final ArrayList<BaseButton> buttons = new ArrayList();
    private int renderYButton = 0;
    private boolean isDragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private final Category category;

    public Window(int x, int y, String name, Category category) {
        super(x, y, 100, 12);
        this.text = name;
        this.category = category;
    }

    @Override
    public void processMouseClick(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isMouseHovered() && button == 1) {
            this.isOpen = !this.isOpen;
        }
        for (BaseButton baseButton : this.buttons) {
            if (!baseButton.shouldRender()) continue;
            baseButton.processMouseClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void processMouseRelease(int mouseX, int mouseY, int button) {
        this.updateIsMouseHovered(mouseX, mouseY);
        if (this.isDragging) {
            this.isDragging = false;
        }
        for (BaseButton baseButton : this.buttons) {
            if (!baseButton.shouldRender()) continue;
            baseButton.processMouseRelease(mouseX, mouseY, button);
        }
    }

    @Override
    public void processKeyPress(char character, int key) {
        for (BaseButton button : this.buttons) {
            if (!button.shouldRender()) continue;
            button.processKeyPress(character, key);
        }
    }

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY) {
        if (this.isDragging) {
            this.setX(mouseX - this.dragX);
            this.setY(mouseY - this.dragY);
        }
        this.updateIsMouseHovered(mouseX, mouseY);
        this.renderYButton = this.getY() + 15;
        RenderUtil.rect(context.getMatrices(), this.getX(), this.getY(), this.getWidth() + this.getX(), 13 + this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 175));
        FontUtil.drawStringWithShadow(this.text, this.getX() + 2, this.getY() + 2, -1, context);
        String str = "[" + Moonlight.moduleManager.getModulesByCategory(this.getCategory()).size() + "]";
        if (NewGui.getInstance().numbers.getValue().booleanValue()) {
            FontUtil.drawStringWithShadow(str, this.getWidth() + this.getX() - Util.mc.textRenderer.getWidth(str) - 2, this.getY() + 2, -1, context);
        }
        if (this.isOpen) {
            RenderUtil.rect(context.getMatrices(), this.getX(), this.getY() + 13, this.getWidth() + this.getX(), this.getHeight() + 4 + this.getY(), this.getColor());
            RenderUtil.rect(context.getMatrices(), (float)this.getX() - 0.5f, (float)this.getY() - 0.5f, this.getWidth() + this.getX(), (float)this.getHeight() + 4.0f + (float)this.getY(), ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255), 1.0f);
            for (BaseButton button : this.buttons) {
                if (!button.shouldRender()) continue;
                button.draw(context, mouseX, mouseY);
                this.renderYButton += button.getHeight() + 1;
            }
        }
    }

    @Override
    public int getHeight() {
        int i = this.height;
        for (BaseButton button : this.buttons) {
            if (!button.shouldRender() || button instanceof Window) continue;
            i += button.getHeight() + 1;
        }
        return i;
    }

    @Override
    public void openGui() {
        for (BaseButton button : this.buttons) {
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

    public void init(Category category) {
        for (Module module : Moonlight.moduleManager.getModulesByCategory(category)) {
            Button b = this.addButton(new Button(this, module));
            if (module.getCategory() != Category.CLIENT) {
                this.addSubBind(new SubBind(b));
            }
            for (Setting setting : module.getSettings().reversed()) {
                Object value = setting.getValue();
                if (value.getClass().isEnum()) {
                    this.addSubMode(new SubMode(b, setting));
                    continue;
                }
                if (value instanceof Number) {
                    this.addSubSlider(new SubSlider(b, setting));
                    continue;
                }
                if (!(value instanceof Boolean) || setting.getName().equals("Enabled") || setting.getName().equals("Drawn")) continue;
                this.addSubButton(new SubButton(b, setting));
            }
        }
    }

    private Button addButton(Button b) {
        this.buttons.add(b);
        return b;
    }

    private void addSubButton(SubButton b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }

    private void addSubBind(SubBind b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }

    private void addSubMode(SubMode b) {
        this.buttons.add(this.buttons.indexOf(b.getParent()) + 1, b);
    }

    private void addSubSlider(SubSlider slider) {
        this.buttons.add(this.buttons.indexOf(slider.getParent()) + 1, slider);
    }

    public int getRenderYButton() {
        return this.renderYButton;
    }

    public Category getCategory() {
        return this.category;
    }
}
