package me.twerknation28.moonlight.features.modules.combat;

import me.twerknation28.moonlight.features.commands.Command;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import me.twerknation28.moonlight.features.api.Category;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import java.util.List;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class VisualRange extends Module
{
    public Setting<Boolean> compact;
    private final List<AbstractClientPlayerEntity> players;
    
    public VisualRange() {
        super("VisualRange", "Notifies about players getting in or out of render", Category.COMBAT, true, false, false);
        this.compact = this.register(new Setting<Boolean>("Compact", false));
        this.players = new CopyOnWriteArrayList<AbstractClientPlayerEntity>();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.players.clear();
        if (fullNullCheck()) {
            return;
        }
        for (final AbstractClientPlayerEntity entity : VisualRange.mc.world.getPlayers()) {
            if (entity == VisualRange.mc.player) {
                continue;
            }
            this.players.add(entity);
        }
    }
    
    @Override
    public void onUpdate() {
        final List<AbstractClientPlayerEntity> currentPlayers = VisualRange.mc.world.getPlayers();
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
