package me.twerknation28.moonlight.features.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class AntiHunger extends Module
{
    public Setting<Boolean> ground;
    public Setting<Boolean> sprint;
    
    public AntiHunger() {
        super("AntiHunger", "Conserves food by cancelling certain packets", Category.PLAYER, true, false, false);
        this.ground = this.register(new Setting<Boolean>("Ground", true));
        this.sprint = this.register(new Setting<Boolean>("Sprint", true));
    }
    
    @Subscribe
    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket && this.ground.getValue()) {
            assert AntiHunger.mc.player != null;
            AntiHunger.mc.player.setOnGround(false);
        }
        final Packet<?> packet = event.getPacket();
        if (packet instanceof final ClientCommandC2SPacket pac) {
            if (this.sprint.getValue() && pac.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                event.cancel();
            }
        }
    }
}
