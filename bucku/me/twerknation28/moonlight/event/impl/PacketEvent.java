package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.event.Event;
import me.twerknation28.moonlight.event.impl.Cancelable;
import me.twerknation28.moonlight.manager.NetworkManager;
import net.minecraft.network.packet.Packet;

public abstract class PacketEvent
extends Event {
    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @Cancelable
    public static class Send
    extends PacketEvent {
        private final boolean cached;

        public Send(Packet<?> packet) {
            super(packet);
            this.cached = NetworkManager.isCached(packet);
        }

        public boolean isClientPacket() {
            return this.cached;
        }
    }

    public static class Receive
    extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }
}
