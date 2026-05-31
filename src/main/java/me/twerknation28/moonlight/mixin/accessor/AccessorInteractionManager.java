package me.twerknation28.moonlight.mixin.accessor;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={ClientPlayerInteractionManager.class})
public interface AccessorInteractionManager {
    @Accessor(value="currentBreakingProgress")
    public float getCurBlockDamageMP();

    @Accessor(value="currentBreakingProgress")
    public void setCurBlockDamageMP(float var1);

    @Invoker(value="syncSelectedSlot")
    public void syncSlot();
}
