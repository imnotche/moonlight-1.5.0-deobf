package me.twerknation28.moonlight.features.commands;

import java.util.Objects;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.util.ChatUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

public abstract class Command
extends Feature {
    protected String name;
    protected String[] commands;

    public Command(String name) {
        super(name);
        this.name = name;
        this.commands = new String[]{""};
    }

    public Command(String name, String[] commands) {
        super(name);
        this.name = name;
        this.commands = commands;
    }

    public static void sendMessage(String message) {
        MutableText prefixText = Text.literal((String)Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb((int)255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        MutableText messageText = Text.literal((String)(" " + message)).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        MutableText finalMessage = prefixText.append((Text)messageText);
        ChatUtil.send((Text)finalMessage);
    }

    public static void toggleMessage(String module, Boolean enabling) {
        MutableText finalMessage;
        if (Objects.equals(module, "ClickGui") || Objects.equals(module, "ElytraSwap") || Objects.equals(module, "Phase")) {
            return;
        }
        MutableText prefixText = Text.literal((String)Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb((int)255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        if (Notifications.getInstance().compact.getValue().booleanValue()) {
            MutableText closeBracket;
            MutableText toggleSign;
            MutableText openBracket;
            if (enabling.booleanValue()) {
                openBracket = Text.literal((String)" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
                toggleSign = Text.literal((String)"+").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
                closeBracket = Text.literal((String)"]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            } else {
                openBracket = Text.literal((String)" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                toggleSign = Text.literal((String)"-").setStyle(Style.EMPTY.withColor(Formatting.RED));
                closeBracket = Text.literal((String)"]").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
            }
            MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
            MutableText messageText = Text.literal((String)(" " + module)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            finalMessage = prefixText.append((Text)toggleText.append((Text)messageText));
        } else {
            MutableText toggleText = Text.literal((String)(" " + module + " toggled ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText enabledText = enabling != false ? Text.literal((String)"on").setStyle(Style.EMPTY.withColor(Formatting.GREEN)) : Text.literal((String)"off").setStyle(Style.EMPTY.withColor(Formatting.RED));
            MutableText periodText = Text.literal((String)".").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            finalMessage = prefixText.append((Text)toggleText.append((Text)enabledText.append((Text)periodText)));
        }
        ChatUtil.send((Text)finalMessage);
    }

    public static void visualRangeMessage(String playerName, Boolean entering) {
        MutableText closeBracket;
        MutableText toggleSign;
        MutableText openBracket;
        MutableText prefixText = Text.literal((String)Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb((int)255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        if (entering.booleanValue()) {
            openBracket = Text.literal((String)" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            toggleSign = Text.literal((String)"\ud83d\udc41").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            closeBracket = Text.literal((String)"]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
        } else {
            openBracket = Text.literal((String)" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
            toggleSign = Text.literal((String)"\ud83d\udc41").setStyle(Style.EMPTY.withColor(Formatting.RED));
            closeBracket = Text.literal((String)"]").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
        }
        MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
        MutableText messageText = Text.literal((String)(" " + playerName)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        MutableText finalMessage = prefixText.append((Text)toggleText.append((Text)messageText));
        ChatUtil.send((Text)finalMessage);
    }

    public static void enableMessage(String module) {
        MutableText finalMessage;
        MutableText prefixText = Text.literal((String)Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb((int)255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        if (Notifications.getInstance().compact.getValue().booleanValue()) {
            MutableText openBracket = Text.literal((String)" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            MutableText toggleSign = Text.literal((String)"+").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            MutableText closeBracket = Text.literal((String)"]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
            MutableText messageText = Text.literal((String)(" " + module)).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            finalMessage = prefixText.append((Text)toggleText.append((Text)messageText));
        } else {
            MutableText toggleText = Text.literal((String)(" " + module + " toggled ")).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText enabledText = Text.literal((String)"on").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            MutableText periodText = Text.literal((String)".").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            finalMessage = prefixText.append((Text)toggleText.append((Text)enabledText.append((Text)periodText)));
        }
        ChatUtil.send((Text)finalMessage);
    }

    public static void sendSilentMessage(String message) {
        if (Command.nullCheck()) {
            return;
        }
        Command.mc.inGameHud.getChatHud().addMessage((Text)Text.literal((String)message));
    }

    public static String getCommandPrefix() {
        return Moonlight.commandManager.getPrefix();
    }

    public abstract void execute(String[] var1);

    @Override
    public String getName() {
        return this.name;
    }

    public String[] getCommands() {
        return this.commands;
    }
}
