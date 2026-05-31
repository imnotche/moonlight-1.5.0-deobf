package me.twerknation28.moonlight.util;

import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.EntityType;
import java.util.ArrayList;
import net.minecraft.entity.Entity;

public class WorldUtil implements Util
{
    public static boolean canTarget(final Entity entity, final ArrayList<EntityType> toTarget) {
        return (toTarget.contains(EntityType.ZOMBIE) && entity instanceof HostileEntity) || (toTarget.contains(EntityType.PIG) && entity instanceof AnimalEntity) || ((entity instanceof BoatEntity || entity instanceof MinecartEntity) && toTarget.contains(EntityType.BOAT)) || toTarget.contains(entity.getType());
    }
}
