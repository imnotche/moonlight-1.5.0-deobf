package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.DeathEvent;
import me.twerknation28.moonlight.mixin.MixinEntity;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public class MixinLivingEntity
extends MixinEntity {
    @Shadow
    @Final
    private static TrackedData<Float> HEALTH;

    @Inject(method={"onTrackedDataSet"}, at={@At(value="RETURN")})
    public void onTrackedDataSet(TrackedData<?> key, CallbackInfo info) {
        if (key.equals(HEALTH) && (double)((Float)this.dataTracker.get(HEALTH)).floatValue() <= 0.0 && Util.mc.world != null && Util.mc.world.isClient()) {
            DeathEvent deathEvent = new DeathEvent((LivingEntity)LivingEntity.class.cast(this));
            Util.EVENT_BUS.post(deathEvent);
        }
    }
}
