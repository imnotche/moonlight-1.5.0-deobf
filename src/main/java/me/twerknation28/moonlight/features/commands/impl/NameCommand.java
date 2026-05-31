package me.twerknation28.moonlight.features.commands.impl;

import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.misc.NameProtect;

public class NameCommand
extends Command {
    public NameCommand() {
        super("nameprotect");
    }

    @Override
    public void execute(String[] commands) {
        NameProtect.INSTANCE.newName.setValue(commands[0]);
        Command.sendMessage("Name changed to " + commands[0]);
    }
}
