package me.twerknation28.moonlight.features.commands.impl;

import java.util.Iterator;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.manager.ConfigManager;
import com.google.gson.JsonParser;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;

public class ModuleCommand extends Command
{
    public ModuleCommand() {
        super("module", new String[] { "<module>", "<set/reset>", "<setting>", "<value>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Modules: ");
            for (Category category : Moonlight.moduleManager.getCategories()) {
                String modules = category.getName() + ": ";
                for (Module module1 : Moonlight.moduleManager.getModulesByCategory(category)) {
                    modules = modules + String.valueOf(module1.isEnabled() ? Formatting.GREEN : Formatting.RED) + module1.getName() + String.valueOf(Formatting.WHITE) + ", ";
                }
                Command.sendMessage(modules);
            }
            return;
        }
        Module module2 = Moonlight.moduleManager.getModuleByDisplayName(commands[0]);
        if (module2 == null) {
            module2 = Moonlight.moduleManager.getModuleByName(commands[0]);
            if (module2 == null) {
                Command.sendMessage("This module doesnt exist.");
                return;
            }
            Command.sendMessage(" This is the original name of the module. Its current name is: " + module2.getDisplayName());
        }
        else {
            if (commands.length == 2) {
                Command.sendMessage(module2.getDisplayName() + " : " + module2.getDescription());
                for (Setting setting2 : module2.getSettings()) {
                    Command.sendMessage(setting2.getName() + " : " + String.valueOf(setting2.getValue()) + ", " + setting2.getDescription());
                }
                return;
            }
            if (commands.length == 3) {
                if (commands[1].equalsIgnoreCase("set")) {
                    Command.sendMessage("Please specify a setting.");
                }
                else if (commands[1].equalsIgnoreCase("reset")) {
                    for (final Setting setting3 : module2.getSettings()) {
                        setting3.setValue(setting3.getDefaultValue());
                    }
                }
                else {
                    Command.sendMessage("This command doesnt exist.");
                }
                return;
            }
            if (commands.length == 4) {
                Command.sendMessage("Please specify a value.");
                return;
            }
            final Setting setting4;
            if (commands.length == 5 && (setting4 = module2.getSettingByName(commands[2])) != null) {
                final JsonParser jp = new JsonParser();
                if (setting4.getType().equalsIgnoreCase("String")) {
                    setting4.setValue(commands[3]);
                    Command.sendMessage(String.valueOf(Formatting.DARK_GRAY) + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3]);
                    return;
                }
                try {
                    if (setting4.getName().equalsIgnoreCase("Enabled")) {
                        if (commands[3].equalsIgnoreCase("true")) {
                            module2.enable();
                        }
                        if (commands[3].equalsIgnoreCase("false")) {
                            module2.disable();
                        }
                    }
                    ConfigManager.setValueFromJson(module2, setting4, jp.parse(commands[3]));
                }
                catch (final Exception e) {
                    Command.sendMessage("Bad Value! This setting requires a: " + setting4.getType() + " value.");
                    return;
                }
                Command.sendMessage(String.valueOf(Formatting.GRAY) + module2.getName() + " " + setting4.getName() + " has been set to " + commands[3]);
            }
        }
    }
}
