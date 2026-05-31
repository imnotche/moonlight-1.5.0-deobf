package me.twerknation28.moonlight;

import java.util.ArrayList;
import java.util.List;
import me.twerknation28.moonlight.manager.ColorManager;
import me.twerknation28.moonlight.manager.CommandManager;
import me.twerknation28.moonlight.manager.ConfigManager;
import me.twerknation28.moonlight.manager.EventManager;
import me.twerknation28.moonlight.manager.FriendManager;
import me.twerknation28.moonlight.manager.HoleManager;
import me.twerknation28.moonlight.manager.ModuleManager;
import me.twerknation28.moonlight.manager.PositionManager;
import me.twerknation28.moonlight.manager.RotationManager;
import me.twerknation28.moonlight.manager.ServerManager;
import me.twerknation28.moonlight.manager.SpeedManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Moonlight
implements ModInitializer,
ClientModInitializer {
    public static final String NAME = "moonlight";
    public static final String VERSION = "1.5.0-beta";
    public static float TIMER = 1.0f;
    public static final Logger LOGGER = LogManager.getLogger((String)"moonlight");
    public static ServerManager serverManager;
    public static ColorManager colorManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static HoleManager holeManager;
    public static EventManager eventManager;
    public static SpeedManager speedManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static List<Identifier> images;

    public void onInitialize() {
        eventManager = new EventManager();
        serverManager = new ServerManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        friendManager = new FriendManager();
        colorManager = new ColorManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        holeManager = new HoleManager();
    }

    public void onInitializeClient() {
        eventManager.init();
        moduleManager.init();
        configManager = new ConfigManager();
        configManager.load();
        colorManager.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.save()));
    }

    static {
        images = new ArrayList<Identifier>();
    }
}
