package me.twerknation28.moonlight.mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={PlayerMoveC2SPacket.class})
public interface AccessorPlayerMoveC2SPacket {
    @Mutable
    @Accessor(value="y")
    public void setY(double var1);

    @Mutable
    @Accessor(value="onGround")
    public void setOnGround(boolean var1);
}
