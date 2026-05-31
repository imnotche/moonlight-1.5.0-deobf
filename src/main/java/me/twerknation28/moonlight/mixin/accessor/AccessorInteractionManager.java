package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientPlayerInteractionManager.class })
public interface AccessorInteractionManager
{
    @Accessor("currentBreakingProgress")
    float getCurBlockDamageMP();
    
    @Accessor("currentBreakingProgress")
    void setCurBlockDamageMP(final float p0);
    
    @Invoker("syncSelectedSlot")
    void syncSlot();
}
