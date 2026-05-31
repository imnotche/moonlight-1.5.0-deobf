package me.twerknation28.moonlight.features.modules.movement;

import net.minecraft.entity.attribute.EntityAttributes;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Step extends Module
{
    private final Setting<Float> height;
    private float prev;
    
    public Step() {
        super("Step", "Lets you scale blocks instantly", Category.PLAYER, true, false, false);
        this.height = this.register(new Setting<Float>("Height", 2.0f, 1.0f, 3.0f, 0.1f));
    }
    
    @Override
    public void onEnable() {
        if (nullCheck()) {
            this.prev = 0.6f;
            return;
        }
        this.prev = Step.mc.player.getStepHeight();
    }
    
    @Override
    public void onDisable() {
        if (nullCheck()) {
            return;
        }
        Step.mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue((double)this.prev);
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        Step.mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue((double)this.height.getValue());
    }
}
