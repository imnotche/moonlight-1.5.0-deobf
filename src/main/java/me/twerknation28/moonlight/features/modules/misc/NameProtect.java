package me.twerknation28.moonlight.features.modules.misc;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;

public class NameProtect
extends Module {
    public static NameProtect INSTANCE;
    public final Setting<String> newName = this.register(new Setting<String>("twerknation28", "twerknation28"));

    public NameProtect() {
        super("NameProtect", "Changes your name client-side", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public String getFakeName() {
        return this.newName.getValue();
    }
}
