package me.twerknation28.moonlight.features.clickgui;

import java.util.LinkedHashSet;
import java.util.Set;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.clickgui.Window;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.util.RenderUtil;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGUI
extends Screen {
    private static ClickGUI INSTANCE;
    private final Set<Window> windows = new LinkedHashSet<Window>();
    private final int margin = 22;

    public ClickGUI() {
        super((Text)Text.literal((String)"ClickGUI"));
        INSTANCE = this;
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 + 11 + 100 + 22, 22, "client", Category.CLIENT));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 - 11 - 200 - 22, 22, "combat", Category.COMBAT));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 + 11, 22, "player", Category.PLAYER));
        this.windows.add(new Window(Util.mc.getWindow().getScaledWidth() / 2 - 11 - 100, 22, "render", Category.RENDER));
        for (Window window : this.windows) {
            window.init(window.getCategory());
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        RenderUtil.rect(context.getMatrices(), 0.0f, 0.0f, this.width, this.height, ColorUtil.toARGB(0, 0, 0, 150));
        for (Window window : this.windows) {
            window.draw(context, mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Window window : this.windows) {
            window.processMouseClick((int)mouseX, (int)mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Window window : this.windows) {
            window.processMouseRelease((int)mouseX, (int)mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.setScreen(null);
            return true;
        }
        for (Window window : this.windows) {
            window.processKeyPress((char)keyCode, scanCode);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public static ClickGUI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGUI();
        }
        return INSTANCE;
    }

    public Set<Window> getWindows() {
        return this.windows;
    }
}
