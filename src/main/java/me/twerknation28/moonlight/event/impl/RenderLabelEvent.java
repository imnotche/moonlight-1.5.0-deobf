package me.twerknation28.moonlight.event.impl;

import net.minecraft.entity.Entity;
import me.twerknation28.moonlight.event.Event;

public class RenderLabelEvent extends Event
{
    private Entity entity;
    
    public RenderLabelEvent(final Entity entity) {
    }
    
    public Entity getEntity() {
        return this.entity;
    }
}
