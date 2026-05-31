package me.twerknation28.moonlight.util;

import me.twerknation28.moonlight.features.settings.Bind;

public class KeyboardUtil
{
    public static String getKeyName(final int key) {
        String str = new Bind(key).toString().toUpperCase();
        str = str.replace("KEY.KEYBOARD", "").replace(".", " ");
        return str;
    }
}
