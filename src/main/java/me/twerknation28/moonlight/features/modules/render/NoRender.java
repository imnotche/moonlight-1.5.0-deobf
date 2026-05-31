package me.twerknation28.moonlight.features.modules.render;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class NoRender extends Module
{
    private static NoRender INSTANCE;
    public Setting<Boolean> potionHUD;
    public Setting<Boolean> fireOverlay;
    public Setting<Boolean> uglyParticles;
    public Setting<Boolean> toasts;
    public Setting<Boolean> glow;
    
    public NoRender() {
        super("NoRender", "Makes ur game look swag", Category.RENDER, true, false, false);
        this.potionHUD = this.register(new Setting<Boolean>("PotionHUD", false));
        this.fireOverlay = this.register(new Setting<Boolean>("FireOverlay", false));
        this.uglyParticles = this.register(new Setting<Boolean>("UglyParticles", false));
        this.toasts = this.register(new Setting<Boolean>("Toasts", false));
        this.glow = this.register(new Setting<Boolean>("Glow", false));
        this.setInstance();
    }
    
    public static NoRender getInstance() {
        if (NoRender.INSTANCE == null) {
            NoRender.INSTANCE = new NoRender();
        }
        return NoRender.INSTANCE;
    }
    
    private void setInstance() {
        NoRender.INSTANCE = this;
    }
    
    static {
        NoRender.INSTANCE = new NoRender();
    }
}
