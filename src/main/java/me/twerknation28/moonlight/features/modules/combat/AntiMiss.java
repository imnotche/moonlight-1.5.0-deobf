package me.twerknation28.moonlight.features.modules.combat;

import me.twerknation28.moonlight.event.impl.HandleBlockBreakingEvent;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;
import me.twerknation28.moonlight.event.impl.AttackEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class AntiMiss extends Module
{
    public Setting<Boolean> onlySword;
    
    public AntiMiss() {
        super("AntiMiss", "Prevents you from swinging if you aren't in range of an enemy", Category.COMBAT, true, false, false);
        this.onlySword = this.register(new Setting<Boolean>("OnlySword", false));
    }
    
    @Subscribe
    public void onAttack(final AttackEvent e) {
        if (!(AntiMiss.mc.crosshairTarget instanceof EntityHitResult) && e.isPre() && (!this.onlySword.getValue() || (this.onlySword.getValue() && (AntiMiss.mc.player.getMainHandStack().getItem() instanceof SwordItem || AntiMiss.mc.player.getOffHandStack().getItem() instanceof SwordItem)))) {
            e.cancel();
        }
    }
    
    @Subscribe
    public void onBlockBreaking(final HandleBlockBreakingEvent e) {
        if (!(AntiMiss.mc.crosshairTarget instanceof EntityHitResult) && (!this.onlySword.getValue() || (this.onlySword.getValue() && (AntiMiss.mc.player.getMainHandStack().getItem() instanceof SwordItem || AntiMiss.mc.player.getOffHandStack().getItem() instanceof SwordItem)))) {
            e.cancel();
        }
    }
}
