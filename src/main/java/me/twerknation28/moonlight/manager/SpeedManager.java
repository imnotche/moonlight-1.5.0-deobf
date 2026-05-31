package me.twerknation28.moonlight.manager;

import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import java.util.HashMap;
import me.twerknation28.moonlight.features.Feature;

public class SpeedManager extends Feature
{
    public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0;
    public static boolean didJumpThisTick;
    public static boolean isJumping;
    private final int distancer = 20;
    public double firstJumpSpeed;
    public double lastJumpSpeed;
    public double percentJumpSpeedChanged;
    public double jumpSpeedChanged;
    public boolean didJumpLastTick;
    public long jumpInfoStartTime;
    public boolean wasFirstJump;
    public double speedometerCurrentSpeed;
    public HashMap<PlayerEntity, Double> playerSpeeds;
    
    public SpeedManager() {
        this.firstJumpSpeed = 0.0;
        this.lastJumpSpeed = 0.0;
        this.percentJumpSpeedChanged = 0.0;
        this.jumpSpeedChanged = 0.0;
        this.didJumpLastTick = false;
        this.jumpInfoStartTime = 0L;
        this.wasFirstJump = true;
        this.speedometerCurrentSpeed = 0.0;
        this.playerSpeeds = new HashMap<PlayerEntity, Double>();
    }
    
    public static void setDidJumpThisTick(final boolean val) {
        SpeedManager.didJumpThisTick = val;
    }
    
    public static void setIsJumping(final boolean val) {
        SpeedManager.isJumping = val;
    }
    
    public float lastJumpInfoTimeRemaining() {
        return (System.currentTimeMillis() - this.jumpInfoStartTime) / 1000.0f;
    }
    
    public void updateValues() {
        final double distTraveledLastTickX = SpeedManager.mc.player.getX() - SpeedManager.mc.player.prevX;
        final double distTraveledLastTickZ = SpeedManager.mc.player.getZ() - SpeedManager.mc.player.prevZ;
        this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
        if (SpeedManager.didJumpThisTick && (!SpeedManager.mc.player.isOnGround() || SpeedManager.isJumping)) {
            if (SpeedManager.didJumpThisTick && !this.didJumpLastTick) {
                this.wasFirstJump = (this.lastJumpSpeed == 0.0);
                this.percentJumpSpeedChanged = ((this.speedometerCurrentSpeed != 0.0) ? (this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0) : -1.0);
                this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
                this.jumpInfoStartTime = System.currentTimeMillis();
                this.lastJumpSpeed = this.speedometerCurrentSpeed;
                this.firstJumpSpeed = (this.wasFirstJump ? this.lastJumpSpeed : 0.0);
            }
            this.didJumpLastTick = SpeedManager.didJumpThisTick;
        }
        else {
            this.didJumpLastTick = false;
            this.lastJumpSpeed = 0.0;
        }
        this.updatePlayers();
    }
    
    public void updatePlayers() {
        for (final PlayerEntity player : SpeedManager.mc.world.getPlayers()) {
            if (SpeedManager.mc.player.distanceTo((Entity)player) >= 20.0f) {
                continue;
            }
            final double distTraveledLastTickX = player.getX() - player.prevX;
            final double distTraveledLastTickZ = player.getZ() - player.prevZ;
            final double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
            this.playerSpeeds.put(player, playerSpeed);
        }
    }
    
    public double getPlayerSpeed(final PlayerEntity player) {
        if (this.playerSpeeds.get(player) == null) {
            return 0.0;
        }
        return this.turnIntoKpH(this.playerSpeeds.get(player));
    }
    
    public double turnIntoKpH(final double input) {
        return Math.sqrt(input) * 71.2729367892;
    }
    
    public double getSpeedKpH() {
        double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
        speedometerkphdouble = Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
    
    public double getSpeedMpS() {
        double speedometerMpsdouble = this.turnIntoKpH(this.speedometerCurrentSpeed) / 3.6;
        speedometerMpsdouble = Math.round(10.0 * speedometerMpsdouble) / 10.0;
        return speedometerMpsdouble;
    }
    
    static {
        SpeedManager.didJumpThisTick = false;
        SpeedManager.isJumping = false;
    }
}
