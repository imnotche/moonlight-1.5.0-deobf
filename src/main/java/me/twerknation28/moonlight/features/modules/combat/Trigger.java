package me.twerknation28.moonlight.features.modules.combat;

import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import me.twerknation28.moonlight.util.WorldUtil;
import net.minecraft.entity.EntityType;
import java.util.ArrayList;
import me.twerknation28.moonlight.manager.RotationManager;
import net.minecraft.item.SwordItem;
import me.twerknation28.moonlight.util.MathUtil;
import me.twerknation28.moonlight.features.api.Category;
import me.twerknation28.moonlight.features.settings.Setting;
import me.twerknation28.moonlight.features.modules.Module;

public class Trigger extends Module
{
    public final Setting<Boolean> players;
    public final Setting<Boolean> mobs;
    public final Setting<Boolean> animals;
    public final Setting<Boolean> vehicles;
    public final Setting<Boolean> onlySword;
    public final Setting<Boolean> ignoreWalls;
    public final Setting<Boolean> criticals;
    public final Setting<Boolean> nextTick;
    public final Setting<Boolean> newDelay;
    public final Setting<Float> minRange;
    public final Setting<Float> maxRange;
    int ticks;
    boolean doNextTick;
    int extraTicks;
    float randRange;
    
    public Trigger() {
        super("Trigger", "Highly advanced auto-ban", Category.COMBAT, true, false, false);
        this.players = this.register(new Setting<Boolean>("Players", true));
        this.mobs = this.register(new Setting<Boolean>("Mobs", true));
        this.animals = this.register(new Setting<Boolean>("Animals", false));
        this.vehicles = this.register(new Setting<Boolean>("Vehicles", false));
        this.onlySword = this.register(new Setting<Boolean>("OnlySword", true));
        this.ignoreWalls = this.register(new Setting<Boolean>("IgnoreWalls", true));
        this.criticals = this.register(new Setting<Boolean>("Criticals", false));
        this.nextTick = this.register(new Setting<Boolean>("TickSkip", true));
        this.newDelay = this.register(new Setting<Boolean>("1.20 Delay", true));
        this.minRange = this.register(new Setting<Float>("Min Range", 2.7f, 0.0f, 4.0f, 0.1f));
        this.maxRange = this.register(new Setting<Float>("Max Range", 3.0f, 0.0f, 4.0f, 0.1f));
        this.ticks = 0;
        this.doNextTick = false;
        this.extraTicks = 0;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.extraTicks = MathUtil.getRandomInt(0, 2);
        this.randRange = (float)this.getRandomRange(this.minRange.getValue(), this.maxRange.getValue());
    }
    
    @Override
    public void onTick() {
        if (Trigger.mc.targetedEntity != null && !Trigger.mc.player.isUsingItem() && (Trigger.mc.player.isHolding(item -> item.getItem() instanceof SwordItem) || !this.onlySword.getValue()) && Trigger.mc.currentScreen == null && (!this.newDelay.getValue() || Trigger.mc.player.getAttackCooldownProgress(0.0f) >= 1.0)) {
            if (this.nextTick.getValue() && !this.doNextTick) {
                if (this.ticks < this.extraTicks) {
                    ++this.ticks;
                }
                else {
                    this.doNextTick = true;
                    this.ticks = 0;
                }
            }
            else if (Trigger.mc.player.isOnGround() || Trigger.mc.player.fallDistance >= 0.1 || !Trigger.mc.options.jumpKey.isPressed() || !this.criticals.getValue()) {
                final Entity target = RotationManager.getCrosshairTarget(Trigger.mc.player.getYaw(), Trigger.mc.player.getPitch(), this.randRange, this.ignoreWalls.getValue());
                final ArrayList<EntityType> toTarget = new ArrayList<EntityType>();
                if (this.players.getValue()) {
                    toTarget.add(EntityType.PLAYER);
                }
                if (this.mobs.getValue()) {
                    toTarget.add(EntityType.ZOMBIE);
                }
                if (this.animals.getValue()) {
                    toTarget.add(EntityType.PIG);
                }
                if (this.vehicles.getValue()) {
                    toTarget.add(EntityType.MINECART);
                }
                if (WorldUtil.canTarget(target, toTarget)) {
                    Trigger.mc.interactionManager.attackEntity((PlayerEntity)Trigger.mc.player, target);
                    Trigger.mc.player.swingHand(Hand.MAIN_HAND);
                    this.doNextTick = false;
                    this.ticks = 0;
                    this.extraTicks = MathUtil.getRandomInt(0, 1);
                    this.randRange = (float)this.getRandomRange(this.minRange.getValue(), this.maxRange.getValue());
                }
            }
        }
    }
    
    public double getRandomRange(final float min, final float max) {
        final Random random = new Random();
        final float randomValue = min + random.nextFloat() * (max - min);
        return Math.round(randomValue * 10.0f) / 10.0f;
    }
}
