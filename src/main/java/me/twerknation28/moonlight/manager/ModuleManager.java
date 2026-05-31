package me.twerknation28.moonlight.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.twerknation28.moonlight.event.impl.AttackBlockEvent;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.event.impl.Render3DEvent;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.HudModule;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.features.modules.combat.AntiMiss;
import me.twerknation28.moonlight.features.modules.combat.AutoMine;
import me.twerknation28.moonlight.features.modules.combat.SuperTotem;
import me.twerknation28.moonlight.features.modules.misc.MiddleClick;
import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import me.twerknation28.moonlight.features.modules.movement.Flight;
import me.twerknation28.moonlight.features.modules.movement.NoJumpDelay;
import me.twerknation28.moonlight.features.modules.movement.PearlPhase;
import me.twerknation28.moonlight.features.modules.movement.Speed;
import me.twerknation28.moonlight.features.modules.movement.Sprint;
import me.twerknation28.moonlight.features.modules.movement.Velocity;
import me.twerknation28.moonlight.features.modules.player.AntiHunger;
import me.twerknation28.moonlight.features.modules.player.ElytraSwap;
import me.twerknation28.moonlight.features.modules.player.FakePlayer;
import me.twerknation28.moonlight.features.modules.player.FastPlace;
import me.twerknation28.moonlight.features.modules.render.BlockHighlight;
import me.twerknation28.moonlight.features.modules.render.DeathEffects;
import me.twerknation28.moonlight.features.modules.render.Fullbright;
import me.twerknation28.moonlight.features.modules.render.NoRender;
import me.twerknation28.moonlight.features.modules.render.PhaseESP;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.util.traits.Jsonable;

public class ModuleManager
        implements Jsonable,
        Util {
    public List<Module> modules = new ArrayList<Module>();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<String>();

    public void init() {
        this.modules.add(new HudModule());
        this.modules.add(new FastPlace());
        this.modules.add(new Velocity());
        this.modules.add(new NameProtect());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Fullbright());
        this.modules.add(new NoRender());
        this.modules.add(new Sprint());
        this.modules.add(new DeathEffects());
        this.modules.add(new FakePlayer());
        this.modules.add(new AntiMiss());
        this.modules.add(new Notifications());
        this.modules.add(new NoJumpDelay());
        this.modules.add(new ElytraSwap());
        this.modules.add(new PearlPhase());
        this.modules.add(new MiddleClick());
        this.modules.add(new PhaseESP());
        this.modules.add(new Flight());
        this.modules.add(new Speed());
        this.modules.add(new AntiHunger());
        this.modules.add(new SuperTotem());
        this.modules.add(new AutoMine());
        this.modules.add(new NewGui());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T)module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add((Module)module);
            }
        });
        return modulesCategory;
    }

    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(EVENT_BUS::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void AttackBlockEvent(AttackBlockEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onAttackBlock(event));
    }

    public void Send(PacketEvent.Send event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onPacketSend(event));
        InventoryManager.onPacketSend(event);
    }

    public void Receive(PacketEvent.Receive event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onPacketReceive(event));
        InventoryManager.onPacketReceive(event);
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> ModuleManager.mc.textRenderer.getWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void onUnload() {
        this.modules.forEach(EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey <= 0 || ModuleManager.mc.currentScreen != null) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        for (Module module : this.modules) {
            object.add(module.getName(), module.toJson());
        }
        return object;
    }

    @Override
    public void fromJson(JsonElement element) {
        for (Module module : this.modules) {
            module.fromJson(element.getAsJsonObject().get(module.getName()));
        }
    }

    @Override
    public String getFileName() {
        return "modules.json";
    }
}
