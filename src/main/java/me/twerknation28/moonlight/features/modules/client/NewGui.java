package me.twerknation28.moonlight.features.modules.client;

import java.util.Iterator;
import net.minecraft.client.gui.screen.Screen;
import me.twerknation28.moonlight.features.clickgui.Window;
import me.twerknation28.moonlight.features.clickgui.ClickGUI;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class NewGui extends Module
{
    private static NewGui INSTANCE;
    public Setting<String> prefix;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Boolean> boxes;
    public Setting<Boolean> numbers;
    public Setting<Boolean> description;
    public Setting<DescriptionMode> descriptionMode;
    
    public NewGui() {
        super("ClickGUI", "oh yeah we premium", Category.CLIENT, true, false, false);
        this.prefix = this.register(new Setting<String>("Prefix", "."));
        this.red = this.register(new Setting<Integer>("Red", 64, 0, 255, 1));
        this.green = this.register(new Setting<Integer>("Green", 133, 0, 255, 1));
        this.blue = this.register(new Setting<Integer>("Blue", 219, 0, 255, 1));
        this.boxes = this.register(new Setting<Boolean>("Boxes", true));
        this.numbers = this.register(new Setting<Boolean>("ModCount", true));
        this.description = this.register(new Setting<Boolean>("Descriptions", true));
        this.descriptionMode = this.register(new Setting<DescriptionMode>("Mode", DescriptionMode.mouse, v -> this.description.getValue()));
        this.setBind(345);
        this.setInstance();
    }
    
    private void setInstance() {
        NewGui.INSTANCE = this;
    }
    
    public static NewGui getInstance() {
        if (NewGui.INSTANCE == null) {
            NewGui.INSTANCE = new NewGui();
        }
        return NewGui.INSTANCE;
    }
    
    @Override
    public void onEnable() {
        if (NewGui.mc.player == null) {
            return;
        }
        for (final Window window : ClickGUI.getInstance().getWindows()) {
            window.openGui();
        }
        NewGui.mc.setScreen((Screen)ClickGUI.getInstance());
        this.toggle();
    }
    
    static {
        NewGui.INSTANCE = new NewGui();
    }
    
    public enum DescriptionMode
    {
        mouse, 
        corner, 
        top;
    }
}
