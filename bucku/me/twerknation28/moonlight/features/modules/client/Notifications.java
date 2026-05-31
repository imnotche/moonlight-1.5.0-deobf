package me.twerknation28.moonlight.features.modules.client;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class Notifications
extends Module {
    private static Notifications INSTANCE = new Notifications();
    public final Setting<Boolean> modToggles = this.register(new Setting<Boolean>("Modules", true));
    public final Setting<Boolean> visualRange = this.register(new Setting<Boolean>("VisualRange", true));
    public final Setting<Boolean> compact = this.register(new Setting<Boolean>("Compact", true));
    private final List<AbstractClientPlayerEntity> players = new CopyOnWriteArrayList<AbstractClientPlayerEntity>();

    public Notifications() {
        super("Notifications", "Turning this off gets your discord token logged and your pc nuked", Category.CLIENT, true, false, true);
        this.setInstance();
    }

    @Override
    public void onEnable() {
        if (this.visualRange.getValue().booleanValue()) {
            super.onEnable();
            this.players.clear();
            if (Notifications.fullNullCheck()) {
                return;
            }
            for (AbstractClientPlayerEntity entity : Notifications.mc.world.getPlayers()) {
                if (entity == Notifications.mc.player) continue;
                this.players.add(entity);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.visualRange.getValue().booleanValue()) {
            List currentPlayers = Notifications.mc.world.getPlayers();
            for (AbstractClientPlayerEntity entity : currentPlayers) {
                if (this.players.contains(entity)) continue;
                if (this.compact.getValue().booleanValue()) {
                    Command.visualRangeMessage(entity.getName().getString(), true);
                } else {
                    Command.sendMessage(entity.getName().getString() + " has entered your visual range");
                }
                this.players.add(entity);
            }
            for (AbstractClientPlayerEntity entity : this.players) {
                if (currentPlayers.contains(entity)) continue;
                if (this.compact.getValue().booleanValue()) {
                    Command.visualRangeMessage(entity.getName().getString(), false);
                } else {
                    Command.sendMessage(entity.getName().getString() + " has left your visual range");
                }
                this.players.remove(entity);
            }
        }
    }

    public static Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
