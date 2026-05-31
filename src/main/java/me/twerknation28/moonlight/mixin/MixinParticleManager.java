package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ParticleManager.class })
public class MixinParticleManager
{
    @Inject(at = { @At("HEAD") }, method = { "addParticle(Lnet/minecraft/client/particle/Particle;)V" }, cancellable = true)
    public void addParticleHook(final Particle particle, final CallbackInfo e) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().uglyParticles.getValue() && (particle instanceof FireSmokeParticle || particle instanceof BlockDustParticle || particle instanceof ExplosionLargeParticle || particle instanceof SpellParticle)) {
            e.cancel();
        }
    }
}
