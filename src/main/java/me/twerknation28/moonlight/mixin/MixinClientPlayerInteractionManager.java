package me.twerknation28.moonlight.mixin;

import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientPlayerInteractionManager.class})
public class MixinClientPlayerInteractionManager {
    @Inject(method={"attackBlock"}, at={@At(value="HEAD")}, cancellable=true)
    private void hookAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = Util.mc.world.getBlockState(pos);
        AttackBlockEvent attackBlockEvent = new AttackBlockEvent(pos, state, direction);
        Util.EVENT_BUS.post(attackBlockEvent);
        if (attackBlockEvent.isCanceled()) {
            cir.cancel();
        }
    }
}
