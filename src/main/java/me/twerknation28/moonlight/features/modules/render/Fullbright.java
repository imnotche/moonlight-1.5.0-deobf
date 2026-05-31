package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public class Fullbright extends Module
{
    private static Fullbright INSTANCE;
    
    public Fullbright() {
        super("Fullbright", "Fully brightens", Category.RENDER, true, false, false);
        this.setInstance();
    }
    
    public static Fullbright getInstance() {
        if (Fullbright.INSTANCE == null) {
            Fullbright.INSTANCE = new Fullbright();
        }
        return Fullbright.INSTANCE;
    }
    
    private void setInstance() {
        Fullbright.INSTANCE = this;
    }
    
    static {
        Fullbright.INSTANCE = new Fullbright();
    }
}
