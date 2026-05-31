package me.twerknation28.moonlight.features.modules.player;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger
extends Module {
    public Setting<Boolean> ground = this.register(new Setting<Boolean>("Ground", true));
    public Setting<Boolean> sprint = this.register(new Setting<Boolean>("Sprint", true));

    public AntiHunger() {
        super("AntiHunger", "Conserves food by cancelling certain packets", Category.PLAYER, true, false, false);
    }

    @Override
    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        Packet<?> packet;
        if (event.getPacket() instanceof PlayerMoveC2SPacket && this.ground.getValue().booleanValue()) {
            assert (AntiHunger.mc.player != null);
            AntiHunger.mc.player.setOnGround(false);
        }
        if ((packet = event.getPacket()) instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket pac = (ClientCommandC2SPacket)packet;
            if (this.sprint.getValue().booleanValue() && pac.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                event.cancel();
            }
        }
    }
}
