package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent
extends Event {
    boolean pre;
    private final Entity entity;

    public AttackEvent(Entity entity, boolean pre) {
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
