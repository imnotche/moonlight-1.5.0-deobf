package me.twerknation28.moonlight.manager;

import java.util.HashSet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.client.network.PendingUpdateManager;
import java.util.Objects;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import me.twerknation28.moonlight.mixin.accessor.AccessorClientWorld;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import java.util.Set;
import me.twerknation28.moonlight.util.Util;

public class NetworkManager implements Util
{
    private static final Set<Packet<?>> PACKET_CACHE;
    
    public static void sendPacket(final Packet<?> p) {
        if (NetworkManager.mc.getNetworkHandler() != null) {
            NetworkManager.PACKET_CACHE.add(p);
            NetworkManager.mc.getNetworkHandler().sendPacket((Packet)p);
        }
    }
    
    public static void sendSequencedPacket(final SequencedPacketCreator o) {
        if (NetworkManager.mc.world != null) {
            final PendingUpdateManager updater = ((AccessorClientWorld)NetworkManager.mc.world).hookGetPendingUpdateManager().incrementSequence();
            try {
                final int i = updater.getSequence();
                final Packet<ServerPlayPacketListener> packet = (Packet<ServerPlayPacketListener>)o.predict(i);
                Objects.requireNonNull(NetworkManager.mc.getNetworkHandler()).sendPacket((Packet)packet);
            }
            catch (final Throwable e) {
                e.printStackTrace();
                if (updater != null) {
                    try {
                        updater.close();
                    }
                    catch (final Throwable e2) {
                        e2.printStackTrace();
                        e.addSuppressed(e2);
                    }
                }
                throw e;
            }
            if (updater != null) {
                updater.close();
            }
        }
    }
    
    public static boolean isCached(final Packet<?> p) {
        return NetworkManager.PACKET_CACHE.contains(p);
    }
    
    static {
        PACKET_CACHE = new HashSet<Packet<?>>();
    }
}
