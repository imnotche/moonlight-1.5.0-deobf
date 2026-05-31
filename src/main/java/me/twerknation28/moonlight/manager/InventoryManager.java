package me.twerknation28.moonlight.manager;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import me.twerknation28.moonlight.event.EventListener;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import me.twerknation28.moonlight.manager.NetworkManager;
import me.twerknation28.moonlight.mixin.accessor.AccessorInteractionManager;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class InventoryManager
implements Util {
    public static int slot;
    public static int previousSlot;
    private static final Set<Packet<?>> PACKET_CACHE;

    @EventListener
    public static void onPacketSend(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof UpdateSelectedSlotC2SPacket) {
            UpdateSelectedSlotC2SPacket packet2 = (UpdateSelectedSlotC2SPacket)packet;
            slot = packet2.getSelectedSlot();
        }
    }

    @EventListener
    public static void onPacketReceive(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket packet2 = (UpdateSelectedSlotS2CPacket)packet;
            slot = packet2.getSlot();
        }
    }

    public static void setSlot(int barSlot) {
        if (InventoryManager.mc.player.getInventory().selectedSlot != barSlot && PlayerInventory.isValidHotbarIndex((int)barSlot)) {
            InventoryManager.setSlotForced(barSlot);
        }
    }

    public static void syncToClient() {
        if (InventoryManager.isDesynced()) {
            InventoryManager.setSlotForced(InventoryManager.mc.player.getInventory().selectedSlot);
        }
    }

    public static void syncLoud() {
        if (InventoryManager.isDesynced()) {
            InventoryManager.setSlotLoud(previousSlot);
        }
    }

    public static boolean isDesynced() {
        return InventoryManager.mc.player.getInventory().selectedSlot != slot;
    }

    public static void setSlotForced(int barSlot) {
        UpdateSelectedSlotC2SPacket p = new UpdateSelectedSlotC2SPacket(barSlot);
        if (mc.getNetworkHandler() != null) {
            PACKET_CACHE.add((Packet<?>)p);
            NetworkManager.sendPacket(p);
        }
    }

    public static void setSlotLoud(int barSlot) {
        if (InventoryManager.mc.player.getInventory().selectedSlot == barSlot && slot == barSlot) {
            return;
        }
        if (previousSlot != barSlot) {
            previousSlot = InventoryManager.mc.player.getInventory().selectedSlot;
        }
        InventoryManager.mc.player.getInventory().selectedSlot = barSlot;
        ((AccessorInteractionManager)InventoryManager.mc.interactionManager).syncSlot();
    }

    public static void swapBack() {
        if (InventoryManager.mc.player.getInventory().selectedSlot == previousSlot && slot == previousSlot || previousSlot == -1) {
            return;
        }
        InventoryManager.mc.player.getInventory().selectedSlot = previousSlot;
        ((AccessorInteractionManager)InventoryManager.mc.interactionManager).syncSlot();
    }

    private static void click(int slot, int button, SlotActionType type) {
        ScreenHandler screenHandler = InventoryManager.mc.player.currentScreenHandler;
        DefaultedList defaultedList = screenHandler.slots;
        int i = defaultedList.size();
        ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (Slot slot1 : defaultedList) {
            list.add(slot1.getStack().copy());
        }
        screenHandler.onSlotClick(slot, button, type, (PlayerEntity)InventoryManager.mc.player);
        Int2ObjectOpenHashMap int2ObjectMap = new Int2ObjectOpenHashMap();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack2;
            ItemStack itemStack = (ItemStack)list.get(j);
            if (ItemStack.areEqual((ItemStack)itemStack, (ItemStack)(itemStack2 = ((Slot)defaultedList.get(j)).getStack()))) continue;
            int2ObjectMap.put(j, (Object)itemStack2.copy());
        }
        InventoryManager.mc.player.networkHandler.sendPacket((Packet)new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), (Int2ObjectMap)int2ObjectMap));
    }

    public static void pickupSlot(int slot) {
        InventoryManager.click(slot, 0, SlotActionType.PICKUP);
    }

    public static int getBestTool(BlockState state) {
        int slot = InventoryManager.getBestToolNoFallback(state);
        return slot != -1 ? slot : InventoryManager.mc.player.getInventory().selectedSlot;
    }

    public static int getBestToolNoFallback(BlockState state) {
        int slot = -1;
        float bestTool = 0.0f;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryManager.mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof ToolItem)) continue;
            float speed = stack.getMiningSpeedMultiplier(state);
            int efficiency = EnchantmentHelper.getLevel((RegistryEntry)((RegistryEntry)InventoryManager.mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get()), (ItemStack)stack);
            if (efficiency > 0) {
                speed += (float)(efficiency * efficiency) + 1.0f;
            }
            if (!(speed > bestTool)) continue;
            bestTool = speed;
            slot = i;
        }
        return slot;
    }

    public static boolean isHolding(Item item) {
        ItemStack itemStack = InventoryManager.mc.player.getMainHandStack();
        if (!itemStack.isEmpty() && itemStack.getItem() == item) {
            return true;
        }
        itemStack = InventoryManager.mc.player.getOffHandStack();
        return !itemStack.isEmpty() && itemStack.getItem() == item;
    }

    static {
        previousSlot = -1;
        PACKET_CACHE = new HashSet();
    }
}
