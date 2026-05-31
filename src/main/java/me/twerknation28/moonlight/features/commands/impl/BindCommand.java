package me.twerknation28.moonlight.features.commands.impl;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.features.settings.Bind;
import me.twerknation28.moonlight.util.KeyboardUtil;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.event.impl.KeyEvent;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.commands.Command;

public class BindCommand extends Command
{
    private boolean listening;
    private Module module;
    
    public BindCommand() {
        super("bind", new String[] { "<module>" });
        BindCommand.EVENT_BUS.register(this);
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage("Please specify a module.");
            return;
        }
        final String moduleName = commands[0];
        final Module module = Moonlight.moduleManager.getModuleByName(moduleName);
        if (module == null) {
            Command.sendMessage("Unknown module '" + String.valueOf(module) + "'!");
            return;
        }
        Command.sendMessage(String.valueOf(Formatting.GRAY) + "Press a key.");
        this.listening = true;
        this.module = module;
    }
    
    @Subscribe
    private void onKey(final KeyEvent event) {
        if (Feature.nullCheck() || !this.listening) {
            return;
        }
        this.listening = false;
        if (event.getKey() == 256) {
            Command.sendMessage(String.valueOf(Formatting.GRAY) + "Operation cancelled.");
            return;
        }
        Command.sendMessage("Bind for " + String.valueOf(Formatting.GREEN) + this.module.getName() + String.valueOf(Formatting.WHITE) + " set to " + String.valueOf(Formatting.GRAY) + KeyboardUtil.getKeyName(event.getKey()));
        this.module.bind.setValue(new Bind(event.getKey()));
    }
}
