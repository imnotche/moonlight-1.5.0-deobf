package me.twerknation28.moonlight.features.commands.impl;

import me.twerknation28.moonlight.Moonlight;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.features.commands.Command;

public class PrefixCommand extends Command
{
    public PrefixCommand() {
        super("prefix", new String[] { "<char>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(String.valueOf(Formatting.GREEN) + "Current prefix is " + Moonlight.commandManager.getPrefix());
            return;
        }
        Moonlight.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + String.valueOf(Formatting.GRAY) + commands[0]);
    }
}
