package me.twerknation28.moonlight.event.impl;

import net.minecraft.entity.LivingEntity;
import me.twerknation28.moonlight.event.Event;

public class DeathEvent extends Event
{
    private final LivingEntity entity;
    
    public DeathEvent(final LivingEntity entity) {
        this.entity = entity;
    }
    
    public LivingEntity getEntity() {
        return this.entity;
    }
}
