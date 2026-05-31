package me.twerknation28.moonlight.features.modules.client;

import me.twerknation28.moonlight.features.commands.Command;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import me.twerknation28.moonlight.features.api.Category;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import java.util.List;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Notifications extends Module
{
    private static Notifications INSTANCE;
    public final Setting<Boolean> modToggles;
    public final Setting<Boolean> visualRange;
    public final Setting<Boolean> compact;
    private final List<AbstractClientPlayerEntity> players;
    
    public Notifications() {
        super("Notifications", "Turning this off gets your discord token logged and your pc nuked", Category.CLIENT, true, false, true);
        this.modToggles = this.register(new Setting<Boolean>("Modules", true));
        this.visualRange = this.register(new Setting<Boolean>("VisualRange", true));
        this.compact = this.register(new Setting<Boolean>("Compact", true));
        this.players = new CopyOnWriteArrayList<AbstractClientPlayerEntity>();
        this.setInstance();
    }
    
    @Override
    public void onEnable() {
        if (this.visualRange.getValue()) {
            super.onEnable();
            this.players.clear();
            if (fullNullCheck()) {
                return;
            }
            for (final AbstractClientPlayerEntity entity : Notifications.mc.world.getPlayers()) {
                if (entity == Notifications.mc.player) {
                    continue;
                }
                this.players.add(entity);
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.visualRange.getValue()) {
            final List<AbstractClientPlayerEntity> currentPlayers = Notifications.mc.world.getPlayers();
            for (AbstractClientPlayerEntity entity : currentPlayers) {
                if (!this.players.contains(entity)) {
                    if (this.compact.getValue()) {
                        Command.visualRangeMessage(entity.getName().getString(), true);
                    }
                    else {
                        Command.sendMessage(entity.getName().getString() + " has entered your visual range");
                    }
                    this.players.add(entity);
                }
            }
            for (AbstractClientPlayerEntity entity : this.players) {
                if (!currentPlayers.contains(entity)) {
                    if (this.compact.getValue()) {
                        Command.visualRangeMessage(entity.getName().getString(), false);
                    }
                    else {
                        Command.sendMessage(entity.getName().getString() + " has left your visual range");
                    }
                    this.players.remove(entity);
                }
            }
        }
    }
    
    public static Notifications getInstance() {
        if (Notifications.INSTANCE == null) {
            Notifications.INSTANCE = new Notifications();
        }
        return Notifications.INSTANCE;
    }
    
    private void setInstance() {
        Notifications.INSTANCE = this;
    }
    
    static {
        Notifications.INSTANCE = new Notifications();
    }
}
