package me.twerknation28.moonlight.util;

import com.google.common.eventbus.EventBus;
import net.minecraft.client.MinecraftClient;

public interface Util
{
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final EventBus EVENT_BUS = new EventBus();
}
