package me.twerknation28.moonlight.features.modules;

import java.lang.invoke.CallSite;
import java.lang.reflect.UndeclaredThrowableException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.StringConcatFactory;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import me.twerknation28.moonlight.manager.ConfigManager;
import java.util.Iterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.event.impl.ClientEvent;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.features.settings.Bind;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.util.traits.Jsonable;
import me.twerknation28.moonlight.features.Feature;

public class Module extends Feature implements Jsonable
{
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled;
    public Setting<Boolean> drawn;
    public Setting<Bind> bind;
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    
    public Module(final String name, final String description, final Category category, final boolean hasListener, final boolean hidden, final boolean alwaysListening) {
        super(name);
        this.enabled = this.register(new Setting<Boolean>("Enabled", false));
        this.drawn = this.register(new Setting<Boolean>("Drawn", true));
        this.bind = this.register(new Setting<Bind>("Keybind", new Bind(-1)));
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
    
    public void onAttackBlock(final AttackBlockEvent event) {
    }
    
    public void onPacketSend(final PacketEvent.Send event) {
    }
    
    public void onPacketReceive(final PacketEvent.Receive event) {
    }
    
    public void onRender2D(final Render2DEvent event) {
    }
    
    public void onRender3D(final Render3DEvent event) {
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
        return !this.enabled.getValue();
    }
    
    public void setEnabled(final boolean enabled) {
        if (enabled) {
            this.enable();
        }
        else {
            this.disable();
        }
    }
    
    public void enable() {
        this.enabled.setValue(true);
        this.onToggle();
        this.onEnable();
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            Module.EVENT_BUS.register(this);
        }
        if (Notifications.getInstance().isOn() && Notifications.getInstance().modToggles.getValue()) {
            Command.toggleMessage(this.getName(), true);
        }
    }
    
    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            Module.EVENT_BUS.unregister(this);
        }
        this.enabled.setValue(false);
        this.onToggle();
        this.onDisable();
        if (Notifications.getInstance().isOn() && Notifications.getInstance().modToggles.getValue()) {
            Command.toggleMessage(this.getName(), false);
        }
    }
    
    public void enableSafe() {
        this.enabled.setValue(true);
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            Module.EVENT_BUS.register(this);
        }
    }
    
    public void disableSafe() {
        this.enabled.setValue(false);
        if (this.hasListener && !this.alwaysListening) {
            Module.EVENT_BUS.unregister(this);
        }
    }
    
    public void toggle() {
        final ClientEvent event = new ClientEvent(this.isEnabled() ? 0 : 1, this);
        Module.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            this.setEnabled(!this.isEnabled());
        }
    }
    
    public void toggleSafe() {
        final ClientEvent event = new ClientEvent(this.isEnabled() ? 0 : 1, this);
        Module.EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            if (!this.isEnabled()) {
                this.enableSafe();
            }
            else {
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
    
    public void setDrawn(final boolean drawn) {
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
    
    public void setBind(final int key) {
        this.bind.setValue(new Bind(key));
    }
    
    public boolean listening() {
        return (this.hasListener && this.isOn()) || this.alwaysListening;
    }
    
    public String getFullArrayString() {
        return this.getDisplayName() + String.valueOf(Formatting.GRAY) + ((this.getDisplayInfo() != null) ? /* invokedynamic(!) */ProcyonInvokeDynamicHelper_1.invoke(String.valueOf(Formatting.WHITE), this.getDisplayInfo(), String.valueOf(Formatting.GRAY)) : "");
    }
    
    @Override
    public JsonElement toJson() {
        final JsonObject object = new JsonObject();
        for (final Setting<?> setting : this.getSettings()) {
            try {
                final Object value = setting.getValue();
                if (value instanceof final Bind bind) {
                    object.addProperty(setting.getName(), bind.getKey());
                }
                else {
                    object.addProperty(setting.getName(), setting.getValueAsString());
                }
            }
            catch (final Throwable t) {}
        }
        return object;
    }
    
    @Override
    public void fromJson(final JsonElement element) {
        final JsonObject object = element.getAsJsonObject();
        final String enabled = object.get("Enabled").getAsString();
        if (Boolean.parseBoolean(enabled)) {
            this.toggleSafe();
        }
        for (final Setting<?> setting : this.getSettings()) {
            try {
                ConfigManager.setValueFromJson(this, setting, object.get(setting.getName()));
            }
            catch (final Throwable t) {}
        }
    }
    
    // This helper class was generated by Procyon to approximate the behavior of an
    // 'invokedynamic' instruction that it doesn't know how to interpret.
    private static final class ProcyonInvokeDynamicHelper_1
    {
        private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
        private static MethodHandle handle;
        private static volatile int fence;
        
        private static MethodHandle handle() {
            final MethodHandle handle = ProcyonInvokeDynamicHelper_1.handle;
            if (handle != null)
                return handle;
            return ProcyonInvokeDynamicHelper_1.ensureHandle();
        }
        
        private static MethodHandle ensureHandle() {
            ProcyonInvokeDynamicHelper_1.fence = 0;
            MethodHandle handle = ProcyonInvokeDynamicHelper_1.handle;
            if (handle == null) {
                MethodHandles.Lookup lookup = ProcyonInvokeDynamicHelper_1.LOOKUP;
                try {
                    handle = ((CallSite)StringConcatFactory.makeConcatWithConstants(lookup, "makeConcatWithConstants", MethodType.methodType(String.class, String.class, String.class, String.class), " [\u0001\u0001\u0001]")).dynamicInvoker();
                }
                catch (Throwable t) {
                    throw new UndeclaredThrowableException(t);
                }
                ProcyonInvokeDynamicHelper_1.fence = 1;
                ProcyonInvokeDynamicHelper_1.handle = handle;
                ProcyonInvokeDynamicHelper_1.fence = 0;
            }
            return handle;
        }
        
        private static String invoke(String p0, String p1, String p2) {
            try {
                return ProcyonInvokeDynamicHelper_1.handle().invokeExact(p0, p1, p2).toString();
            }
            catch (Throwable t) {
                throw new UndeclaredThrowableException(t);
            }
        }
    }
}
