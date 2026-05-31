package me.twerknation28.moonlight.features.modules.player;

import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.commands.Command;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ElytraSwap
extends Module {
    private boolean inHotbar;

    public ElytraSwap() {
        super("ElytraSwap", "Swaps Chestplate and Elytra", Category.PLAYER, true, false, true);
    }

    @Override
    public void onEnable() {
        Item chestPiece = !(ElytraSwap.mc.player.getInventory().getArmorStack(2).getItem() instanceof ElytraItem) ? Items.ELYTRA : Items.NETHERITE_CHESTPLATE;
        int elytraSlot = -1;
        for (int i = 0; i <= 44; ++i) {
            assert (ElytraSwap.mc.player != null);
            Item item = ElytraSwap.mc.player.getInventory().getStack(i).getItem();
            if (item != chestPiece && (chestPiece != Items.NETHERITE_CHESTPLATE || item != Items.DIAMOND_CHESTPLATE)) continue;
            this.inHotbar = i < 9;
            elytraSlot = i;
        }
        if (this.inHotbar) {
            InventoryManager.setSlot(elytraSlot);
            ElytraSwap.mc.interactionManager.interactItem((PlayerEntity)ElytraSwap.mc.player, Hand.MAIN_HAND);
            InventoryManager.syncToClient();
            Command.sendMessage("Swapped chestpiece");
        } else {
            ItemStack elytraStack = ElytraSwap.mc.player.getInventory().getArmorStack(2);
            InventoryManager.pickupSlot(elytraSlot);
            boolean rt = !elytraStack.isEmpty();
            InventoryManager.pickupSlot(6);
            if (rt) {
                InventoryManager.pickupSlot(elytraSlot);
            }
            Command.sendMessage("Swapped chestpiece");
        }
        this.toggle();
    }
}
