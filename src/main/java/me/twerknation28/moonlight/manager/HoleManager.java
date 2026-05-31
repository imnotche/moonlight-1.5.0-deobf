package me.twerknation28.moonlight.manager;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import java.util.Collection;
import net.minecraft.util.math.Box;
import java.util.LinkedList;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import com.google.common.eventbus.Subscribe;
import me.twerknation28.moonlight.event.impl.UpdateEvent;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import me.twerknation28.moonlight.features.Feature;

public class HoleManager extends Feature
{
    private final int range = 8;
    private final List<Hole> holes;
    private final BlockPos.Mutable pos;
    
    public HoleManager() {
        this.holes = new ArrayList<Hole>();
        this.pos = new BlockPos.Mutable();
        HoleManager.EVENT_BUS.register(this);
    }
    
    @Subscribe
    private void onTick(final UpdateEvent event) {
        this.holes.clear();
        for (int x = -8; x < 8; ++x) {
            for (int y = -8; y < 8; ++y) {
                for (int z = -8; z < 8; ++z) {
                    this.pos.set(HoleManager.mc.player.getX() + x, HoleManager.mc.player.getY() + y, HoleManager.mc.player.getZ() + z);
                    final Hole hole = this.getHole((BlockPos)this.pos);
                    if (hole != null) {
                        this.holes.add(hole);
                    }
                }
            }
        }
    }
    
    @Nullable
    public Hole getHole(final BlockPos pos) {
        if (HoleManager.mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
            return null;
        }
        HoleType type = HoleType.BEDROCK;
        for (final Direction direction : Direction.Type.HORIZONTAL) {
            final Block block = HoleManager.mc.world.getBlockState(pos.offset(direction)).getBlock();
            if (block == Blocks.OBSIDIAN) {
                type = HoleType.UNSAFE;
            }
            else {
                if (block != Blocks.BEDROCK) {
                    return null;
                }
                continue;
            }
        }
        return new Hole(pos, type);
    }
    
    public static List<BlockPos> getSurroundEntities(final Entity entity) {
        final List<BlockPos> entities = new LinkedList<BlockPos>();
        entities.add(entity.getBlockPos());
        for (final Direction dir : Direction.values()) {
            if (dir.getAxis().isHorizontal()) {
                for (final BlockPos pos : PositionManager.getAllInBox(entity.getBoundingBox(), entity.getBlockPos())) {
                    if (!entities.contains(pos)) {
                        entities.add(pos);
                    }
                }
            }
        }
        return entities;
    }
    
    public List<BlockPos> getSurroundEntities(final BlockPos pos) {
        final List<BlockPos> entities = new LinkedList<BlockPos>();
        entities.add(pos);
        for (final Direction dir : Direction.values()) {
            if (dir.getAxis().isHorizontal()) {
                final BlockPos pos2 = pos.add(dir.getVector());
                final List<Entity> box = HoleManager.mc.world.getOtherEntities((Entity)null, new Box(pos2)).stream().filter(e -> !this.isEntityBlockingSurround(e)).toList();
                if (!box.isEmpty()) {
                    for (final Entity entity : box) {
                        entities.addAll(PositionManager.getAllInBox(entity.getBoundingBox(), pos));
                    }
                }
            }
        }
        return entities;
    }
    
    public boolean isEntityBlockingSurround(final Entity entity) {
        return entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof EndCrystalEntity;
    }
    
    public static List<BlockPos> getEntitySurroundNoSupport(final Entity entity) {
        final List<BlockPos> entities = getSurroundEntities(entity);
        final List<BlockPos> blocks = new CopyOnWriteArrayList<BlockPos>();
        for (final BlockPos epos : entities) {
            for (final Direction dir2 : Direction.values()) {
                if (dir2.getAxis().isHorizontal()) {
                    final BlockPos pos2 = epos.add(dir2.getVector());
                    if (!entities.contains(pos2) && !blocks.contains(pos2)) {
                        final double dist = HoleManager.mc.player.squaredDistanceTo(pos2.toCenterPos());
                        if (dist <= 16.0) {
                            blocks.add(pos2);
                        }
                    }
                }
            }
        }
        return blocks;
    }
    
    record Hole(BlockPos pos, HoleType holeType) {}
    
    private enum HoleType
    {
        BEDROCK, 
        UNSAFE;
    }
}
