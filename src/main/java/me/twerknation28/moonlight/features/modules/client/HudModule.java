package me.twerknation28.moonlight.features.modules.client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.twerknation28.moonlight.Moonlight;
import me.twerknation28.moonlight.event.impl.Render2DEvent;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.modules.client.NewGui;
import me.twerknation28.moonlight.features.modules.misc.NameProtect;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.ServerManager;
import me.twerknation28.moonlight.util.ColorUtil;
import me.twerknation28.moonlight.util.MathUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;

public class HudModule
extends Module {
    public Setting<colorMode> colorModeSetting = this.register(new Setting<colorMode>("Color", colorMode.GLOBAL));
    public Setting<String> firstGradientColor = this.register(new Setting<String>("FirstGradientColor", "#FF0000", v -> this.colorModeSetting.getValue() == colorMode.CYCLE));
    public Setting<String> secondGradientColor = this.register(new Setting<String>("SecondGradientColor", "#0000FF", v -> this.colorModeSetting.getValue() == colorMode.CYCLE));
    public Setting<Boolean> watermark = this.register(new Setting<Boolean>("Watermark", true));
    public Setting<Boolean> gaymark = this.register(new Setting<Boolean>("GayMark", false));
    public Setting<Boolean> indicators = this.register(new Setting<Boolean>("Indicators", Boolean.valueOf(false), v -> this.gaymark.getValue()));
    public Setting<Boolean> textRadar = this.register(new Setting<Boolean>("TextRadar", true));
    public Setting<Integer> yOffset = this.register(new Setting<Integer>("Offset", 0, 0, 45, 1));
    public Setting<Boolean> welcomer = this.register(new Setting<Boolean>("Welcomer", false));
    public Setting<welcomerMode> mode = this.register(new Setting<Object>("Mode", (Object)welcomerMode.TIME, v -> this.welcomer.getValue()));
    public Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", true));
    public Setting<Boolean> info = this.register(new Setting<Boolean>("Info", true));
    public Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ArrayList", true));
    public Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", Boolean.valueOf(true), v -> this.info.getValue()));
    public Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", Boolean.valueOf(true), v -> this.info.getValue()));
    public Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", Boolean.valueOf(true), v -> this.info.getValue()));
    float gradientCount = 0.0f;
    boolean gradientDirection = true;
    int audioTimer = 20;
    int REFRESH_INTERVAL = 20;
    String songString = "";

    public HudModule() {
        super("HUD", "swag", Category.CLIENT, true, false, false);
    }

    @Override
    public void onTick() {
        if (this.welcomer.getValue().booleanValue() && this.mode.getValue() == welcomerMode.LINUXAUDIO) {
            ++this.audioTimer;
            if (this.audioTimer >= this.REFRESH_INTERVAL) {
                this.songString = this.getCurrentSong();
                this.audioTimer = 0;
            }
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int globalColor;
        if (this.colorModeSetting.getValue() == colorMode.GLOBAL) {
            globalColor = ColorUtil.toARGB(NewGui.getInstance().red.getValue(), NewGui.getInstance().green.getValue(), NewGui.getInstance().blue.getValue(), 255);
        } else {
            Color color1 = Color.decode(this.firstGradientColor.getValue());
            Color color2 = Color.decode(this.secondGradientColor.getValue());
            globalColor = ColorUtil.lerpRGB(color1, color2, this.gradientCount);
            if (this.gradientDirection) {
                this.gradientCount += 0.01f;
                this.gradientCount = MathUtil.clamp(this.gradientCount, 0.0f, 1.0f);
                if (this.gradientCount == 1.0f) {
                    this.gradientDirection = false;
                }
            } else {
                this.gradientCount -= 0.01f;
                this.gradientCount = MathUtil.clamp(this.gradientCount, 0.0f, 1.0f);
                if (this.gradientCount == 0.0f) {
                    this.gradientDirection = true;
                }
            }
        }
        if (this.watermark.getValue().booleanValue()) {
            event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "moonlight", 2, 2 + this.yOffset.getValue(), globalColor);
            int firstWordWidth = HudModule.mc.textRenderer.getWidth("moonlight");
            event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " 1.5.0-beta", 2 + firstWordWidth, 2 + this.yOffset.getValue(), 0xFFFFFF);
        }
        if (this.gaymark.getValue().booleanValue()) {
            event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "gayretard.club", 2, 150, globalColor);
            if (this.indicators.getValue().booleanValue()) {
                boolean isInRange = false;
                boolean isInPlaceRange = false;
                boolean bl = false;
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "HTR", 2, 160, 14224393);
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "PLR", 2, 169, 14224393);
                int totems = 0;
                for (int i = 0; i <= 45; ++i) {
                    if (HudModule.mc.player.getInventory().getStack(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
                    ++totems;
                }
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, Integer.toString(totems), 2, 178, totems != 0 ? 8453123 : 14224393);
                int ping = ServerManager.getPing();
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "PING " + ping, 2, 187, ping <= 100 ? 8453123 : 14224393);
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "LBY", 2, 196, 14224393);
            }
        }
        if (this.coords.getValue().booleanValue()) {
            event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "XYZ:", 2, mc.getWindow().getScaledHeight() - 9, globalColor);
            int xyzLength = HudModule.mc.textRenderer.getWidth("XYZ:");
            event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " " + HudModule.mc.player.getBlockX() + ", " + HudModule.mc.player.getBlockY() + ", " + HudModule.mc.player.getBlockZ(), 2 + xyzLength, mc.getWindow().getScaledHeight() - 9, 0xFFFFFF);
        }
        if (this.welcomer.getValue().booleanValue()) {
            switch (this.mode.getValue().ordinal()) {
                case 1: {
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, this.songString, (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)HudModule.mc.textRenderer.getWidth(this.songString) / 2.0f + 2.0f), 2, globalColor);
                    break;
                }
                case 3: {
                    int welcomerLength = HudModule.mc.textRenderer.getWidth("welcome, " + this.getSpoofedName() + " :^)");
                    int welcomeX = (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f);
                    int n = welcomeX + HudModule.mc.textRenderer.getWidth("welcome, ");
                    int faceX = welcomeX + HudModule.mc.textRenderer.getWidth("welcome, " + this.getSpoofedName());
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "welcome, ", (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f), 2, globalColor);
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, this.getSpoofedName(), n, 2, 0xFFFFFF);
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " :^)", faceX, 2, globalColor);
                    break;
                }
                case 0: {
                    int welcomerLength = HudModule.mc.textRenderer.getWidth(MathUtil.getTimeOfDay() + ", " + this.getSpoofedName());
                    int welcomeX = (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f);
                    int n = welcomeX + HudModule.mc.textRenderer.getWidth(MathUtil.getTimeOfDay() + ", ");
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, MathUtil.getTimeOfDay() + ", ", (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f), 2, globalColor);
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, this.getSpoofedName(), n, 2, 0xFFFFFF);
                    break;
                }
                case 2: {
                    int welcomerLength = HudModule.mc.textRenderer.getWidth("welcome to moonlight, " + this.getSpoofedName());
                    int welcomeX = (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f);
                    int n = welcomeX + HudModule.mc.textRenderer.getWidth("welcome to moonlight, ");
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "welcome to moonlight, ", (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f), 2, globalColor);
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, this.getSpoofedName(), n, 2, 0xFFFFFF);
                    break;
                }
                case 4: {
                    String welcomerText = "Welcome " + this.getSpoofedName() + " :^)";
                    int welcomerLength = HudModule.mc.textRenderer.getWidth("Welcome " + this.getSpoofedName() + " :^)");
                    int n = (int)((float)mc.getWindow().getScaledWidth() / 2.0f - (float)welcomerLength / 2.0f + 2.0f);
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, welcomerText, n, 2, globalColor);
                    break;
                }
            }
        }
        if (this.arrayList.getValue().booleanValue()) {
            int moduleOffset = 0;
            for (int k = 0; k < Moonlight.moduleManager.sortedModules.size(); ++k) {
                if (!Moonlight.moduleManager.sortedModules.get((int)k).drawn.getValue().booleanValue()) continue;
                String string = Moonlight.moduleManager.sortedModules.get(k).getName();
                int modLength = HudModule.mc.textRenderer.getWidth(string);
                int modX = mc.getWindow().getScaledWidth() - modLength - 2;
                int modY = 2 + moduleOffset;
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, string, modX, modY, globalColor);
                moduleOffset += 9;
            }
        }
        if (this.textRadar.getValue().booleanValue()) {
            int count = 1;
            for (Entity entity : HudModule.mc.world.getPlayers().stream().filter(e -> e != HudModule.mc.player).sorted(Comparator.comparing(arg_0 -> ((ClientPlayerEntity)HudModule.mc.player).method_5739(arg_0))).toList()) {
                int dist = Math.round(HudModule.mc.player.distanceTo(entity));
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, entity.getDisplayName().getString(), 2, 11 + count * 9 + this.yOffset.getValue(), globalColor);
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " " + dist + "m", 2 + HudModule.mc.textRenderer.getWidth(entity.getDisplayName().getString()), 11 + count * 9 + this.yOffset.getValue(), 0xFFFFFF);
                ++count;
            }
        }
        if (this.info.getValue().booleanValue()) {
            int yOffset = 0;
            if (this.potions.getValue().booleanValue()) {
                for (StatusEffectInstance statusEffectInstance : HudModule.mc.player.getStatusEffects()) {
                    StatusEffect potion = (StatusEffect)statusEffectInstance.getEffectType().value();
                    int potionLength = HudModule.mc.textRenderer.getWidth(potion.getName().getString() + " " + HudModule.getDuration(statusEffectInstance));
                    int potionX = mc.getWindow().getScaledWidth() - potionLength - 3;
                    int potionY = mc.getWindow().getScaledHeight() - (yOffset += 9);
                    int potionIntX = potionX + HudModule.mc.textRenderer.getWidth(potion.getName().getString() + ":");
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, potion.getName().getString(), potionX, potionY, potion.getColor());
                    event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " " + HudModule.getDuration(statusEffectInstance), potionIntX, potionY, 0xFFFFFF);
                }
            }
            if (this.fps.getValue().booleanValue()) {
                int fpsLength = HudModule.mc.textRenderer.getWidth("FPS: " + HudModule.mc.fpsDebugString.split(" ", 2)[0]);
                int n = mc.getWindow().getScaledWidth() - fpsLength - 2;
                int intX = n + HudModule.mc.textRenderer.getWidth("FPS:");
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "FPS:", n, mc.getWindow().getScaledHeight() - (yOffset += 9), globalColor);
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " " + HudModule.mc.fpsDebugString.split(" ", 2)[0], intX, mc.getWindow().getScaledHeight() - yOffset, 0xFFFFFF);
            }
            if (this.ping.getValue().booleanValue()) {
                int pingLength = HudModule.mc.textRenderer.getWidth("Ping: " + ServerManager.getPing());
                int n = mc.getWindow().getScaledWidth() - pingLength - 2;
                int pingY = mc.getWindow().getScaledHeight() - (yOffset += 9);
                int pingIntX = n + HudModule.mc.textRenderer.getWidth("Ping:");
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, "Ping:", n, pingY, globalColor);
                event.getContext().drawTextWithShadow(HudModule.mc.textRenderer, " " + ServerManager.getPing(), pingIntX, pingY, 0xFFFFFF);
            }
        }
    }

    public static String getDuration(StatusEffectInstance pe) {
        if (pe.isInfinite()) {
            return "*:*";
        }
        int var1 = pe.getDuration();
        int mins = var1 / 1200;
        String sec = String.format("%02d", var1 % 1200 / 20);
        return mins + ":" + sec;
    }

    public String getCurrentSong() {
        try {
            String line;
            ProcessBuilder builder = new ProcessBuilder("playerctl", "metadata", "--format", "{{ artist }} - {{ title }}");
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder song = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                song.append(line);
            }
            process.waitFor();
            return song.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getSpoofedName() {
        NameProtect nameHiderModule = Moonlight.moduleManager.getModuleByClass(NameProtect.class);
        return nameHiderModule.isEnabled() ? nameHiderModule.newName.getValue() : HudModule.mc.player.getName().getString();
    }

    public int[] getPlayerSize() {
        List nameLengths = HudModule.mc.world.getPlayers().stream().filter(e -> e != HudModule.mc.player).map(e -> HudModule.mc.textRenderer.getWidth(e.getDisplayName().getString() + " | " + e.getBlockPos().getX() + " " + e.getBlockPos().getY() + " " + e.getBlockPos().getZ() + " (" + Math.round(HudModule.mc.player.distanceTo((Entity)e)) + "m)")).collect(Collectors.toList());
        nameLengths.add(HudModule.mc.textRenderer.getWidth("Players:"));
        nameLengths.sort(Comparator.reverseOrder());
        return new int[]{(Integer)nameLengths.get(0) + 2, nameLengths.size() * 10 + 1};
    }

    public static enum colorMode {
        GLOBAL,
        CYCLE;

    }

    public static enum welcomerMode {
        TIME,
        LINUXAUDIO,
        MOONLIGHT,
        CLASSIC,
        BITCH;

    }
}
