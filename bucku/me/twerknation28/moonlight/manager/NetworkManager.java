package me.twerknation28.moonlight.manager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.twerknation28.moonlight.mixin.accessor.AccessorClientWorld;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;

public class NetworkManager
implements Util {
    private static final Set<Packet<?>> PACKET_CACHE = new HashSet();

    public static void sendPacket(Packet<?> p) {
        if (mc.getNetworkHandler() != null) {
            PACKET_CACHE.add(p);
            mc.getNetworkHandler().sendPacket(p);
        }
    }

    public static void sendSequencedPacket(SequencedPacketCreator o) {
        if (NetworkManager.mc.world != null) {
            PendingUpdateManager updater = ((AccessorClientWorld)NetworkManager.mc.world).hookGetPendingUpdateManager().incrementSequence();
            try {
                int i = updater.getSequence();
                Packet packet = o.predict(i);
                Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(packet);
            }
            catch (Throwable e) {
                e.printStackTrace();
                if (updater != null) {
                    try {
                        updater.close();
                    }
                    catch (Throwable e1) {
                        e1.printStackTrace();
                        e.addSuppressed(e1);
                    }
                }
                throw e;
            }
            if (updater != null) {
                updater.close();
            }
        }
    }

    public static boolean isCached(Packet<?> p) {
        return PACKET_CACHE.contains(p);
    }
}
