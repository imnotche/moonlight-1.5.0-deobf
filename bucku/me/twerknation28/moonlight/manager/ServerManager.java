package me.twerknation28.moonlight.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.event.impl.TotemPopEvent;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.util.models.Timer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ServerManager
extends Feature {
    private final float[] tpsCounts = new float[10];
    private final DecimalFormat format = new DecimalFormat("##.00#");
    private final Timer timer = new Timer();
    private float TPS = 20.0f;
    private long lastUpdate = -1L;
    private String serverBrand = "";
    public static HashMap<String, Integer> popList = new HashMap();

    public void onPacketReceived() {
        this.timer.reset();
    }

    public void onPacketReceive(PacketEvent.Receive event) {
        EntityStatusS2CPacket pac;
        Packet<?> packet = event.getPacket();
        if (packet instanceof EntityStatusS2CPacket && (pac = (EntityStatusS2CPacket)packet).getStatus() == 35) {
            Entity ent = pac.getEntity((World)ServerManager.mc.world);
            if (!(ent instanceof PlayerEntity)) {
                return;
            }
            if (popList == null) {
                popList = new HashMap();
            }
            if (popList.get(ent.getName().getString()) == null) {
                popList.put(ent.getName().getString(), 1);
            } else if (popList.get(ent.getName().getString()) != null) {
                popList.put(ent.getName().getString(), popList.get(ent.getName().getString()) + 1);
            }
            EntityStatusS2CPacket packet2 = (EntityStatusS2CPacket)event.getPacket();
            Entity entity = packet2.getEntity((World)ServerManager.mc.world);
            if (entity instanceof PlayerEntity && packet2.getStatus() == 35) {
                PlayerEntity player = (PlayerEntity)entity;
                EVENT_BUS.post(new TotemPopEvent(player, popList.get(player.getName().getString())));
            }
        }
    }

    public List<ServerPlayerEntity> getPlayersInRenderDistance(ServerPlayerEntity sourcePlayer) {
        ServerWorld world = (ServerWorld)sourcePlayer.getWorld();
        return new ArrayList<ServerPlayerEntity>(PlayerLookup.tracking((Entity)sourcePlayer));
    }

    public boolean isServerNotResponding() {
        return this.timer.passedMs(2000L);
    }

    public long serverRespondingTime() {
        return this.timer.getPassedTimeMs();
    }

    public void update() {
        double d;
        float f;
        for (PlayerEntity player : ServerManager.mc.world.getPlayers()) {
            if (!(player.getHealth() <= 0.0f) || !popList.containsKey(player.getName().getString())) continue;
            popList.remove(player.getName().getString(), popList.get(player.getName().getString()));
        }
        long currentTime = System.currentTimeMillis();
        if (this.lastUpdate == -1L) {
            this.lastUpdate = currentTime;
            return;
        }
        long timeDiff = currentTime - this.lastUpdate;
        float tickTime = (float)timeDiff / 20.0f;
        if (tickTime == 0.0f) {
            tickTime = 50.0f;
        }
        float tps = 1000.0f / tickTime;
        if (f > 20.0f) {
            tps = 20.0f;
        }
        System.arraycopy(this.tpsCounts, 0, this.tpsCounts, 1, this.tpsCounts.length - 1);
        this.tpsCounts[0] = tps;
        double total = 0.0;
        for (float f2 : this.tpsCounts) {
            total += (double)f2;
        }
        total /= (double)this.tpsCounts.length;
        if (d > 20.0) {
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

    public void setServerBrand(String brand) {
        this.serverBrand = brand;
    }

    public static int getPing() {
        if (ServerManager.fullNullCheck()) {
            return 0;
        }
        try {
            return mc.getNetworkHandler().getPlayerListEntry(ServerManager.mc.player.getGameProfile().getName()).getLatency();
        }
        catch (Throwable e) {
            return 0;
        }
    }
}
