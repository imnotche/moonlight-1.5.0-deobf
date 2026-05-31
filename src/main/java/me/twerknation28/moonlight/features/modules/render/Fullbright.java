package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public class Fullbright
extends Module {
    private static Fullbright INSTANCE = new Fullbright();

    public Fullbright() {
        super("Fullbright", "Fully brightens", Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static Fullbright getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Fullbright();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
