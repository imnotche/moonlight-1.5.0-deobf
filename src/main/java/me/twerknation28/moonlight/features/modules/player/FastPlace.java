package me.twerknation28.moonlight.features.modules.player;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.item.Items;

public class FastPlace
extends Module {
    public final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 5, 1));
    public Setting<Boolean> exp = this.register(new Setting<Boolean>("Exp Bottles", true));
    public Setting<Boolean> obsidian = this.register(new Setting<Boolean>("Obsidian", true));
    public Setting<Boolean> tnt = this.register(new Setting<Boolean>("TNT", true));

    public FastPlace() {
        super("FastPlace", "Places fast", Category.PLAYER, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (FastPlace.nullCheck()) {
            return;
        }
        if (this.shouldReduce()) {
            FastPlace.mc.itemUseCooldown = this.delay.getValue();
        }
    }

    public boolean shouldReduce() {
        if (FastPlace.mc.player.isHolding(Items.EXPERIENCE_BOTTLE) && this.exp.getValue().booleanValue()) {
            return true;
        }
        if (FastPlace.mc.player.isHolding(Items.OBSIDIAN) && this.obsidian.getValue().booleanValue()) {
            return true;
        }
        return FastPlace.mc.player.isHolding(Items.TNT) && this.tnt.getValue() != false;
    }
}
