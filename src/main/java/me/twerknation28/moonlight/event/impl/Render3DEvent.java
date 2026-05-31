package me.twerknation28.moonlight.event.impl;

import net.minecraft.client.util.math.MatrixStack;
import me.twerknation28.moonlight.event.Event;

public class Render3DEvent extends Event
{
    private final MatrixStack matrix;
    private final float delta;
    
    public Render3DEvent(final MatrixStack matrix, final float delta) {
        this.matrix = matrix;
        this.delta = delta;
    }
    
    public MatrixStack getMatrix() {
        return this.matrix;
    }
    
    public float getDelta() {
        return this.delta;
    }
    
    public MatrixStack getMatrixStack() {
        return this.matrix;
    }
}
