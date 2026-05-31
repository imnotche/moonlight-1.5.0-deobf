package me.twerknation28.moonlight.features.modules.combat;

import java.util.List;
import me.twerknation28.moonlight.event.EventListener;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class SuperTotem
extends Module {
    private static final int INVENTORY_SYNC_ID = 0;
    private static final List<Item> HOTBAR_ITEMS = List.of(Items.TOTEM_OF_UNDYING);
    public Setting<Boolean> fastEquip = this.register(new Setting<Boolean>("FastEquip", true));
    public Setting<Boolean> inInventory = this.register(new Setting<Boolean>("InInventory", true));
    private int lastSlot;

    public SuperTotem() {
        super("AutoTotem", "Totally Tubular Totemic Technologies", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.lastSlot = -1;
    }

    @Override
    @EventListener
    public void onUpdate() {
        int itemSlot;
        Item itemToWield;
        if ((SuperTotem.mc.currentScreen == null || this.inInventory.getValue().booleanValue()) && !InventoryManager.isHolding(itemToWield = Items.TOTEM_OF_UNDYING) && (itemSlot = this.getSlotFor(itemToWield)) != -1) {
            if (itemSlot < 9) {
                this.lastSlot = itemSlot;
            }
            if (this.fastEquip.getValue().booleanValue()) {
                SuperTotem.mc.interactionManager.clickSlot(0, itemSlot < 9 ? itemSlot + 36 : itemSlot, 40, SlotActionType.SWAP, (PlayerEntity)SuperTotem.mc.player);
            } else {
                SuperTotem.mc.interactionManager.clickSlot(0, itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                SuperTotem.mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                if (!SuperTotem.mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                    SuperTotem.mc.interactionManager.clickSlot(0, itemSlot < 9 ? itemSlot + 36 : itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                }
            }
        }
    }

    private int getSlotFor(Item item) {
        if (this.lastSlot != -1 && item.equals(SuperTotem.mc.player.getInventory().getStack(this.lastSlot).getItem())) {
            int slot = this.lastSlot;
            this.lastSlot = -1;
            return slot;
        }
        int startSlot = HOTBAR_ITEMS.contains(item) ? 0 : 9;
        for (int slot = 35; slot >= startSlot; --slot) {
            ItemStack itemStack = SuperTotem.mc.player.getInventory().getStack(slot);
            if (itemStack.isEmpty() || !itemStack.getItem().equals(item)) continue;
            return slot;
        }
        return -1;
    }
}
