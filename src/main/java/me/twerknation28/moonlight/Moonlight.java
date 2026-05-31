package me.twerknation28.moonlight;

import java.util.ArrayList;
import net.minecraft.util.Identifier;
import java.util.List;

import me.twerknation28.moonlight.manager.ConfigManager;
import me.twerknation28.moonlight.manager.ModuleManager;
import me.twerknation28.moonlight.manager.FriendManager;
import me.twerknation28.moonlight.manager.CommandManager;
import me.twerknation28.moonlight.manager.SpeedManager;
import me.twerknation28.moonlight.manager.EventManager;
import me.twerknation28.moonlight.manager.HoleManager;
import me.twerknation28.moonlight.manager.PositionManager;
import me.twerknation28.moonlight.manager.RotationManager;
import me.twerknation28.moonlight.manager.ColorManager;
import me.twerknation28.moonlight.manager.ServerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Moonlight implements ModInitializer, ClientModInitializer
{
    public static final String NAME = "moonlight";
    public static final String VERSION = "1.5.0-beta";
    public static float TIMER;
    public static final Logger LOGGER = LogManager.getLogger(NAME);
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
        Moonlight.eventManager = new EventManager();
        Moonlight.serverManager = new ServerManager();
        Moonlight.rotationManager = new RotationManager();
        Moonlight.positionManager = new PositionManager();
        Moonlight.friendManager = new FriendManager();
        Moonlight.colorManager = new ColorManager();
        Moonlight.commandManager = new CommandManager();
        Moonlight.moduleManager = new ModuleManager();
        Moonlight.speedManager = new SpeedManager();
        Moonlight.holeManager = new HoleManager();
    }
    
    public void onInitializeClient() {
        Moonlight.eventManager.init();
        Moonlight.moduleManager.init();
        (Moonlight.configManager = new ConfigManager()).load();
        Moonlight.colorManager.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Moonlight.configManager.save()));
    }
    
    static {
        Moonlight.TIMER = 1.0f;
        Moonlight.images = new ArrayList<Identifier>();
    }
}
