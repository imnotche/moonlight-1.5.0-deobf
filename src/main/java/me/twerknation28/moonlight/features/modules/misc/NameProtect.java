package me.twerknation28.moonlight.features.modules.misc;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class NameProtect extends Module
{
    public static NameProtect INSTANCE;
    public final Setting<String> newName;
    
    public NameProtect() {
        super("NameProtect", "Changes your name client-side", Category.RENDER, true, false, false);
        this.newName = this.register(new Setting<String>("twerknation28", "twerknation28"));
        NameProtect.INSTANCE = this;
    }
    
    public String getFakeName() {
        return this.newName.getValue();
    }
}
