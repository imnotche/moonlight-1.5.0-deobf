package me.twerknation28.moonlight.features.modules.movement;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step
extends Module {
    private final Setting<Float> height = this.register(new Setting<Float>("Height", Float.valueOf(2.0f), Float.valueOf(1.0f), Float.valueOf(3.0f), Float.valueOf(0.1f)));
    private float prev;

    public Step() {
        super("Step", "Lets you scale blocks instantly", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (Step.nullCheck()) {
            this.prev = 0.6f;
            return;
        }
        this.prev = Step.mc.player.getStepHeight();
    }

    @Override
    public void onDisable() {
        if (Step.nullCheck()) {
            return;
        }
        Step.mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue((double)this.prev);
    }

    @Override
    public void onUpdate() {
        if (Step.nullCheck()) {
            return;
        }
        Step.mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue((double)this.height.getValue().floatValue());
    }
}
