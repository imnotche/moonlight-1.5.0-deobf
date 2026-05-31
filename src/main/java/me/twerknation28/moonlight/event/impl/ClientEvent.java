package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.settings.Setting;

public class ClientEvent
extends Event {
    private Feature feature;
    private Setting<?> setting;
    private int stage;

    public ClientEvent(int stage, Feature feature) {
        this.stage = stage;
        this.feature = feature;
    }

    public ClientEvent(Setting<?> setting) {
        this.stage = 2;
        this.setting = setting;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public Setting<?> getSetting() {
        return this.setting;
    }

    public int getStage() {
        return this.stage;
    }
}
