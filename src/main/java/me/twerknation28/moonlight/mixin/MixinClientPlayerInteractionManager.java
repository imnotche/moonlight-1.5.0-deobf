package me.twerknation28.moonlight.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.block.BlockState;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientPlayerInteractionManager.class })
public class MixinClientPlayerInteractionManager
{
    @Inject(method = { "attackBlock" }, at = { @At("HEAD") }, cancellable = true)
    private void hookAttackBlock(final BlockPos pos, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
        final BlockState state = Util.mc.world.getBlockState(pos);
        final AttackBlockEvent attackBlockEvent = new AttackBlockEvent(pos, state, direction);
        Util.EVENT_BUS.post(attackBlockEvent);
        if (attackBlockEvent.isCanceled()) {
            cir.cancel();
        }
    }
}
