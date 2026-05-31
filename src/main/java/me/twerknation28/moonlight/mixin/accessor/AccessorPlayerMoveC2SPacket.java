package me.twerknation28.moonlight.mixin.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mutable;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ PlayerMoveC2SPacket.class })
public interface AccessorPlayerMoveC2SPacket
{
    @Mutable
    @Accessor("y")
    void setY(final double p0);
    
    @Mutable
    @Accessor("onGround")
    void setOnGround(final boolean p0);
}
