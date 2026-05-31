package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;

public class KeyEvent extends Event
{
    private final int key;
    
    public KeyEvent(final int key) {
        this.key = key;
    }
    
    public int getKey() {
        return this.key;
    }
}
