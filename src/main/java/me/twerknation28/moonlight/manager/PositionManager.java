package me.twerknation28.moonlight.manager;

import java.util.ArrayList;
import java.util.List;
import me.twerknation28.moonlight.features.Feature;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class PositionManager
extends Feature {
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

    public void setPlayerPosition(double x, double y, double z) {
        PositionManager.mc.player.setPosition(x, y, z);
    }

    public void setPlayerPosition(double x, double y, double z, boolean onground) {
        PositionManager.mc.player.setPosition(x, y, z);
        PositionManager.mc.player.setOnGround(onground);
    }

    public static BlockPos getPlayerPos() {
        assert (PositionManager.mc.player != null);
        return new BlockPos(PositionManager.mc.player.getBlockX(), PositionManager.mc.player.getBlockY(), PositionManager.mc.player.getBlockZ());
    }

    public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos, boolean noLagBack) {
        PositionManager.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
        if (setPos) {
            PositionManager.mc.player.setPosition(x, y, z);
            if (noLagBack) {
                this.updatePosition();
            }
        }
    }

    public static List<BlockPos> getAllInBox(Box box, BlockPos pos) {
        ArrayList<BlockPos> intersections = new ArrayList<BlockPos>();
        int x = (int)Math.floor(box.minX);
        while ((double)x < Math.ceil(box.maxX)) {
            int z = (int)Math.floor(box.minZ);
            while ((double)z < Math.ceil(box.maxZ)) {
                intersections.add(new BlockPos(x, pos.getY(), z));
                ++z;
            }
            ++x;
        }
        return intersections;
    }

    public static List<BlockPos> getAllInBox(Box box) {
        ArrayList<BlockPos> intersections = new ArrayList<BlockPos>();
        int x = (int)Math.floor(box.minX);
        while ((double)x < Math.ceil(box.maxX)) {
            int y = (int)Math.floor(box.minY);
            while ((double)y < Math.ceil(box.maxY)) {
                int z = (int)Math.floor(box.minZ);
                while ((double)z < Math.ceil(box.maxZ)) {
                    intersections.add(new BlockPos(x, y, z));
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        return intersections;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
