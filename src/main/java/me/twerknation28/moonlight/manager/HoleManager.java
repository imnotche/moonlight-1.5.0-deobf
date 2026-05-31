package me.twerknation28.moonlight.manager;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import me.twerknation28.moonlight.features.Feature;
import me.twerknation28.moonlight.manager.PositionManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class HoleManager
extends Feature {
    private final int range = 8;
    private final List<Hole> holes = new ArrayList<Hole>();
    private final BlockPos.Mutable pos = new BlockPos.Mutable();

    public HoleManager() {
        EVENT_BUS.register(this);
    }

    @Subscribe
    private void onTick(UpdateEvent event) {
        this.holes.clear();
        for (int x = -8; x < 8; ++x) {
            for (int y = -8; y < 8; ++y) {
                for (int z = -8; z < 8; ++z) {
                    this.pos.set(HoleManager.mc.player.getX() + (double)x, HoleManager.mc.player.getY() + (double)y, HoleManager.mc.player.getZ() + (double)z);
                    Hole hole = this.getHole((BlockPos)this.pos);
                    if (hole == null) continue;
                    this.holes.add(hole);
                }
            }
        }
    }

    @Nullable
    public Hole getHole(BlockPos pos) {
        if (HoleManager.mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
            return null;
        }
        HoleType type = HoleType.BEDROCK;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            Block block = HoleManager.mc.world.getBlockState(pos.offset(direction)).getBlock();
            if (block == Blocks.OBSIDIAN) {
                type = HoleType.UNSAFE;
                continue;
            }
            if (block == Blocks.BEDROCK) continue;
            return null;
        }
        return new Hole(pos, type);
    }

    public static List<BlockPos> getSurroundEntities(Entity entity) {
        LinkedList<BlockPos> entities = new LinkedList<BlockPos>();
        entities.add(entity.getBlockPos());
        for (Direction dir : Direction.values()) {
            if (!dir.getAxis().isHorizontal()) continue;
            for (BlockPos pos : PositionManager.getAllInBox(entity.getBoundingBox(), entity.getBlockPos())) {
                if (entities.contains(pos)) continue;
                entities.add(pos);
            }
        }
        return entities;
    }

    public List<BlockPos> getSurroundEntities(BlockPos pos) {
        LinkedList<BlockPos> entities = new LinkedList<BlockPos>();
        entities.add(pos);
        for (Direction dir : Direction.values()) {
            BlockPos pos1;
            List<Entity> box;
            if (!dir.getAxis().isHorizontal() || (box = HoleManager.mc.world.getOtherEntities(null, new Box(pos1 = pos.add(dir.getVector()))).stream().filter(e -> !this.isEntityBlockingSurround((Entity)e)).toList()).isEmpty()) continue;
            for (Entity entity : box) {
                entities.addAll(PositionManager.getAllInBox(entity.getBoundingBox(), pos));
            }
        }
        return entities;
    }

    public boolean isEntityBlockingSurround(Entity entity) {
        return entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof EndCrystalEntity;
    }

    public static List<BlockPos> getEntitySurroundNoSupport(Entity entity) {
        List<BlockPos> entities = HoleManager.getSurroundEntities(entity);
        CopyOnWriteArrayList<BlockPos> blocks = new CopyOnWriteArrayList<BlockPos>();
        for (BlockPos epos : entities) {
            for (Direction dir2 : Direction.values()) {
                double dist;
                BlockPos pos2;
                if (!dir2.getAxis().isHorizontal() || entities.contains(pos2 = epos.add(dir2.getVector())) || blocks.contains(pos2) || (dist = HoleManager.mc.player.squaredDistanceTo(pos2.toCenterPos())) > 16.0) continue;
                blocks.add(pos2);
            }
        }
        return blocks;
    }

    private record Hole(BlockPos pos, HoleType holeType) {
    }

    private static enum HoleType {
        BEDROCK,
        UNSAFE;

    }
}
