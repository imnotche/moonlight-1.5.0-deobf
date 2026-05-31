package me.twerknation28.moonlight.features.commands.impl;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.event.impl.KeyEvent;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Bind;
import me.twerknation28.moonlight.util.KeyboardUtil;
import net.minecraft.util.Formatting;

public class BindCommand
extends Command {
    private boolean listening;
    private Module module;

    public BindCommand() {
        super("bind", new String[]{"<module>"});
        EVENT_BUS.register(this);
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            BindCommand.sendMessage("Please specify a module.");
            return;
        }
        String moduleName = commands[0];
        Module module = Moonlight.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            BindCommand.sendMessage("Unknown module '" + String.valueOf(module) + "'!");
            return;
        }
        BindCommand.sendMessage(String.valueOf(Formatting.GRAY) + "Press a key.");
        this.listening = true;
        this.module = module;
    }

    @Subscribe
    private void onKey(KeyEvent event) {
        if (BindCommand.nullCheck() || !this.listening) {
            return;
        }
        this.listening = false;
        if (event.getKey() == 256) {
            BindCommand.sendMessage(String.valueOf(Formatting.GRAY) + "Operation cancelled.");
            return;
        }
        BindCommand.sendMessage("Bind for " + String.valueOf(Formatting.GREEN) + this.module.getName() + String.valueOf(Formatting.WHITE) + " set to " + String.valueOf(Formatting.GRAY) + KeyboardUtil.getKeyName(event.getKey()));
        this.module.bind.setValue(new Bind(event.getKey()));
    }
}
