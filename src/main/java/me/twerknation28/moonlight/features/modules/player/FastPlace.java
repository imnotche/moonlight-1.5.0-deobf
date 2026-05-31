package me.twerknation28.moonlight.features.modules.player;

import net.minecraft.item.Items;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class FastPlace extends Module
{
    public final Setting<Integer> delay;
    public Setting<Boolean> exp;
    public Setting<Boolean> obsidian;
    public Setting<Boolean> tnt;
    
    public FastPlace() {
        super("FastPlace", "Places fast", Category.PLAYER, true, false, false);
        this.delay = this.register(new Setting<Integer>("Delay", 0, 0, 5, 1));
        this.exp = this.register(new Setting<Boolean>("Exp Bottles", true));
        this.obsidian = this.register(new Setting<Boolean>("Obsidian", true));
        this.tnt = this.register(new Setting<Boolean>("TNT", true));
    }
    
    @Override
    public void onUpdate() {
        if (nullCheck()) {
            return;
        }
        if (this.shouldReduce()) {
            FastPlace.mc.itemUseCooldown = this.delay.getValue();
        }
    }
    
    public boolean shouldReduce() {
        return (FastPlace.mc.player.isHolding(Items.EXPERIENCE_BOTTLE) && this.exp.getValue()) || (FastPlace.mc.player.isHolding(Items.OBSIDIAN) && this.obsidian.getValue()) || (FastPlace.mc.player.isHolding(Items.TNT) && this.tnt.getValue());
    }
}
