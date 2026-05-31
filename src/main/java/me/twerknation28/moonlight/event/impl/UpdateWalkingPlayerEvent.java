package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Stage;
import me.twerknation28.moonlight.event.Event;

public class UpdateWalkingPlayerEvent extends Event
{
    private final Stage stage;
    
    public UpdateWalkingPlayerEvent(final Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return this.stage;
    }
}
