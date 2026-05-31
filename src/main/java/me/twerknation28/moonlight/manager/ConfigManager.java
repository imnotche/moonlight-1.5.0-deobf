package me.twerknation28.moonlight.manager;

import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.OpenOption;
import java.util.Iterator;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import me.twerknation28.moonlight.features.settings.EnumConverter;
import me.twerknation28.moonlight.features.settings.Bind;
import com.google.gson.JsonElement;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.util.traits.Jsonable;
import java.util.List;
import com.google.gson.Gson;
import java.nio.file.Path;

public class ConfigManager
{
    private static final Path MOONLIGHT_PATH;
    private static final Gson gson;
    private final List<Jsonable> jsonables;
    
    public ConfigManager() {
        this.jsonables = List.of(Moonlight.friendManager, Moonlight.moduleManager, Moonlight.commandManager);
    }
    
    public static void setValueFromJson(final Feature feature, final Setting setting, final JsonElement element) {
        final String type = setting.getType();
        switch (type) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(element.getAsFloat());
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                final String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind(element.getAsInt()));
                break;
            }
            case "Enum": {
                try {
                    final EnumConverter converter = new EnumConverter((Class<? extends Enum>) setting.getValue().getClass());
                    final Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                }
                catch (final Exception ex) {}
                break;
            }
            default: {
                Moonlight.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
                break;
            }
        }
    }
    
    public void load() {
        if (!ConfigManager.MOONLIGHT_PATH.toFile().exists()) {
            ConfigManager.MOONLIGHT_PATH.toFile().mkdirs();
        }
        for (final Jsonable jsonable : this.jsonables) {
            if (!ConfigManager.MOONLIGHT_PATH.resolve(jsonable.getFileName()).toFile().exists()) {
                continue;
            }
            try {
                final String read = Files.readString(ConfigManager.MOONLIGHT_PATH.resolve(jsonable.getFileName()));
                jsonable.fromJson(JsonParser.parseString(read));
            }
            catch (final Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    public void save() {
        if (!ConfigManager.MOONLIGHT_PATH.toFile().exists()) {
            ConfigManager.MOONLIGHT_PATH.toFile().mkdirs();
        }
        for (final Jsonable jsonable : this.jsonables) {
            try {
                final JsonElement json = jsonable.toJson();
                Files.writeString(ConfigManager.MOONLIGHT_PATH.resolve(jsonable.getFileName()), ConfigManager.gson.toJson(json), new OpenOption[0]);
            }
            catch (final Throwable t) {}
        }
    }
    
    static {
        MOONLIGHT_PATH = FabricLoader.getInstance().getGameDir().resolve("moonlight");
        gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
    }
}
