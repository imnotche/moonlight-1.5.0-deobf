package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;
import net.minecraft.entity.Entity;

public class RenderLabelEvent
extends Event {
    private Entity entity;

    public RenderLabelEvent(Entity entity) {
    }

    public Entity getEntity() {
        return this.entity;
    }
}
