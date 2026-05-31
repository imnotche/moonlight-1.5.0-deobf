package me.twerknation28.moonlight.mixin.accessor;

import net.minecraft.client.network.PendingUpdateManager;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientWorld.class })
public interface AccessorClientWorld
{
    @Invoker("playSound")
    void hookPlaySound(final double p0, final double p1, final double p2, final SoundEvent p3, final SoundCategory p4, final float p5, final float p6, final boolean p7, final long p8);
    
    @Invoker("getPendingUpdateManager")
    PendingUpdateManager hookGetPendingUpdateManager();
}
