package me.twerknation28.moonlight.features.modules.client;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.clickgui.ClickGUI;
import me.twerknation28.moonlight.features.clickgui.Window;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.client.gui.screen.Screen;

public class NewGui
extends Module {
    private static NewGui INSTANCE = new NewGui();
    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 64, 0, 255, 1));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 133, 0, 255, 1));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 219, 0, 255, 1));
    public Setting<Boolean> boxes = this.register(new Setting<Boolean>("Boxes", true));
    public Setting<Boolean> numbers = this.register(new Setting<Boolean>("ModCount", true));
    public Setting<Boolean> description = this.register(new Setting<Boolean>("Descriptions", true));
    public Setting<DescriptionMode> descriptionMode = this.register(new Setting<DescriptionMode>("Mode", DescriptionMode.mouse, v -> this.description.getValue()));

    public NewGui() {
        super("ClickGUI", "oh yeah we premium", Category.CLIENT, true, false, false);
        this.setBind(345);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NewGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewGui();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (NewGui.mc.player == null) {
            return;
        }
        for (Window window : ClickGUI.getInstance().getWindows()) {
            window.openGui();
        }
        mc.setScreen((Screen)ClickGUI.getInstance());
        this.toggle();
    }

    public static enum DescriptionMode {
        mouse,
        corner,
        top;

    }
}
