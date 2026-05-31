package me.twerknation28.moonlight.event.impl;

import me.twerknation28.moonlight.manager.NetworkManager;
import net.minecraft.network.packet.Packet;
import me.twerknation28.moonlight.event.Event;

public abstract class PacketEvent extends Event
{
    private final Packet<?> packet;
    
    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }
    
    public Packet<?> getPacket() {
        return this.packet;
    }
    
    public static class Receive extends PacketEvent
    {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }
    
    @Cancelable
    public static class Send extends PacketEvent
    {
        private final boolean cached;
        
        public Send(final Packet<?> packet) {
            super(packet);
            this.cached = NetworkManager.isCached(packet);
        }
        
        public boolean isClientPacket() {
            return this.cached;
        }
    }
}
