package me.twerknation28.moonlight.features.modules.player;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import me.twerknation28.moonlight.features.commands.Command;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.item.Items;
import net.minecraft.item.ElytraItem;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;

public class ElytraSwap extends Module
{
    private boolean inHotbar;
    
    public ElytraSwap() {
        super("ElytraSwap", "Swaps Chestplate and Elytra", Category.PLAYER, true, false, true);
    }
    
    @Override
    public void onEnable() {
        Item chestPiece;
        if (!(ElytraSwap.mc.player.getInventory().getArmorStack(2).getItem() instanceof ElytraItem)) {
            chestPiece = Items.ELYTRA;
        }
        else {
            chestPiece = Items.NETHERITE_CHESTPLATE;
        }
        int elytraSlot = -1;
        for (int i = 0; i <= 44; ++i) {
            assert ElytraSwap.mc.player != null;
            final Item item = ElytraSwap.mc.player.getInventory().getStack(i).getItem();
            if (item == chestPiece || (chestPiece == Items.NETHERITE_CHESTPLATE && item == Items.DIAMOND_CHESTPLATE)) {
                if (i < 9) {
                    this.inHotbar = true;
                }
                else {
                    this.inHotbar = false;
                }
                elytraSlot = i;
            }
        }
        if (this.inHotbar) {
            InventoryManager.setSlot(elytraSlot);
            ElytraSwap.mc.interactionManager.interactItem((PlayerEntity)ElytraSwap.mc.player, Hand.MAIN_HAND);
            InventoryManager.syncToClient();
            Command.sendMessage("Swapped chestpiece");
        }
        else {
            final ItemStack elytraStack = ElytraSwap.mc.player.getInventory().getArmorStack(2);
            InventoryManager.pickupSlot(elytraSlot);
            final boolean rt = !elytraStack.isEmpty();
            InventoryManager.pickupSlot(6);
            if (rt) {
                InventoryManager.pickupSlot(elytraSlot);
            }
            Command.sendMessage("Swapped chestpiece");
        }
        this.toggle();
    }
}
