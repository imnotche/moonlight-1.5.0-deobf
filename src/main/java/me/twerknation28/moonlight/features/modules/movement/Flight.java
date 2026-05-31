package me.twerknation28.moonlight.features.modules.movement;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Position;
import me.twerknation28.moonlight.manager.RotationManager;
import net.minecraft.util.math.Vec3d;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Flight extends Module
{
    private final Setting<Double> baseSpeed;
    private final Setting<Double> maxSpeed;
    private final Setting<Boolean> veloSpeed;
    private final Setting<AntiKickMode> antiKick;
    private float speedDelta;
    
    public Flight() {
        super("Flight", "Zooooom", Category.PLAYER, true, false, true);
        this.baseSpeed = this.register(new Setting<Double>("Base Speed", Double.valueOf(Float.valueOf(1.0f)), Double.valueOf(Float.valueOf(0.0f)), Double.valueOf(Float.valueOf(1.0f)), Double.valueOf(Float.valueOf(0.1f))));
        this.maxSpeed = this.register(new Setting<Double>("Max Speed", Double.valueOf(Float.valueOf(5.0f)), Double.valueOf(Float.valueOf(0.0f)), Double.valueOf(Float.valueOf(10.0f)), Double.valueOf(Float.valueOf(0.1f))));
        this.veloSpeed = this.register(new Setting<Boolean>("Velocity Speed", true));
        this.antiKick = this.register(new Setting<AntiKickMode>("AntiKick", AntiKickMode.PACKET));
        this.speedDelta = 0.0f;
    }
    
    @Subscribe
    @Override
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket) {
            event.cancel();
        }
    }
    
    @Override
    public void onDisable() {
        if (Flight.mc.player.getAbilities().creativeMode) {
            return;
        }
        Flight.mc.player.getAbilities().allowFlying = false;
    }
    
    @Override
    public void onTick() {
        if (this.isDisabled()) {
            return;
        }
        if (Flight.mc.player.getAbilities().creativeMode) {
            return;
        }
        Flight.mc.player.getAbilities().allowFlying = true;
        if (this.veloSpeed.getValue() && this.isMoving()) {
            this.speedDelta += 0.1f;
        }
        if (!this.isMoving() && this.speedDelta > 0.0f) {
            this.speedDelta = 0.0f;
            Flight.mc.player.setVelocity(Vec3d.ZERO);
        }
        double speed;
        if (this.veloSpeed.getValue()) {
            speed = this.baseSpeed.getValue() * this.speedDelta;
            if (speed >= this.maxSpeed.getValue()) {
                speed = this.maxSpeed.getValue();
            }
        }
        else {
            speed = this.baseSpeed.getValue();
        }
        final Vec3d antiKickVel = this.getAntiKickVec();
        Flight.mc.player.setVelocity(antiKickVel);
        final Vec3d forward = new Vec3d(0.0, 0.0, speed).rotateY(-(float)Math.toRadians(Flight.mc.player.getYaw()));
        final Vec3d strafe = forward.rotateY((float)Math.toRadians(90.0));
        if (Flight.mc.options.jumpKey.isPressed()) {
            if (speed == 0.0) {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, (double)this.baseSpeed.getValue(), 0.0));
            }
            else {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, speed, 0.0));
            }
        }
        if (Flight.mc.options.sneakKey.isPressed()) {
            if (speed == 0.0) {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, -this.baseSpeed.getValue(), 0.0));
            }
            else {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, -speed, 0.0));
            }
        }
        if (Flight.mc.options.backKey.isPressed()) {
            Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(-forward.x, 0.0, -forward.z));
        }
        if (Flight.mc.options.forwardKey.isPressed()) {
            Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(forward.x, 0.0, forward.z));
        }
        if (Flight.mc.options.leftKey.isPressed()) {
            Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(strafe.x, 0.0, strafe.z));
        }
        if (Flight.mc.options.rightKey.isPressed()) {
            Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(-strafe.x, 0.0, -strafe.z));
        }
    }
    
    private Vec3d getAntiKickVec() {
        return switch (this.antiKick.getValue().ordinal()) {
            default -> throw new IncompatibleClassChangeError();
            case 0 -> Vec3d.ZERO;
            case 1 -> {
                final Vec3d position = Flight.mc.player.getPos().add(0.0, -0.069, 0.0);
                final BlockPos blockPos = RotationManager.from((Position)position);
                yield (Flight.mc.player.age % 7 == 0 && Flight.mc.world.getBlockState(blockPos).isReplaceable()) ? Vec3d.ZERO.add(0.0, -0.069, 0.0) : Vec3d.ZERO;
            }
            case 2 -> {
                if (Flight.mc.player.age % 40 == 0) {
                    final Vec3d position = Flight.mc.player.getPos().add(0.0, 0.15, 0.0);
                    if (Flight.mc.world.getBlockState(RotationManager.from((Position)position)).isReplaceable()) {
                        yield Vec3d.ZERO.add(0.0, 0.15, 0.0);
                    }
                }
                else if (Flight.mc.player.age % 20 == 0) {
                    final Vec3d position = Flight.mc.player.getPos().add(0.0, -0.15, 0.0);
                    if (Flight.mc.world.getBlockState(RotationManager.from((Position)position)).isReplaceable()) {
                        yield Vec3d.ZERO.add(0.0, -0.15, 0.0);
                    }
                }
                yield Vec3d.ZERO;
            }
            case 3 -> {
                if (Flight.mc.player.age % 20 == 0) {
                    Flight.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Flight.mc.player.getX(), Flight.mc.player.getY() - 0.069, Flight.mc.player.getZ(), false));
                    Flight.mc.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(Flight.mc.player.getX(), Flight.mc.player.getY() + 0.069, Flight.mc.player.getZ(), true));
                }
                yield Vec3d.ZERO;
            }
        };
    }
    
    private boolean isMoving() {
        return Flight.mc.options.sneakKey.isPressed() || Flight.mc.options.backKey.isPressed() || Flight.mc.options.forwardKey.isPressed() || Flight.mc.options.leftKey.isPressed() || Flight.mc.options.rightKey.isPressed();
    }
    
    public enum AntiKickMode
    {
        NONE, 
        FALL, 
        BOB, 
        PACKET;
    }
}
