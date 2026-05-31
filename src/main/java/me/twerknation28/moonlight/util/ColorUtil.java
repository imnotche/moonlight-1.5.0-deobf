package me.twerknation28.moonlight.util;

import java.awt.Color;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.util.MathUtil;

public class ColorUtil {
    public static final int BUTTON_ON_OFF = ColorUtil.toARGB(150, 150, 150, 250);
    public static final int BUTTON_ON_ON = -1;

    public static int getColorForGuiEntry(int type, boolean hovered, boolean state) {
        int BUTTON2_OFF = ColorUtil.toARGB(0, 0, 0, 0);
        int BUTTON2_OFF_HOV = ColorUtil.toARGB(150, 150, 150, 50);
        int BUTTON2_ON = ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
        int BUTTON2_ON_HOV = ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
        switch (type) {
            case 0: {
                if (hovered) {
                    if (!state) {
                        return BUTTON_ON_OFF;
                    }
                    return BUTTON_ON_ON;
                }
                if (!state) {
                    return ColorUtil.toARGB(179, 179, 179, 255);
                }
                return ColorUtil.toARGB(255, 255, 255, 255);
            }
            case 1: {
                return ColorUtil.toARGB(255, 255, 255, 30);
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
                if (!state) {
                    return BUTTON2_OFF_HOV;
                }
                return BUTTON2_ON_HOV;
            }
            case 4: {
                if (!state) {
                    return ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 70);
                }
                return ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 128);
            }
        }
        throw new IllegalStateException("Invalid type: " + type);
    }

    public static int toARGB(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int lerpRGB(Color color1, Color color2, float t) {
        float[] rgb1 = new float[]{color1.getRed(), color1.getGreen(), color1.getBlue()};
        float[] rgb2 = new float[]{color2.getRed(), color2.getGreen(), color2.getBlue()};
        float red = MathUtil.clamp(rgb1[0] + (rgb2[0] - rgb1[0]) * t, 0.0f, 255.0f);
        float green = MathUtil.clamp(rgb1[1] + (rgb2[1] - rgb1[1]) * t, 0.0f, 255.0f);
        float blue = MathUtil.clamp(rgb1[2] + (rgb2[2] - rgb1[2]) * t, 0.0f, 255.0f);
        return new Color(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f).getRGB();
    }

    public static int toRGBA(int r, int g, int b) {
        return ColorUtil.toRGBA(r, g, b, 255);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return ColorUtil.toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }

    public static int toRGBA(float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return ColorUtil.toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static int toRGBA(Color color) {
        return ColorUtil.toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
