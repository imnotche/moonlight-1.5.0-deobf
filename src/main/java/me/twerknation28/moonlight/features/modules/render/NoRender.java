package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;

public class NoRender
extends Module {
    private static NoRender INSTANCE = new NoRender();
    public Setting<Boolean> potionHUD = this.register(new Setting<Boolean>("PotionHUD", false));
    public Setting<Boolean> fireOverlay = this.register(new Setting<Boolean>("FireOverlay", false));
    public Setting<Boolean> uglyParticles = this.register(new Setting<Boolean>("UglyParticles", false));
    public Setting<Boolean> toasts = this.register(new Setting<Boolean>("Toasts", false));
    public Setting<Boolean> glow = this.register(new Setting<Boolean>("Glow", false));

    public NoRender() {
        super("NoRender", "Makes ur game look swag", Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
