package me.twerknation28.moonlight.features.modules.misc;

import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.FriendManager;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class MiddleClick
extends Module {
    private boolean pressed;
    public final Setting<Boolean> friend = this.register(new Setting<Boolean>("Friend", true));
    public final Setting<Boolean> pearl = this.register(new Setting<Boolean>("Pearl", true));
    public final Setting<Boolean> firework = this.register(new Setting<Boolean>("Firework", true));

    public MiddleClick() {
        super("MiddleClick", "Does things when you middle click", Category.PLAYER, true, false, false);
    }

    @Override
    public void onTick() {
        if (GLFW.glfwGetMouseButton((long)mc.getWindow().getHandle(), (int)2) == 1) {
            if (!this.pressed) {
                Entity targetedEntity = MiddleClick.mc.targetedEntity;
                if (MiddleClick.mc.player.isFallFlying() && this.firework.getValue().booleanValue()) {
                    int rocketSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = MiddleClick.mc.player.getInventory().getStack(i);
                        if (!(stack.getItem() instanceof FireworkRocketItem)) continue;
                        rocketSlot = i;
                        break;
                    }
                    InventoryManager.setSlot(rocketSlot);
                    MiddleClick.mc.interactionManager.interactItem((PlayerEntity)MiddleClick.mc.player, Hand.MAIN_HAND);
                    InventoryManager.syncToClient();
                } else if (targetedEntity instanceof PlayerEntity && this.friend.getValue().booleanValue()) {
                    String name = ((PlayerEntity)targetedEntity).getGameProfile().getName();
                    if (FriendManager.isFriend(name)) {
                        Moonlight.friendManager.removeFriend(name);
                        Command.sendMessage(String.valueOf(Formatting.RED) + name + String.valueOf(Formatting.RED) + " has been unfriended.");
                    } else {
                        Moonlight.friendManager.addFriend(name);
                        Command.sendMessage(String.valueOf(Formatting.AQUA) + name + String.valueOf(Formatting.AQUA) + " has been friended.");
                    }
                } else if (this.pearl.getValue().booleanValue()) {
                    int pearlSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        assert (MiddleClick.mc.player != null);
                        ItemStack stack = MiddleClick.mc.player.getInventory().getStack(i);
                        if (!(stack.getItem() instanceof EnderPearlItem)) continue;
                        pearlSlot = i;
                        break;
                    }
                    if (pearlSlot != -1 && !MiddleClick.mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL)) {
                        InventoryManager.setSlot(pearlSlot);
                        MiddleClick.mc.interactionManager.interactItem((PlayerEntity)MiddleClick.mc.player, Hand.MAIN_HAND);
                        InventoryManager.syncToClient();
                    }
                }
                this.pressed = true;
            }
        } else {
            this.pressed = false;
        }
    }
}
