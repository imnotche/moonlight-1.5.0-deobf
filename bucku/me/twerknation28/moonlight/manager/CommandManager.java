package me.twerknation28.moonlight.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.commands.impl.BindCommand;
import me.twerknation28.moonlight.features.commands.impl.FriendCommand;
import me.twerknation28.moonlight.features.commands.impl.HelpCommand;
import me.twerknation28.moonlight.features.commands.impl.ModuleCommand;
import me.twerknation28.moonlight.features.commands.impl.NameCommand;
import me.twerknation28.moonlight.features.commands.impl.PrefixCommand;
import me.twerknation28.moonlight.features.commands.impl.ToggleCommand;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.util.traits.Jsonable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandManager
extends Feature
implements Jsonable {
    private final List<Command> commands = new ArrayList<Command>();
    private String clientMessage = "[\u263d]";
    private String prefix = ".";

    public CommandManager() {
        super("Command");
        this.commands.add(new ToggleCommand());
        this.commands.add(new BindCommand());
        this.commands.add(new FriendCommand());
        this.commands.add(new ModuleCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new NameCommand());
        this.commands.add(new HelpCommand());
    }

    public static String[] removeElement(String[] input, int indexToDelete) {
        LinkedList<String> result = new LinkedList<String>();
        for (int i = 0; i < input.length; ++i) {
            if (i == indexToDelete) continue;
            result.add(input[i]);
        }
        return result.toArray(input);
    }

    private static String strip(String str, String key) {
        if (str.startsWith(key) && str.endsWith(key)) {
            return str.substring(key.length(), str.length() - key.length());
        }
        return str;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        String name = parts[0].substring(1);
        String[] args = CommandManager.removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) continue;
            args[i] = CommandManager.strip(args[i], "\"");
        }
        for (Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) continue;
            c.execute(parts);
            return;
        }
        String message = "Command not found, type 'help' for the commands list.";
        MutableText text = Text.literal((String)(String.valueOf(Formatting.GRAY) + message));
        CommandManager.mc.inGameHud.getChatHud().addMessage((Text)text);
    }

    public Command getCommandByName(String name) {
        for (Command command : this.commands) {
            if (!command.getName().equals(name)) continue;
            return command;
        }
        return null;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public String getClientMessage() {
        if (Notifications.getInstance().compact.getValue().booleanValue()) {
            return "[\u263d]";
        }
        return "[moonlight]";
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("prefix", this.prefix);
        return object;
    }

    @Override
    public void fromJson(JsonElement element) {
        this.setPrefix(element.getAsJsonObject().get("prefix").getAsString());
    }

    @Override
    public String getFileName() {
        return "commands.json";
    }
}
