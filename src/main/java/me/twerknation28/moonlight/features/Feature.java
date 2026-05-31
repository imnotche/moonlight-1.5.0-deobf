package me.twerknation28.moonlight.features;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import me.twerknation28.moonlight.features.settings.Setting;
import java.util.List;
import me.twerknation28.moonlight.util.Util;

public class Feature implements Util
{
    public List<Setting<?>> settings;
    private String name;
    
    public Feature() {
        this.settings = new ArrayList<Setting<?>>();
    }
    
    public Feature(final String name) {
        this.settings = new ArrayList<Setting<?>>();
        this.name = name;
    }
    
    public static boolean nullCheck() {
        return Feature.mc.player == null;
    }
    
    public static boolean fullNullCheck() {
        return Feature.mc.player == null || Feature.mc.world == null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<Setting<?>> getSettings() {
        return this.settings;
    }
    
    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }
    
    public boolean isEnabled() {
        return false;
    }
    
    public boolean isDisabled() {
        return !this.isEnabled();
    }
    
    public <T> Setting<T> register(final Setting<T> setting) {
        setting.setFeature(this);
        this.settings.add(setting);
        return setting;
    }
    
    public void unregister(final Setting<?> settingIn) {
        final ArrayList<Setting<?>> removeList = new ArrayList<Setting<?>>();
        for (final Setting<?> setting : this.settings) {
            if (!setting.equals(settingIn)) {
                continue;
            }
            removeList.add(setting);
        }
        if (!removeList.isEmpty()) {
            this.settings.removeAll(removeList);
        }
    }
    
    public Setting<?> getSettingByName(final String name) {
        for (final Setting<?> setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return setting;
        }
        return null;
    }
    
    public void reset() {
        for (final Setting<?> setting : this.settings) {
            setting.reset();
        }
    }
    
    public void clearSettings() {
        this.settings = new ArrayList<Setting<?>>();
    }
}
