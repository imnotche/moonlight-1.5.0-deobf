package me.twerknation28.moonlight.features.commands.impl;

import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;

public class ToggleCommand
extends Command {
    public ToggleCommand() {
        super("toggle", new String[]{"<module>"});
    }

    @Override
    public void execute(String[] var1) {
        if (var1.length < 1 || var1[0] == null) {
            this.notFound();
            return;
        }
        Module mod = Moonlight.moduleManager.getModuleByName(var1[0]);
        if (mod == null) {
            this.notFound();
            return;
        }
        mod.toggle();
    }

    private void notFound() {
        ToggleCommand.sendMessage("Module is not found.");
    }
}
