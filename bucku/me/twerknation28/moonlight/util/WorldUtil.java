package me.twerknation28.moonlight.util;

import java.util.ArrayList;
import me.twerknation28.moonlight.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;

public class WorldUtil
implements Util {
    public static boolean canTarget(Entity entity, ArrayList<EntityType> toTarget) {
        if (toTarget.contains(EntityType.ZOMBIE) && entity instanceof HostileEntity) {
            return true;
        }
        if (toTarget.contains(EntityType.PIG) && entity instanceof AnimalEntity) {
            return true;
        }
        return (entity instanceof BoatEntity || entity instanceof MinecartEntity) && toTarget.contains(EntityType.BOAT) ? true : toTarget.contains(entity.getType());
    }
}
