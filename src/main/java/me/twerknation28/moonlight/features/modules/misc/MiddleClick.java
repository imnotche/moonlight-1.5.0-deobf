package me.twerknation28.moonlight.features.modules.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.item.EnderPearlItem;
import me.twerknation28.moonlight.features.commands.Command;
import net.minecraft.util.Formatting;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.manager.FriendManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.item.FireworkRocketItem;
import org.lwjgl.glfw.GLFW;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class MiddleClick extends Module
{
    private boolean pressed;
    public final Setting<Boolean> friend;
    public final Setting<Boolean> pearl;
    public final Setting<Boolean> firework;
    
    public MiddleClick() {
        super("MiddleClick", "Does things when you middle click", Category.PLAYER, true, false, false);
        this.friend = this.register(new Setting<Boolean>("Friend", true));
        this.pearl = this.register(new Setting<Boolean>("Pearl", true));
        this.firework = this.register(new Setting<Boolean>("Firework", true));
    }
    
    @Override
    public void onTick() {
        if (GLFW.glfwGetMouseButton(MiddleClick.mc.getWindow().getHandle(), 2) == 1) {
            if (!this.pressed) {
                final Entity targetedEntity = MiddleClick.mc.targetedEntity;
                if (MiddleClick.mc.player.isFallFlying() && this.firework.getValue()) {
                    int rocketSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        final ItemStack stack = MiddleClick.mc.player.getInventory().getStack(i);
                        if (stack.getItem() instanceof FireworkRocketItem) {
                            rocketSlot = i;
                            break;
                        }
                    }
                    InventoryManager.setSlot(rocketSlot);
                    MiddleClick.mc.interactionManager.interactItem((PlayerEntity)MiddleClick.mc.player, Hand.MAIN_HAND);
                    InventoryManager.syncToClient();
                }
                else if (targetedEntity instanceof PlayerEntity && this.friend.getValue()) {
                    final String name = ((PlayerEntity)targetedEntity).getGameProfile().getName();
                    if (FriendManager.isFriend(name)) {
                        Moonlight.friendManager.removeFriend(name);
                        Command.sendMessage(String.valueOf(Formatting.RED) + name + String.valueOf(Formatting.RED) + " has been unfriended.");
                    }
                    else {
                        Moonlight.friendManager.addFriend(name);
                        Command.sendMessage(String.valueOf(Formatting.AQUA) + name + String.valueOf(Formatting.AQUA) + " has been friended.");
                    }
                }
                else if (this.pearl.getValue()) {
                    int pearlSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        assert MiddleClick.mc.player != null;
                        final ItemStack stack = MiddleClick.mc.player.getInventory().getStack(i);
                        if (stack.getItem() instanceof EnderPearlItem) {
                            pearlSlot = i;
                            break;
                        }
                    }
                    if (pearlSlot != -1 && !MiddleClick.mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL)) {
                        InventoryManager.setSlot(pearlSlot);
                        MiddleClick.mc.interactionManager.interactItem((PlayerEntity)MiddleClick.mc.player, Hand.MAIN_HAND);
                        InventoryManager.syncToClient();
                    }
                }
                this.pressed = true;
            }
        }
        else {
            this.pressed = false;
        }
    }
}
