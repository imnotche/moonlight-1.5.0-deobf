package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.event.impl.DeathEvent;
import me.twerknation28.moonlight.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ LivingEntity.class })
public class MixinLivingEntity extends MixinEntity
{
    @Shadow
    @Final
    private static TrackedData<Float> HEALTH;
    
    @Inject(method = { "onTrackedDataSet" }, at = { @At("RETURN") })
    public void onTrackedDataSet(final TrackedData<?> key, final CallbackInfo info) {
        if (key.equals((Object)MixinLivingEntity.HEALTH) && (float)this.dataTracker.get((TrackedData)MixinLivingEntity.HEALTH) <= 0.0 && Util.mc.world != null && Util.mc.world.isClient()) {
            final DeathEvent deathEvent = new DeathEvent(LivingEntity.class.cast(this));
            Util.EVENT_BUS.post(deathEvent);
        }
    }
}
