package me.twerknation28.moonlight.manager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.world.ServerWorld;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import me.twerknation28.moonlight.event.impl.TotemPopEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import java.util.HashMap;
import me.twerknation28.moonlight.util.models.Timer;
import java.text.DecimalFormat;
import me.twerknation28.moonlight.features.Feature;

public class ServerManager extends Feature
{
    private final float[] tpsCounts;
    private final DecimalFormat format;
    private final Timer timer;
    private float TPS;
    private long lastUpdate;
    private String serverBrand;
    public static HashMap<String, Integer> popList;
    
    public ServerManager() {
        this.tpsCounts = new float[10];
        this.format = new DecimalFormat("##.00#");
        this.timer = new Timer();
        this.TPS = 20.0f;
        this.lastUpdate = -1L;
        this.serverBrand = "";
    }
    
    public void onPacketReceived() {
        this.timer.reset();
    }
    
    public void onPacketReceive(final PacketEvent.Receive event) {
        final Packet<?> packet2 = event.getPacket();
        if (packet2 instanceof final EntityStatusS2CPacket pac) {
            if (pac.getStatus() == 35) {
                final Entity ent = pac.getEntity((World)ServerManager.mc.world);
                if (!(ent instanceof PlayerEntity)) {
                    return;
                }
                if (ServerManager.popList == null) {
                    ServerManager.popList = new HashMap<String, Integer>();
                }
                if (ServerManager.popList.get(ent.getName().getString()) == null) {
                    ServerManager.popList.put(ent.getName().getString(), 1);
                }
                else if (ServerManager.popList.get(ent.getName().getString()) != null) {
                    ServerManager.popList.put(ent.getName().getString(), ServerManager.popList.get(ent.getName().getString()) + 1);
                }
                final EntityStatusS2CPacket packet = (EntityStatusS2CPacket)event.getPacket();
                final Entity entity = packet.getEntity((World)ServerManager.mc.world);
                if (entity instanceof PlayerEntity && packet.getStatus() == 35) {
                    final PlayerEntity player = (PlayerEntity)entity;
                    ServerManager.EVENT_BUS.post(new TotemPopEvent(player, ServerManager.popList.get(player.getName().getString())));
                }
            }
        }
    }
    
    public List<ServerPlayerEntity> getPlayersInRenderDistance(final ServerPlayerEntity sourcePlayer) {
        final ServerWorld world = (ServerWorld)sourcePlayer.getWorld();
        return new ArrayList<ServerPlayerEntity>(PlayerLookup.tracking((Entity)sourcePlayer));
    }
    
    public boolean isServerNotResponding() {
        return this.timer.passedMs(2000L);
    }
    
    public long serverRespondingTime() {
        return this.timer.getPassedTimeMs();
    }
    
    public void update() {
        for (final PlayerEntity player : ServerManager.mc.world.getPlayers()) {
            if (player.getHealth() <= 0.0f && ServerManager.popList.containsKey(player.getName().getString())) {
                ServerManager.popList.remove(player.getName().getString(), ServerManager.popList.get(player.getName().getString()));
            }
        }
        final long currentTime = System.currentTimeMillis();
        if (this.lastUpdate == -1L) {
            this.lastUpdate = currentTime;
            return;
        }
        final long timeDiff = currentTime - this.lastUpdate;
        float tickTime = timeDiff / 20.0f;
        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }
        float tps;
        if ((tps = 1000.0f / tickTime) > 20.0f) {
            tps = 20.0f;
        }
        System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
        this.tpsCounts[0] = tps;
        double total = 0.0;
        for (final float f : this.tpsCounts) {
            total += f;
        }
        if ((total /= this.tpsCounts.length) > 20.0) {
            total = 20.0;
        }
        this.TPS = Float.parseFloat(this.format.format(total).replace(",", "."));
        this.lastUpdate = currentTime;
    }
    
    @Override
    public void reset() {
        Arrays.fill(this.tpsCounts, 20.0f);
        this.TPS = 20.0f;
    }
    
    public float getTpsFactor() {
        return 20.0f / this.TPS;
    }
    
    public float getTPS() {
        return this.TPS;
    }
    
    public String getServerBrand() {
        return this.serverBrand;
    }
    
    public void setServerBrand(final String brand) {
        this.serverBrand = brand;
    }
    
    public static int getPing() {
        if (fullNullCheck()) {
            return 0;
        }
        try {
            return ServerManager.mc.getNetworkHandler().getPlayerListEntry(ServerManager.mc.player.getGameProfile().getName()).getLatency();
        }
        catch (final Throwable e) {
            return 0;
        }
    }
    
    static {
        ServerManager.popList = new HashMap<String, Integer>();
    }
}
