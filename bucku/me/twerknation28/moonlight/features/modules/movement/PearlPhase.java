package me.twerknation28.moonlight.features.modules.movement;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.manager.NetworkManager;
import me.twerknation28.moonlight.manager.RotationManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class PearlPhase
extends Module {
    public final Setting<Float> throwPitch = this.register(new Setting<Float>("Pitch", Float.valueOf(85.0f), Float.valueOf(0.0f), Float.valueOf(90.0f), Float.valueOf(1.0f)));
    public final Setting<Integer> jumpDelay = this.register(new Setting<Integer>("Jump Delay", 2, 0, 6, 1));
    private static int delayTimer = 0;
    private static float realPitch;

    public PearlPhase() {
        super("Phase", "Phases you into blocks", Category.PLAYER, true, false, true);
    }

    @Override
    public void onEnable() {
        if (PearlPhase.nullCheck()) {
            return;
        }
        if (Notifications.getInstance().isEnabled()) {
            Command.enableMessage(this.getName());
        }
        if (PearlPhase.mc.player.isInSwimmingPose()) {
            Command.sendMessage("Escaping crawl...");
            delayTimer = 0;
            realPitch = -90.0f;
            PearlPhase.mc.player.jump();
        } else {
            delayTimer = this.jumpDelay.getValue();
            realPitch = this.throwPitch.getValue().floatValue();
        }
    }

    @Override
    public void onUpdate() {
        if (delayTimer >= this.jumpDelay.getValue()) {
            int pearlSlot = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = PearlPhase.mc.player.getInventory().getStack(i);
                if (!(stack.getItem() instanceof EnderPearlItem)) continue;
                pearlSlot = i;
                break;
            }
            if (pearlSlot == -1 || PearlPhase.mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL)) {
                this.disable();
                return;
            }
            int prevItem = PearlPhase.mc.player.getInventory().selectedSlot;
            float prevYaw = PearlPhase.mc.player.getYaw();
            float prevPitch = PearlPhase.mc.player.getPitch();
            float[] rotations = RotationManager.getRotationsTo(PearlPhase.mc.player.getEyePos(), new Vec3d(Math.floor(PearlPhase.mc.player.getX()) + 0.5, 0.0, Math.floor(PearlPhase.mc.player.getZ()) + 0.5));
            RotationManager.setPlayerRotations(rotations[0] + 180.0f, realPitch);
            InventoryManager.setSlot(pearlSlot);
            PlayerMoveC2SPacket.Full p = new PlayerMoveC2SPacket.Full(PearlPhase.mc.player.getX(), PearlPhase.mc.player.getY(), PearlPhase.mc.player.getZ(), rotations[0] + 180.0f, realPitch, PearlPhase.mc.player.isOnGround());
            NetworkManager.sendPacket(p);
            PearlPhase.mc.interactionManager.interactItem((PlayerEntity)PearlPhase.mc.player, Hand.MAIN_HAND);
            PearlPhase.mc.player.swingHand(Hand.MAIN_HAND);
            InventoryManager.syncToClient();
            RotationManager.setPlayerRotations(prevYaw, prevPitch);
            this.toggle();
        } else {
            ++delayTimer;
        }
    }
}
