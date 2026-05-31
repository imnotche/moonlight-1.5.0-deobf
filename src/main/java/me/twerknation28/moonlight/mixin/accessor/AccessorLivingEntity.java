package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ LivingEntity.class })
public interface AccessorLivingEntity
{
    @Accessor("lastAttackedTicks")
    int getLastAttackedTicks();
    
    @Accessor("jumpingCooldown")
    int getLastJumpCooldown();
    
    @Accessor("jumpingCooldown")
    void setLastJumpCooldown(final int p0);
}
