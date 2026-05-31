package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;
import me.twerknation28.moonlight.event.Stage;

public class UpdateWalkingPlayerEvent
extends Event {
    private final Stage stage;

    public UpdateWalkingPlayerEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return this.stage;
    }
}
