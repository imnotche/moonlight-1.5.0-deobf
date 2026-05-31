package me.twerknation28.moonlight.event.impl;

import net.minecraft.client.gui.DrawContext;
import me.twerknation28.moonlight.event.Event;

public class Render2DEvent extends Event
{
    private final DrawContext context;
    private final float delta;
    
    public Render2DEvent(final DrawContext context, final float delta) {
        this.context = context;
        this.delta = delta;
    }
    
    public DrawContext getContext() {
        return this.context;
    }
    
    public float getDelta() {
        return this.delta;
    }
}
