package me.twerknation28.moonlight.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import java.util.Iterator;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.LinkedList;
import me.twerknation28.moonlight.features.commands.impl.HelpCommand;
import me.twerknation28.moonlight.features.commands.impl.NameCommand;
import me.twerknation28.moonlight.features.commands.impl.PrefixCommand;
import me.twerknation28.moonlight.features.commands.impl.ModuleCommand;
import me.twerknation28.moonlight.features.commands.impl.FriendCommand;
import me.twerknation28.moonlight.features.commands.impl.BindCommand;
import me.twerknation28.moonlight.features.commands.impl.ToggleCommand;
import java.util.ArrayList;
import me.twerknation28.moonlight.features.commands.Command;
import java.util.List;
import me.twerknation28.moonlight.util.traits.Jsonable;
import me.twerknation28.moonlight.features.Feature;

public class CommandManager extends Feature implements Jsonable
{
    private final List<Command> commands;
    private String clientMessage;
    private String prefix;
    
    public CommandManager() {
        super("Command");
        this.commands = new ArrayList<Command>();
        this.clientMessage = "[\u263d]";
        this.prefix = ".";
        this.commands.add(new ToggleCommand());
        this.commands.add(new BindCommand());
        this.commands.add(new FriendCommand());
        this.commands.add(new ModuleCommand());
        this.commands.add(new PrefixCommand());
        this.commands.add(new NameCommand());
        this.commands.add(new HelpCommand());
    }
    
    public static String[] removeElement(final String[] input, final int indexToDelete) {
        final LinkedList<String> result = new LinkedList<String>();
        for (int i = 0; i < input.length; ++i) {
            if (i != indexToDelete) {
                result.add(input[i]);
            }
        }
        return result.toArray(input);
    }
    
    private static String strip(final String str, final String key) {
        if (str.startsWith(key) && str.endsWith(key)) {
            return str.substring(key.length(), str.length() - key.length());
        }
        return str;
    }
    
    public void executeCommand(final String command) {
        final String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String name = parts[0].substring(1);
        final String[] args = removeElement(parts, 0);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                args[i] = strip(args[i], "\"");
            }
        }
        for (final Command c : this.commands) {
            if (!c.getName().equalsIgnoreCase(name)) {
                continue;
            }
            c.execute(parts);
            return;
        }
        final String message = "Command not found, type 'help' for the commands list.";
        final Text text = (Text)Text.literal(String.valueOf(Formatting.GRAY) + message);
        CommandManager.mc.inGameHud.getChatHud().addMessage(text);
    }
    
    public Command getCommandByName(final String name) {
        for (final Command command : this.commands) {
            if (!command.getName().equals(name)) {
                continue;
            }
            return command;
        }
        return null;
    }
    
    public List<Command> getCommands() {
        return this.commands;
    }
    
    public String getClientMessage() {
        if (Notifications.getInstance().compact.getValue()) {
            return "[\u263d]";
        }
        return "[moonlight]";
    }
    
    public void setClientMessage(final String clientMessage) {
        this.clientMessage = clientMessage;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    @Override
    public JsonElement toJson() {
        final JsonObject object = new JsonObject();
        object.addProperty("prefix", this.prefix);
        return object;
    }
    
    @Override
    public void fromJson(final JsonElement element) {
        this.setPrefix(element.getAsJsonObject().get("prefix").getAsString());
    }
    
    @Override
    public String getFileName() {
        return "commands.json";
    }
}
