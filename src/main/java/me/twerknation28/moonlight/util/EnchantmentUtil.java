package me.twerknation28.moonlight.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public final class EnchantmentUtil
implements Util {
    private EnchantmentUtil() {
        throw new IllegalArgumentException("\u043f\u043e\u0448\u0435\u043b \u043d\u0430\u0445\u0443\u0439");
    }

    public static int getLevel(RegistryKey<Enchantment> key, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        for (Object2IntMap.Entry enchantment : stack.getEnchantments().getEnchantmentEntries()) {
            if (!((RegistryEntry)enchantment.getKey()).matchesKey(key)) continue;
            return enchantment.getIntValue();
        }
        return 0;
    }

    public static int getLevel(RegistryKey<Enchantment> key, EquipmentSlot slot, LivingEntity entity) {
        return EnchantmentUtil.getLevel(key, entity.getEquippedStack(slot));
    }

    public static int getLevel(RegistryKey<Enchantment> key, EquipmentSlot slot) {
        return EnchantmentUtil.getLevel(key, slot, (LivingEntity)EnchantmentUtil.mc.player);
    }

    public static boolean has(RegistryKey<Enchantment> key, ItemStack stack) {
        return EnchantmentUtil.getLevel(key, stack) > 0;
    }

    public static boolean has(RegistryKey<Enchantment> key, EquipmentSlot slot, LivingEntity entity) {
        return EnchantmentUtil.getLevel(key, slot, entity) > 0;
    }

    public static boolean has(RegistryKey<Enchantment> key, EquipmentSlot slot) {
        return EnchantmentUtil.getLevel(key, slot) > 0;
    }
}
