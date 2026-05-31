package me.twerknation28.moonlight.event.impl;

import javax.swing.text.html.parser.Entity;
import me.twerknation28.moonlight.event.Event;

public class PushEntityEvent extends Event
{
    private final Entity pushed;
    private final Entity pusher;
    
    public PushEntityEvent(final Entity pushed, final Entity pusher) {
        this.pushed = pushed;
        this.pusher = pusher;
    }
    
    public Entity getPushed() {
        return this.pushed;
    }
    
    public Entity getPusher() {
        return this.pusher;
    }
}
