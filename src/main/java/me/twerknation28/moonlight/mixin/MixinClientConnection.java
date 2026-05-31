package me.twerknation28.moonlight.mixin;

import net.minecraft.network.PacketCallbacks;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import me.twerknation28.moonlight.util.Util;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.network.NetworkSide;
import org.spongepowered.asm.mixin.Shadow;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ClientConnection.class })
public class MixinClientConnection
{
    @Shadow
    private Channel channel;
    @Shadow
    @Final
    private NetworkSide side;
    
    @Inject(method = { "channelRead0" }, at = { @At("HEAD") }, cancellable = true)
    public void channelRead0(final ChannelHandlerContext chc, final Packet<?> packet, final CallbackInfo ci) {
        if (this.channel.isOpen() && packet != null) {
            try {
                final PacketEvent.Receive event = new PacketEvent.Receive(packet);
                Util.EVENT_BUS.post(event);
                if (event.isCancelled()) {
                    ci.cancel();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    @Inject(method = { "sendImmediately" }, at = { @At("HEAD") }, cancellable = true)
    private void sendImmediately(final Packet<?> packet, final PacketCallbacks callbacks, final boolean flush, final CallbackInfo ci) {
        if (this.side != NetworkSide.CLIENTBOUND) {
            return;
        }
        try {
            final PacketEvent.Send event = new PacketEvent.Send(packet);
            Util.EVENT_BUS.post(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
        catch (final Exception ex) {}
    }
}
