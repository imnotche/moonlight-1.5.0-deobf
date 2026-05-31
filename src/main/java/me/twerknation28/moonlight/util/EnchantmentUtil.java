package me.twerknation28.moonlight.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import java.util.Iterator;
import net.minecraft.registry.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;

public final class EnchantmentUtil implements Util
{
    private EnchantmentUtil() {
        throw new IllegalArgumentException("\u043f\u043e\u0448\u0435\u043b \u043d\u0430\u0445\u0443\u0439");
    }
    
    public static int getLevel(final RegistryKey<Enchantment> key, final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        for (final Object2IntMap.Entry<RegistryEntry<Enchantment>> enchantment : stack.getEnchantments().getEnchantmentEntries()) {
            if (((RegistryEntry)enchantment.getKey()).matchesKey((RegistryKey)key)) {
                return enchantment.getIntValue();
            }
        }
        return 0;
    }
    
    public static int getLevel(final RegistryKey<Enchantment> key, final EquipmentSlot slot, final LivingEntity entity) {
        return getLevel(key, entity.getEquippedStack(slot));
    }
    
    public static int getLevel(final RegistryKey<Enchantment> key, final EquipmentSlot slot) {
        return getLevel(key, slot, (LivingEntity)EnchantmentUtil.mc.player);
    }
    
    public static boolean has(final RegistryKey<Enchantment> key, final ItemStack stack) {
        return getLevel(key, stack) > 0;
    }
    
    public static boolean has(final RegistryKey<Enchantment> key, final EquipmentSlot slot, final LivingEntity entity) {
        return getLevel(key, slot, entity) > 0;
    }
    
    public static boolean has(final RegistryKey<Enchantment> key, final EquipmentSlot slot) {
        return getLevel(key, slot) > 0;
    }
}
