package me.twerknation28.moonlight.features.modules.movement;

import net.minecraft.network.packet.Packet;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import me.twerknation28.moonlight.manager.NetworkManager;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import me.twerknation28.moonlight.manager.InventoryManager;
import me.twerknation28.moonlight.manager.RotationManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.Items;
import net.minecraft.item.EnderPearlItem;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.client.Notifications;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class PearlPhase extends Module
{
    public final Setting<Float> throwPitch;
    public final Setting<Integer> jumpDelay;
    private static int delayTimer;
    private static float realPitch;
    
    public PearlPhase() {
        super("Phase", "Phases you into blocks", Category.PLAYER, true, false, true);
        this.throwPitch = this.register(new Setting<Float>("Pitch", 85.0f, 0.0f, 90.0f, 1.0f));
        this.jumpDelay = this.register(new Setting<Integer>("Jump Delay", 2, 0, 6, 1));
    }
    
    @Override
    public void onEnable() {
        if (nullCheck()) {
            return;
        }
        if (Notifications.getInstance().isEnabled()) {
            Command.enableMessage(this.getName());
        }
        if (PearlPhase.mc.player.isInSwimmingPose()) {
            Command.sendMessage("Escaping crawl...");
            PearlPhase.delayTimer = 0;
            PearlPhase.realPitch = -90.0f;
            PearlPhase.mc.player.jump();
        }
        else {
            PearlPhase.delayTimer = this.jumpDelay.getValue();
            PearlPhase.realPitch = this.throwPitch.getValue();
        }
    }
    
    @Override
    public void onUpdate() {
        if (PearlPhase.delayTimer >= this.jumpDelay.getValue()) {
            int pearlSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = PearlPhase.mc.player.getInventory().getStack(i);
                if (stack.getItem() instanceof EnderPearlItem) {
                    pearlSlot = i;
                    break;
                }
            }
            if (pearlSlot == -1 || PearlPhase.mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL)) {
                this.disable();
                return;
            }
            final int prevItem = PearlPhase.mc.player.getInventory().selectedSlot;
            final float prevYaw = PearlPhase.mc.player.getYaw();
            final float prevPitch = PearlPhase.mc.player.getPitch();
            final float[] rotations = RotationManager.getRotationsTo(PearlPhase.mc.player.getEyePos(), new Vec3d(Math.floor(PearlPhase.mc.player.getX()) + 0.5, 0.0, Math.floor(PearlPhase.mc.player.getZ()) + 0.5));
            RotationManager.setPlayerRotations(rotations[0] + 180.0f, PearlPhase.realPitch);
            InventoryManager.setSlot(pearlSlot);
            final Packet<?> p = (Packet<?>)new PlayerMoveC2SPacket.Full(PearlPhase.mc.player.getX(), PearlPhase.mc.player.getY(), PearlPhase.mc.player.getZ(), rotations[0] + 180.0f, PearlPhase.realPitch, PearlPhase.mc.player.isOnGround());
            NetworkManager.sendPacket(p);
            PearlPhase.mc.interactionManager.interactItem((PlayerEntity)PearlPhase.mc.player, Hand.MAIN_HAND);
            PearlPhase.mc.player.swingHand(Hand.MAIN_HAND);
            InventoryManager.syncToClient();
            RotationManager.setPlayerRotations(prevYaw, prevPitch);
            this.toggle();
        }
        else {
            ++PearlPhase.delayTimer;
        }
    }
    
    static {
        PearlPhase.delayTimer = 0;
    }
}
