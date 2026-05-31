package me.twerknation28.moonlight.features.modules.combat;

import net.minecraft.item.ItemStack;
import me.twerknation28.moonlight.event.EventListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import me.twerknation28.moonlight.manager.InventoryManager;
import net.minecraft.item.Items;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import net.minecraft.item.Item;
import java.util.List;
import me.twerknation28.moonlight.features.modules.Module;

public class SuperTotem extends Module
{
    private static final int INVENTORY_SYNC_ID = 0;
    private static final List<Item> HOTBAR_ITEMS;
    public Setting<Boolean> fastEquip;
    public Setting<Boolean> inInventory;
    private int lastSlot;
    
    public SuperTotem() {
        super("AutoTotem", "Totally Tubular Totemic Technologies", Category.COMBAT, true, false, false);
        this.fastEquip = this.register(new Setting<Boolean>("FastEquip", true));
        this.inInventory = this.register(new Setting<Boolean>("InInventory", true));
    }
    
    @Override
    public void onEnable() {
        this.lastSlot = -1;
    }
    
    @EventListener
    @Override
    public void onUpdate() {
        if (SuperTotem.mc.currentScreen == null || this.inInventory.getValue()) {
            final Item itemToWield = Items.TOTEM_OF_UNDYING;
            if (!InventoryManager.isHolding(itemToWield)) {
                final int itemSlot = this.getSlotFor(itemToWield);
                if (itemSlot != -1) {
                    if (itemSlot < 9) {
                        this.lastSlot = itemSlot;
                    }
                    if (this.fastEquip.getValue()) {
                        SuperTotem.mc.interactionManager.clickSlot(0, (itemSlot < 9) ? (itemSlot + 36) : itemSlot, 40, SlotActionType.SWAP, (PlayerEntity)SuperTotem.mc.player);
                    }
                    else {
                        SuperTotem.mc.interactionManager.clickSlot(0, (itemSlot < 9) ? (itemSlot + 36) : itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                        SuperTotem.mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                        if (!SuperTotem.mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                            SuperTotem.mc.interactionManager.clickSlot(0, (itemSlot < 9) ? (itemSlot + 36) : itemSlot, 0, SlotActionType.PICKUP, (PlayerEntity)SuperTotem.mc.player);
                        }
                    }
                }
            }
        }
    }
    
    private int getSlotFor(final Item item) {
        if (this.lastSlot != -1 && item.equals(SuperTotem.mc.player.getInventory().getStack(this.lastSlot).getItem())) {
            final int slot = this.lastSlot;
            this.lastSlot = -1;
            return slot;
        }
        for (int startSlot = SuperTotem.HOTBAR_ITEMS.contains(item) ? 0 : 9, slot2 = 35; slot2 >= startSlot; --slot2) {
            final ItemStack itemStack = SuperTotem.mc.player.getInventory().getStack(slot2);
            if (!itemStack.isEmpty() && itemStack.getItem().equals(item)) {
                return slot2;
            }
        }
        return -1;
    }
    
    static {
        HOTBAR_ITEMS = List.of(Items.TOTEM_OF_UNDYING);
    }
}
