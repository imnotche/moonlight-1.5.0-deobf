package me.twerknation28.moonlight.features.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.impl.ClientEvent;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.features.settings.Bind;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.ConfigManager;
import me.twerknation28.moonlight.util.traits.Jsonable;
import net.minecraft.util.Formatting;

public class Module
extends Feature
implements Jsonable {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = this.register(new Setting<Boolean>("Enabled", false));
    public Setting<Boolean> drawn = this.register(new Setting<Boolean>("Drawn", true));
    public Setting<Bind> bind = this.register(new Setting<Bind>("Keybind", new Bind(-1)));
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;

    public Module(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name);
        this.displayName = this.register(new Setting<String>("DisplayName", name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onUpdate() {
    }

    public void onAttackBlock(AttackBlockEvent event) {
    }

    public void onPacketSend(PacketEvent.Send event) {
    }

    public void onPacketReceive(PacketEvent.Receive event) {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    public void onUnload() {
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return this.enabled.getValue() == false;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.enabled.setValue(true);
        this.onToggle();
        this.onEnable();
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            EVENT_BUS.register(this);
        }
        if (Notifications.getInstance().isOn() && Notifications.getInstance().modToggles.getValue().booleanValue()) {
            Command.toggleMessage(this.getName(), true);
        }
    }

    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            EVENT_BUS.unregister(this);
        }
        this.enabled.setValue(false);
        this.onToggle();
        this.onDisable();
        if (Notifications.getInstance().isOn() && Notifications.getInstance().modToggles.getValue().booleanValue()) {
            Command.toggleMessage(this.getName(), false);
        }
    }

    public void enableSafe() {
        this.enabled.setValue(true);
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            EVENT_BUS.register(this);
        }
    }

    public void disableSafe() {
        this.enabled.setValue(false);
        if (this.hasListener && !this.alwaysListening) {
            EVENT_BUS.unregister(this);
        }
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public void toggleSafe() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            if (!this.isEnabled()) {
                this.enableSafe();
            } else {
                this.disableSafe();
            }
        }
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }

    @Override
    public boolean isEnabled() {
        return this.isOn();
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return this.hasListener && this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + String.valueOf(Formatting.GRAY) + (String)(this.getDisplayInfo() != null ? " [" + String.valueOf(Formatting.WHITE) + this.getDisplayInfo() + String.valueOf(Formatting.GRAY) + "]" : "");
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        for (Setting<?> setting : this.getSettings()) {
            try {
                Object obj = setting.getValue();
                if (obj instanceof Bind) {
                    Bind bind = (Bind)obj;
                    object.addProperty(setting.getName(), bind.getKey());
                    continue;
                }
                object.addProperty(setting.getName(), setting.getValueAsString());
            }
            catch (Throwable throwable) {}
        }
        return object;
    }

    @Override
    public void fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String enabled = object.get("Enabled").getAsString();
        if (Boolean.parseBoolean(enabled)) {
            this.toggleSafe();
        }
        for (Setting<?> setting : this.getSettings()) {
            try {
                ConfigManager.setValueFromJson(this, setting, object.get(setting.getName()));
            }
            catch (Throwable throwable) {}
        }
    }
}
