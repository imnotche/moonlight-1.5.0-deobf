package me.twerknation28.moonlight.event.impl;

import net.minecraft.entity.Entity;
import me.twerknation28.moonlight.event.Event;

public class AttackEvent extends Event
{
    boolean pre;
    private final Entity entity;
    
    public AttackEvent(final Entity entity, final boolean pre) {
        this.entity = entity;
        this.pre = pre;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public boolean isPre() {
        return this.pre;
    }
}
