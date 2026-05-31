package me.twerknation28.moonlight.manager;

import java.util.HashSet;
import net.minecraft.item.Item;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.item.ToolItem;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import java.util.ArrayList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.screen.ScreenHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.item.ItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import com.google.common.collect.Lists;
import net.minecraft.screen.slot.SlotActionType;
import me.twerknation28.moonlight.mixin.accessor.AccessorInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import me.twerknation28.moonlight.event.EventListener;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import me.twerknation28.moonlight.event.impl.PacketEvent;
import net.minecraft.network.packet.Packet;
import java.util.Set;
import me.twerknation28.moonlight.util.Util;

public class InventoryManager implements Util
{
    public static int slot;
    public static int previousSlot;
    private static final Set<Packet<?>> PACKET_CACHE;
    
    @EventListener
    public static void onPacketSend(final PacketEvent.Send event) {
        final Packet<?> packet2 = event.getPacket();
        if (packet2 instanceof final UpdateSelectedSlotC2SPacket packet) {
            InventoryManager.slot = packet.getSelectedSlot();
        }
    }
    
    @EventListener
    public static void onPacketReceive(final PacketEvent.Receive event) {
        final Packet<?> packet2 = event.getPacket();
        if (packet2 instanceof final UpdateSelectedSlotS2CPacket packet) {
            InventoryManager.slot = packet.getSlot();
        }
    }
    
    public static void setSlot(final int barSlot) {
        if (InventoryManager.mc.player.getInventory().selectedSlot != barSlot && PlayerInventory.isValidHotbarIndex(barSlot)) {
            setSlotForced(barSlot);
        }
    }
    
    public static void syncToClient() {
        if (isDesynced()) {
            setSlotForced(InventoryManager.mc.player.getInventory().selectedSlot);
        }
    }
    
    public static void syncLoud() {
        if (isDesynced()) {
            setSlotLoud(InventoryManager.previousSlot);
        }
    }
    
    public static boolean isDesynced() {
        return InventoryManager.mc.player.getInventory().selectedSlot != InventoryManager.slot;
    }
    
    public static void setSlotForced(final int barSlot) {
        final Packet<?> p = (Packet<?>)new UpdateSelectedSlotC2SPacket(barSlot);
        if (InventoryManager.mc.getNetworkHandler() != null) {
            InventoryManager.PACKET_CACHE.add(p);
            NetworkManager.sendPacket(p);
        }
    }
    
    public static void setSlotLoud(final int barSlot) {
        if (InventoryManager.mc.player.getInventory().selectedSlot == barSlot && InventoryManager.slot == barSlot) {
            return;
        }
        if (InventoryManager.previousSlot != barSlot) {
            InventoryManager.previousSlot = InventoryManager.mc.player.getInventory().selectedSlot;
        }
        InventoryManager.mc.player.getInventory().selectedSlot = barSlot;
        ((AccessorInteractionManager)InventoryManager.mc.interactionManager).syncSlot();
    }
    
    public static void swapBack() {
        if ((InventoryManager.mc.player.getInventory().selectedSlot == InventoryManager.previousSlot && InventoryManager.slot == InventoryManager.previousSlot) || InventoryManager.previousSlot == -1) {
            return;
        }
        InventoryManager.mc.player.getInventory().selectedSlot = InventoryManager.previousSlot;
        ((AccessorInteractionManager)InventoryManager.mc.interactionManager).syncSlot();
    }
    
    private static void click(final int slot, final int button, final SlotActionType type) {
        final ScreenHandler screenHandler = InventoryManager.mc.player.currentScreenHandler;
        final DefaultedList<Slot> defaultedList = (DefaultedList<Slot>)screenHandler.slots;
        final int i = defaultedList.size();
        final ArrayList<ItemStack> list = Lists.newArrayListWithCapacity(i);
        for (final Slot slot2 : defaultedList) {
            list.add(slot2.getStack().copy());
        }
        screenHandler.onSlotClick(slot, button, type, (PlayerEntity)InventoryManager.mc.player);
        final Int2ObjectOpenHashMap<ItemStack> int2ObjectMap = (Int2ObjectOpenHashMap<ItemStack>)new Int2ObjectOpenHashMap();
        for (int j = 0; j < i; ++j) {
            final ItemStack itemStack = list.get(j);
            final ItemStack itemStack2;
            if (!ItemStack.areEqual(itemStack, itemStack2 = ((Slot)defaultedList.get(j)).getStack())) {
                int2ObjectMap.put(j, (ItemStack) itemStack2.copy());
            }
        }
        InventoryManager.mc.player.networkHandler.sendPacket((Packet)new ClickSlotC2SPacket(screenHandler.syncId, screenHandler.getRevision(), slot, button, type, screenHandler.getCursorStack().copy(), (Int2ObjectMap)int2ObjectMap));
    }
    
    public static void pickupSlot(final int slot) {
        click(slot, 0, SlotActionType.PICKUP);
    }
    
    public static int getBestTool(final BlockState state) {
        final int slot = getBestToolNoFallback(state);
        return (slot != -1) ? slot : InventoryManager.mc.player.getInventory().selectedSlot;
    }
    
    public static int getBestToolNoFallback(final BlockState state) {
        int slot = -1;
        float bestTool = 0.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = InventoryManager.mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ToolItem) {
                float speed = stack.getMiningSpeedMultiplier(state);
                final int efficiency = EnchantmentHelper.getLevel((RegistryEntry)InventoryManager.mc.world.getRegistryManager().get(Enchantments.EFFICIENCY.getRegistryRef()).getEntry(Enchantments.EFFICIENCY).get(), stack);
                if (efficiency > 0) {
                    speed += efficiency * efficiency + 1.0f;
                }
                if (speed > bestTool) {
                    bestTool = speed;
                    slot = i;
                }
            }
        }
        return slot;
    }
    
    public static boolean isHolding(final Item item) {
        ItemStack itemStack = InventoryManager.mc.player.getMainHandStack();
        if (!itemStack.isEmpty() && itemStack.getItem() == item) {
            return true;
        }
        itemStack = InventoryManager.mc.player.getOffHandStack();
        return !itemStack.isEmpty() && itemStack.getItem() == item;
    }
    
    static {
        InventoryManager.previousSlot = -1;
        PACKET_CACHE = new HashSet<Packet<?>>();
    }
}
