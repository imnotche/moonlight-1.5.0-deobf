package me.twerknation28.moonlight.features.modules.combat;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.AttackEvent;
import me.twerknation28.moonlight.event.impl.HandleBlockBreakingEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;

public class AntiMiss
extends Module {
    public Setting<Boolean> onlySword = this.register(new Setting<Boolean>("OnlySword", false));

    public AntiMiss() {
        super("AntiMiss", "Prevents you from swinging if you aren't in range of an enemy", Category.COMBAT, true, false, false);
    }

    @Subscribe
    public void onAttack(AttackEvent e) {
        if (!(AntiMiss.mc.crosshairTarget instanceof EntityHitResult) && e.isPre() && (!this.onlySword.getValue().booleanValue() || this.onlySword.getValue().booleanValue() && (AntiMiss.mc.player.getMainHandStack().getItem() instanceof SwordItem || AntiMiss.mc.player.getOffHandStack().getItem() instanceof SwordItem))) {
            e.cancel();
        }
    }

    @Subscribe
    public void onBlockBreaking(HandleBlockBreakingEvent e) {
        if (!(AntiMiss.mc.crosshairTarget instanceof EntityHitResult) && (!this.onlySword.getValue().booleanValue() || this.onlySword.getValue().booleanValue() && (AntiMiss.mc.player.getMainHandStack().getItem() instanceof SwordItem || AntiMiss.mc.player.getOffHandStack().getItem() instanceof SwordItem))) {
            e.cancel();
        }
    }
}
