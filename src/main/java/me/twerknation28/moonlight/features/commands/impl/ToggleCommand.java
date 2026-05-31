package me.twerknation28.moonlight.features.commands.impl;

import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;

public class ToggleCommand extends Command
{
    public ToggleCommand() {
        super("toggle", new String[] { "<module>" });
    }
    
    @Override
    public void execute(final String[] var1) {
        if (var1.length < 1 || var1[0] == null) {
            this.notFound();
            return;
        }
        final Module mod = Moonlight.moduleManager.getModuleByName(var1[0]);
        if (mod == null) {
            this.notFound();
            return;
        }
        mod.toggle();
    }
    
    private void notFound() {
        Command.sendMessage("Module is not found.");
    }
}
