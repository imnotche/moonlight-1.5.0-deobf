package me.twerknation28.moonlight.features.api;

import java.awt.Color;

public enum Category {
    RENDER("render", "stuff on the screen", new Color(255, 124, 191)),
    COMBAT("combat", "elite combate advantages", new Color(172, 64, 231)),
    PLAYER("player", "Mods that change player behaviour", new Color(38, 217, 163)),
    SERVICE("service", "Background mods", new Color(0, 0, 0)),
    CLIENT("client", "Always on mods", new Color(255, 255, 255));

    private final String Name;
    private final String description;
    private final Color color;

    private Category(String name, String description, Color color) {
        this.Name = name;
        this.description = description;
        this.color = color;
    }

    public String getName() {
        return this.Name;
    }

    public String getDescription() {
        return this.description;
    }

    public Color getColor() {
        return this.color;
    }
}
