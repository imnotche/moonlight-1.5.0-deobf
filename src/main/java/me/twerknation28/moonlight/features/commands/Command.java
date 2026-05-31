package me.twerknation28.moonlight.features.commands;

import me.twerknation28.moonlight.features.modules.client.Notifications;
import java.util.Objects;
import net.minecraft.text.MutableText;
import me.twerknation28.moonlight.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.Feature;

public abstract class Command extends Feature
{
    protected String name;
    protected String[] commands;
    
    public Command(final String name) {
        super(name);
        this.name = name;
        this.commands = new String[] { "" };
    }
    
    public Command(final String name, final String[] commands) {
        super(name);
        this.name = name;
        this.commands = commands;
    }
    
    public static void sendMessage(final String message) {
        final MutableText prefixText = Text.literal(Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb(255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        final Text messageText = (Text)Text.literal(" " + message).setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        final Text finalMessage = (Text)prefixText.append(messageText);
        ChatUtil.send(finalMessage);
    }
    
    public static void toggleMessage(final String module, final Boolean enabling) {
        if (Objects.equals(module, "ClickGui") || Objects.equals(module, "ElytraSwap") || Objects.equals(module, "Phase")) {
            return;
        }
        final MutableText prefixText = Text.literal(Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb(255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        Text finalMessage;
        if (Notifications.getInstance().compact.getValue()) {
            MutableText openBracket;
            MutableText toggleSign;
            MutableText closeBracket;
            if (enabling) {
                openBracket = Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
                toggleSign = Text.literal("+").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
                closeBracket = Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            }
            else {
                openBracket = Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                toggleSign = Text.literal("-").setStyle(Style.EMPTY.withColor(Formatting.RED));
                closeBracket = Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
            }
            final MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
            final Text messageText = (Text)Text.literal(" " + module).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            finalMessage = (Text)prefixText.append((Text)toggleText.append(messageText));
        }
        else {
            final MutableText toggleText = Text.literal(" " + module + " toggled ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            MutableText enabledText;
            if (enabling) {
                enabledText = Text.literal("on").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            }
            else {
                enabledText = Text.literal("off").setStyle(Style.EMPTY.withColor(Formatting.RED));
            }
            final MutableText periodText = Text.literal(".").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            finalMessage = (Text)prefixText.append((Text)toggleText.append((Text)enabledText.append((Text)periodText)));
        }
        ChatUtil.send(finalMessage);
    }
    
    public static void visualRangeMessage(final String playerName, final Boolean entering) {
        final MutableText prefixText = Text.literal(Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb(255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        MutableText openBracket;
        MutableText toggleSign;
        MutableText closeBracket;
        if (entering) {
            openBracket = Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            toggleSign = Text.literal("\ud83d\udc41").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            closeBracket = Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
        }
        else {
            openBracket = Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
            toggleSign = Text.literal("\ud83d\udc41").setStyle(Style.EMPTY.withColor(Formatting.RED));
            closeBracket = Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
        }
        final MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
        final Text messageText = (Text)Text.literal(" " + playerName).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        final Text finalMessage = (Text)prefixText.append((Text)toggleText.append(messageText));
        ChatUtil.send(finalMessage);
    }
    
    public static void enableMessage(final String module) {
        final MutableText prefixText = Text.literal(Moonlight.commandManager.getClientMessage()).setStyle(Style.EMPTY.withColor(ColorHelper.Argb.getArgb(255, (int)NewGui.getInstance().red.getValue(), (int)NewGui.getInstance().green.getValue(), (int)NewGui.getInstance().blue.getValue())));
        Text finalMessage;
        if (Notifications.getInstance().compact.getValue()) {
            final MutableText openBracket = Text.literal(" [").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            final MutableText toggleSign = Text.literal("+").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            final MutableText closeBracket = Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN));
            final MutableText toggleText = openBracket.append((Text)toggleSign.append((Text)closeBracket));
            final Text messageText = (Text)Text.literal(" " + module).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
            finalMessage = (Text)prefixText.append((Text)toggleText.append(messageText));
        }
        else {
            final MutableText toggleText = Text.literal(" " + module + " toggled ").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            final MutableText enabledText = Text.literal("on").setStyle(Style.EMPTY.withColor(Formatting.GREEN));
            final MutableText periodText = Text.literal(".").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
            finalMessage = (Text)prefixText.append((Text)toggleText.append((Text)enabledText.append((Text)periodText)));
        }
        ChatUtil.send(finalMessage);
    }
    
    public static void sendSilentMessage(final String message) {
        if (nullCheck()) {
            return;
        }
        Command.mc.inGameHud.getChatHud().addMessage((Text)Text.literal(message));
    }
    
    public static String getCommandPrefix() {
        return Moonlight.commandManager.getPrefix();
    }
    
    public abstract void execute(final String[] p0);
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public String[] getCommands() {
        return this.commands;
    }
}
