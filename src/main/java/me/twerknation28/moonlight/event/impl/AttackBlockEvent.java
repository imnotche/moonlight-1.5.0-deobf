package me.twerknation28.moonlight.event.impl;

import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import me.twerknation28.moonlight.event.Event;

@Cancelable
public class AttackBlockEvent extends Event
{
    private final BlockPos pos;
    private final BlockState state;
    private final Direction direction;
    private boolean canceled;
    
    public AttackBlockEvent(final BlockPos pos, final BlockState state, final Direction direction) {
        this.pos = pos;
        this.state = state;
        this.direction = direction;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public BlockState getState() {
        return this.state;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public boolean isCanceled() {
        return this.canceled;
    }
    
    public void setCanceled(final boolean cancel) {
        this.canceled = cancel;
    }
}
