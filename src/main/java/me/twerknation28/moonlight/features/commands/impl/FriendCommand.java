package me.twerknation28.moonlight.features.commands.impl;

import java.util.Iterator;
import me.twerknation28.moonlight.manager.FriendManager;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.commands.Command;

public class FriendCommand extends Command
{
    public FriendCommand() {
        super("friend", new String[] { "<add/del/name/clear>", "<name>" });
    }
    
    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1) {
            if (Moonlight.friendManager.getFriends().isEmpty()) {
                Command.sendMessage("Friend list empty D:.");
            }
            else {
                final StringBuilder f = new StringBuilder("Friends: ");
                for (final String friend : Moonlight.friendManager.getFriends()) {
                    try {
                        f.append(friend).append(", ");
                    }
                    catch (final Exception ex) {}
                }
                Command.sendMessage(f.toString());
            }
            return;
        }
        if (commands.length != 2) {
            if (commands.length >= 2) {
                final String s = commands[0];
                switch (s) {
                    case "add": {
                        Moonlight.friendManager.addFriend(commands[1]);
                        Command.sendMessage(String.valueOf(Formatting.GREEN) + commands[1] + " has been friended");
                        return;
                    }
                    case "del":
                    case "remove": {
                        Moonlight.friendManager.removeFriend(commands[1]);
                        Command.sendMessage(String.valueOf(Formatting.RED) + commands[1] + " has been unfriended");
                        return;
                    }
                    default: {
                        Command.sendMessage("Unknown Command, try friend add/del (name)");
                        break;
                    }
                }
            }
            return;
        }
        if (commands[0].equals("reset")) {
            Moonlight.friendManager.getFriends().clear();
            Command.sendMessage("Friends got reset.");
            return;
        }
        final String s2 = commands[0];
        final FriendManager friendManager = Moonlight.friendManager;
        Command.sendMessage(s2 + (FriendManager.isFriend(commands[0]) ? " is friended." : " isn't friended."));
    }
}
