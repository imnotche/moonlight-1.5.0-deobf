package me.twerknation28.moonlight.event.impl;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MovementType;

public class PlayerMoveEvent
{
    public MovementType type;
    public Vec3d movement;
    
    public MovementType getType() {
        return this.type;
    }
    
    public Vec3d getMovement() {
        return this.movement;
    }
}
