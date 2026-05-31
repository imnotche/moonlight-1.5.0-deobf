package me.twerknation28.moonlight.features.clickgui;

import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.ColorUtil;
import net.minecraft.client.gui.DrawContext;
import java.util.Iterator;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.util.Util;
import java.util.LinkedHashSet;
import net.minecraft.text.Text;
import java.util.Set;
import net.minecraft.client.gui.screen.Screen;

public class ClickGUI extends Screen
{
    private static ClickGUI INSTANCE;
    private final Set<Window> windows;
    private final int margin = 22;
    
    public ClickGUI() {
        super((Text)Text.literal("ClickGUI"));
        this.windows = new LinkedHashSet<Window>();
        ClickGUI.INSTANCE = this;
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 + 11 + 100 + 22, 22, "client", Category.CLIENT));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 - 11 - 200 - 22, 22, "combat", Category.COMBAT));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 + 11, 22, "player", Category.PLAYER));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 - 11 - 100, 22, "render", Category.RENDER));
        for (final Window window : this.windows) {
            window.init(window.getCategory());
        }
    }
    
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        RenderUtil.rect(context.getMatrices(), 0.0f, 0.0f, (float)this.width, (float)this.height, ColorUtil.toARGB(0, 0, 0, 150));
        for (final Window window : this.windows) {
            window.draw(context, mouseX, mouseY);
        }
    }
    
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        for (final Window window : this.windows) {
            window.processMouseClick((int)mouseX, (int)mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        for (final Window window : this.windows) {
            window.processMouseRelease((int)mouseX, (int)mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 256) {
            this.client.setScreen((Screen)null);
            return true;
        }
        for (final Window window : this.windows) {
            window.processKeyPress((char)keyCode, scanCode);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public static ClickGUI getInstance() {
        if (ClickGUI.INSTANCE == null) {
            ClickGUI.INSTANCE = new ClickGUI();
        }
        return ClickGUI.INSTANCE;
    }
    
    public Set<Window> getWindows() {
        return this.windows;
    }
}
