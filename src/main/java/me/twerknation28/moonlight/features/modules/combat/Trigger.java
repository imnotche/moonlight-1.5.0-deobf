package me.twerknation28.moonlight.features.modules.combat;

import java.util.ArrayList;
import java.util.Random;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.modules.Module;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.manager.RotationManager;
import me.twerknation28.moonlight.util.MathUtil;
import me.twerknation28.moonlight.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

public class Trigger
extends Module {
    public final Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    public final Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", true));
    public final Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    public final Setting<Boolean> vehicles = this.register(new Setting<Boolean>("Vehicles", false));
    public final Setting<Boolean> onlySword = this.register(new Setting<Boolean>("OnlySword", true));
    public final Setting<Boolean> ignoreWalls = this.register(new Setting<Boolean>("IgnoreWalls", true));
    public final Setting<Boolean> criticals = this.register(new Setting<Boolean>("Criticals", false));
    public final Setting<Boolean> nextTick = this.register(new Setting<Boolean>("TickSkip", true));
    public final Setting<Boolean> newDelay = this.register(new Setting<Boolean>("1.20 Delay", true));
    public final Setting<Float> minRange = this.register(new Setting<Float>("Min Range", Float.valueOf(2.7f), Float.valueOf(0.0f), Float.valueOf(4.0f), Float.valueOf(0.1f)));
    public final Setting<Float> maxRange = this.register(new Setting<Float>("Max Range", Float.valueOf(3.0f), Float.valueOf(0.0f), Float.valueOf(4.0f), Float.valueOf(0.1f)));
    int ticks = 0;
    boolean doNextTick = false;
    int extraTicks = 0;
    float randRange;

    public Trigger() {
        super("Trigger", "Highly advanced auto-ban", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.extraTicks = MathUtil.getRandomInt(0, 2);
        this.randRange = (float)this.getRandomRange(this.minRange.getValue().floatValue(), this.maxRange.getValue().floatValue());
    }

    @Override
    public void onTick() {
        if (!(Trigger.mc.targetedEntity == null || Trigger.mc.player.isUsingItem() || !Trigger.mc.player.isHolding(item -> item.getItem() instanceof SwordItem) && this.onlySword.getValue().booleanValue() || Trigger.mc.currentScreen != null || this.newDelay.getValue().booleanValue() && (double)Trigger.mc.player.getAttackCooldownProgress(0.0f) < 1.0)) {
            if (this.nextTick.getValue().booleanValue() && !this.doNextTick) {
                if (this.ticks < this.extraTicks) {
                    ++this.ticks;
                } else {
                    this.doNextTick = true;
                    this.ticks = 0;
                }
            } else if (Trigger.mc.player.isOnGround() || !((double)Trigger.mc.player.fallDistance < 0.1) || !Trigger.mc.options.jumpKey.isPressed() || !this.criticals.getValue().booleanValue()) {
                Entity target = RotationManager.getCrosshairTarget(Trigger.mc.player.getYaw(), Trigger.mc.player.getPitch(), this.randRange, this.ignoreWalls.getValue());
                ArrayList<EntityType> toTarget = new ArrayList<EntityType>();
                if (this.players.getValue().booleanValue()) {
                    toTarget.add(EntityType.PLAYER);
                }
                if (this.mobs.getValue().booleanValue()) {
                    toTarget.add(EntityType.ZOMBIE);
                }
                if (this.animals.getValue().booleanValue()) {
                    toTarget.add(EntityType.PIG);
                }
                if (this.vehicles.getValue().booleanValue()) {
                    toTarget.add(EntityType.MINECART);
                }
                if (WorldUtil.canTarget(target, toTarget)) {
                    Trigger.mc.interactionManager.attackEntity((PlayerEntity)Trigger.mc.player, target);
                    Trigger.mc.player.swingHand(Hand.MAIN_HAND);
                    this.doNextTick = false;
                    this.ticks = 0;
                    this.extraTicks = MathUtil.getRandomInt(0, 1);
                    this.randRange = (float)this.getRandomRange(this.minRange.getValue().floatValue(), this.maxRange.getValue().floatValue());
                }
            }
        }
    }

    public double getRandomRange(float min, float max) {
        Random random = new Random();
        float randomValue = min + random.nextFloat() * (max - min);
        return (float)Math.round(randomValue * 10.0f) / 10.0f;
    }
}
