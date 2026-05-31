package me.twerknation28.moonlight.features.modules.combat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class VisualRange
extends Module {
    public Setting<Boolean> compact = this.register(new Setting<Boolean>("Compact", false));
    private final List<AbstractClientPlayerEntity> players = new CopyOnWriteArrayList<AbstractClientPlayerEntity>();

    public VisualRange() {
        super("VisualRange", "Notifies about players getting in or out of render", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.players.clear();
        if (VisualRange.fullNullCheck()) {
            return;
        }
        for (AbstractClientPlayerEntity entity : VisualRange.mc.world.getPlayers()) {
            if (entity == VisualRange.mc.player) continue;
            this.players.add(entity);
        }
    }

    @Override
    public void onUpdate() {
        List currentPlayers = VisualRange.mc.world.getPlayers();
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
