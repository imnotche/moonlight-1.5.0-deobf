package me.twerknation28.moonlight.manager;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Box;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import me.twerknation28.moonlight.features.Feature;

public class PositionManager extends Feature
{
    private double x;
    private double y;
    private double z;
    private boolean onground;
    
    public void updatePosition() {
        this.x = PositionManager.mc.player.getX();
        this.y = PositionManager.mc.player.getY();
        this.z = PositionManager.mc.player.getZ();
        this.onground = PositionManager.mc.player.isOnGround();
    }
    
    public void restorePosition() {
        PositionManager.mc.player.setPosition(this.x, this.y, this.z);
        PositionManager.mc.player.setOnGround(this.onground);
    }
    
    public void setPlayerPosition(final double x, final double y, final double z) {
        PositionManager.mc.player.setPosition(x, y, z);
    }
    
    public void setPlayerPosition(final double x, final double y, final double z, final boolean onground) {
        PositionManager.mc.player.setPosition(x, y, z);
        PositionManager.mc.player.setOnGround(onground);
    }
    
    public static BlockPos getPlayerPos() {
        assert PositionManager.mc.player != null;
        return new BlockPos(PositionManager.mc.player.getBlockX(), PositionManager.mc.player.getBlockY(), PositionManager.mc.player.getBlockZ());
    }
    
    public void setPositionPacket(final double x, final double y, final double z, final boolean onGround, final boolean setPos, final boolean noLagBack) {
        PositionManager.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
        if (setPos) {
            PositionManager.mc.player.setPosition(x, y, z);
            if (noLagBack) {
                this.updatePosition();
            }
        }
    }
    
    public static List<BlockPos> getAllInBox(final Box box, final BlockPos pos) {
        final List<BlockPos> intersections = new ArrayList<BlockPos>();
        for (int x = (int)Math.floor(box.minX); x < Math.ceil(box.maxX); ++x) {
            for (int z = (int)Math.floor(box.minZ); z < Math.ceil(box.maxZ); ++z) {
                intersections.add(new BlockPos(x, pos.getY(), z));
            }
        }
        return intersections;
    }
    
    public static List<BlockPos> getAllInBox(final Box box) {
        final List<BlockPos> intersections = new ArrayList<BlockPos>();
        for (int x = (int)Math.floor(box.minX); x < Math.ceil(box.maxX); ++x) {
            for (int y = (int)Math.floor(box.minY); y < Math.ceil(box.maxY); ++y) {
                for (int z = (int)Math.floor(box.minZ); z < Math.ceil(box.maxZ); ++z) {
                    intersections.add(new BlockPos(x, y, z));
                }
            }
        }
        return intersections;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
}
