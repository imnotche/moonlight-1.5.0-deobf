package me.twerknation28.moonlight.features.commands.impl;

import java.util.Iterator;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;

public class HelpCommand extends Command
{
    public HelpCommand() {
        super("help");
    }
    
    @Override
    public void execute(final String[] commands) {
        Command.sendMessage("Commands: ");
        for (final Command command : Moonlight.commandManager.getCommands()) {
            final StringBuilder builder = new StringBuilder(Formatting.GRAY.toString());
            builder.append(Moonlight.commandManager.getPrefix());
            builder.append(command.getName());
            builder.append(" ");
            for (final String cmd : command.getCommands()) {
                builder.append(cmd);
                builder.append(" ");
            }
            Command.sendMessage(builder.toString());
        }
    }
}
