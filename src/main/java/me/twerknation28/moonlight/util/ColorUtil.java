package me.twerknation28.moonlight.util;

import java.awt.Color;
import me.twerknation28.moonlight.features.modules.client.NewGui;

public class ColorUtil
{
    public static final int BUTTON_ON_OFF;
    public static final int BUTTON_ON_ON;
    
    public static int getColorForGuiEntry(final int type, final boolean hovered, final boolean state) {
        final int BUTTON2_OFF = toARGB(0, 0, 0, 0);
        final int BUTTON2_OFF_HOV = toARGB(150, 150, 150, 50);
        final int BUTTON2_ON = toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
        final int BUTTON2_ON_HOV = toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
        switch (type) {
            case 0: {
                if (hovered) {
                    if (!state) {
                        return ColorUtil.BUTTON_ON_OFF;
                    }
                    return ColorUtil.BUTTON_ON_ON;
                }
                else {
                    if (!state) {
                        return toARGB(179, 179, 179, 255);
                    }
                    return toARGB(255, 255, 255, 255);
                }
            }
            case 1: {
                return toARGB(255, 255, 255, 30);
            }
            case 2: {
                if (!hovered) {
                    return BUTTON2_ON;
                }
                return BUTTON2_ON_HOV;
            }
            case 3: {
                if (!hovered) {
                    if (!state) {
                        return BUTTON2_OFF;
                    }
                    return BUTTON2_ON;
                }
                else {
                    if (!state) {
                        return BUTTON2_OFF_HOV;
                    }
                    return BUTTON2_ON_HOV;
                }
            }
            case 4: {
                if (!state) {
                    return toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 70);
                }
                return toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 128);
            }
            default: {
                throw new IllegalStateException("Invalid type: " + type);
            }
        }
    }
    
    public static int toARGB(final int r, final int g, final int b, final int a) {
        return new Color(r, g, b, a).getRGB();
    }
    
    public static int lerpRGB(final Color color1, final Color color2, final float t) {
        final float[] rgb1 = { (float)color1.getRed(), (float)color1.getGreen(), (float)color1.getBlue() };
        final float[] rgb2 = { (float)color2.getRed(), (float)color2.getGreen(), (float)color2.getBlue() };
        final float red = MathUtil.clamp(rgb1[0] + (rgb2[0] - rgb1[0]) * t, 0.0f, 255.0f);
        final float green = MathUtil.clamp(rgb1[1] + (rgb2[1] - rgb1[1]) * t, 0.0f, 255.0f);
        final float blue = MathUtil.clamp(rgb1[2] + (rgb2[2] - rgb1[2]) * t, 0.0f, 255.0f);
        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f).getRGB();
    }
    
    public static int toRGBA(final int r, final int g, final int b) {
        return toRGBA(r, g, b, 255);
    }
    
    public static int toRGBA(final int r, final int g, final int b, final int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }
    
    public static int toRGBA(final float r, final float g, final float b, final float a) {
        return toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }
    
    public static int toRGBA(final float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }
    
    public static int toRGBA(final double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }
    
    public static int toRGBA(final Color color) {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
    
    static {
        BUTTON_ON_OFF = toARGB(150, 150, 150, 250);
        BUTTON_ON_ON = -1;
    }
}
