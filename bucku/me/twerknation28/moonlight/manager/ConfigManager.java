package me.twerknation28.moonlight.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.settings.Bind;
import me.twerknation28.moonlight.features.settings.EnumConverter;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.util.traits.Jsonable;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
    private static final Path MOONLIGHT_PATH = FabricLoader.getInstance().getGameDir().resolve(Moonlight.NAME);
    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private final List<Jsonable> jsonables = List.of(Moonlight.friendManager, Moonlight.moduleManager, Moonlight.commandManager);

    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        switch (setting.getType()) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(Float.valueOf(element.getAsFloat()));
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind(element.getAsInt()));
                break;
            }
            case "Enum": {
                try {
                    EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                }
                catch (Exception exception) {}
                break;
            }
            default: {
                Moonlight.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
            }
        }
    }

    public void load() {
        if (!MOONLIGHT_PATH.toFile().exists()) {
            MOONLIGHT_PATH.toFile().mkdirs();
        }
        for (Jsonable jsonable : this.jsonables) {
            if (!MOONLIGHT_PATH.resolve(jsonable.getFileName()).toFile().exists()) continue;
            try {
                String read = Files.readString(MOONLIGHT_PATH.resolve(jsonable.getFileName()));
                jsonable.fromJson(JsonParser.parseString(read));
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (!MOONLIGHT_PATH.toFile().exists()) {
            MOONLIGHT_PATH.toFile().mkdirs();
        }
        for (Jsonable jsonable : this.jsonables) {
            try {
                JsonElement json = jsonable.toJson();
                Files.writeString(MOONLIGHT_PATH.resolve(jsonable.getFileName()), (CharSequence)gson.toJson(json), new OpenOption[0]);
            }
            catch (Throwable throwable) {}
        }
    }
}
