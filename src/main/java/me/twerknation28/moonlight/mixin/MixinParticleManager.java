package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.features.modules.render.NoRender;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpellParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ParticleManager.class})
public class MixinParticleManager {
    @Inject(at={@At(value="HEAD")}, method={"addParticle(Lnet/minecraft/client/particle/Particle;)V"}, cancellable=true)
    public void addParticleHook(Particle particle, CallbackInfo e) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().uglyParticles.getValue().booleanValue() && (particle instanceof FireSmokeParticle || particle instanceof BlockDustParticle || particle instanceof ExplosionLargeParticle || particle instanceof SpellParticle)) {
            e.cancel();
        }
    }
}
