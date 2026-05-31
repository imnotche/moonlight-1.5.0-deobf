package me.twerknation28.moonlight.features.commands.impl;

import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;
import net.minecraft.util.Formatting;

public class HelpCommand
extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Moonlight.commandManager.getCommands()) {
            StringBuilder builder = new StringBuilder(Formatting.GRAY.toString());
            builder.append(Moonlight.commandManager.getPrefix());
            builder.append(command.getName());
            builder.append(" ");
            for (String cmd : command.getCommands()) {
                builder.append(cmd);
                builder.append(" ");
            }
            HelpCommand.sendMessage(builder.toString());
        }
    }
}
