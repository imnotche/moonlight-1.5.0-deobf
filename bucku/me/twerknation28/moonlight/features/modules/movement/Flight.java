package me.twerknation28.moonlight.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.RotationManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

public class Flight
extends Module {
    private final Setting<Double> baseSpeed = this.register(new Setting<Float>("Base Speed", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.1f)));
    private final Setting<Double> maxSpeed = this.register(new Setting<Float>("Max Speed", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), Float.valueOf(0.1f)));
    private final Setting<Boolean> veloSpeed = this.register(new Setting<Boolean>("Velocity Speed", true));
    private final Setting<AntiKickMode> antiKick = this.register(new Setting<AntiKickMode>("AntiKick", AntiKickMode.PACKET));
    private float speedDelta = 0.0f;

    public Flight() {
        super("Flight", "Zooooom", Category.PLAYER, true, false, true);
    }

    @Override
    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
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
        double speed;
        if (this.isDisabled()) {
            return;
        }
        if (Flight.mc.player.getAbilities().creativeMode) {
            return;
        }
        Flight.mc.player.getAbilities().allowFlying = true;
        if (this.veloSpeed.getValue().booleanValue() && this.isMoving()) {
            this.speedDelta += 0.1f;
        }
        if (!this.isMoving() && this.speedDelta > 0.0f) {
            this.speedDelta = 0.0f;
            Flight.mc.player.setVelocity(Vec3d.ZERO);
        }
        if (this.veloSpeed.getValue().booleanValue()) {
            speed = this.baseSpeed.getValue() * (double)this.speedDelta;
            if (speed >= this.maxSpeed.getValue()) {
                speed = this.maxSpeed.getValue();
            }
        } else {
            speed = this.baseSpeed.getValue();
        }
        Vec3d antiKickVel = this.getAntiKickVec();
        Flight.mc.player.setVelocity(antiKickVel);
        Vec3d forward = new Vec3d(0.0, 0.0, speed).rotateY(-((float)Math.toRadians(Flight.mc.player.getYaw())));
        Vec3d strafe = forward.rotateY((float)Math.toRadians(90.0));
        if (Flight.mc.options.jumpKey.isPressed()) {
            if (speed == 0.0) {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, this.baseSpeed.getValue().doubleValue(), 0.0));
            } else {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, speed, 0.0));
            }
        }
        if (Flight.mc.options.sneakKey.isPressed()) {
            if (speed == 0.0) {
                Flight.mc.player.setVelocity(Flight.mc.player.getVelocity().add(0.0, -this.baseSpeed.getValue().doubleValue(), 0.0));
            } else {
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
                Vec3d position = Flight.mc.player.getPos().add(0.0, -0.069, 0.0);
                BlockPos blockPos = RotationManager.from((Position)position);
                if (Flight.mc.player.age % 7 == 0) {
                    if (Flight.mc.world.getBlockState(blockPos).isReplaceable()) {
                        yield Vec3d.ZERO.add(0.0, -0.069, 0.0);
                    }
                }
                yield Vec3d.ZERO;
            }
            case 2 -> {
                if (Flight.mc.player.age % 40 == 0) {
                    Vec3d position = Flight.mc.player.getPos().add(0.0, 0.15, 0.0);
                    if (Flight.mc.world.getBlockState(RotationManager.from((Position)position)).isReplaceable()) {
                        yield Vec3d.ZERO.add(0.0, 0.15, 0.0);
                    }
                } else if (Flight.mc.player.age % 20 == 0) {
                    Vec3d position = Flight.mc.player.getPos().add(0.0, -0.15, 0.0);
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean isMoving() {
        if (Flight.mc.options.sneakKey.isPressed()) return true;
        if (Flight.mc.options.backKey.isPressed()) return true;
        if (Flight.mc.options.forwardKey.isPressed()) return true;
        if (Flight.mc.options.leftKey.isPressed()) return true;
        if (!Flight.mc.options.rightKey.isPressed()) return false;
        return true;
    }

    public static enum AntiKickMode {
        NONE,
        FALL,
        BOB,
        PACKET;

    }
}
