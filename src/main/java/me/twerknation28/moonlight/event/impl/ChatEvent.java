package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;

public class ChatEvent
extends Event {
    private final String content;

    public ChatEvent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return this.content;
    }
}
