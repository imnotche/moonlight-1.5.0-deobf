package me.twerknation28.moonlight.util;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DirectionUtil {
    public static Boolean isCardinal(EightWayDirections dir) {
        return dir.equals((Object)EightWayDirections.EAST) || dir.equals((Object)EightWayDirections.WEST) || dir.equals((Object)EightWayDirections.SOUTH) || dir.equals((Object)EightWayDirections.NORTH);
    }

    public static enum EightWayDirections {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST),
        NORTHEAST(Direction.NORTH, Direction.EAST),
        NORTHWEST(Direction.NORTH, Direction.WEST),
        SOUTHEAST(Direction.SOUTH, Direction.EAST),
        SOUTHWEST(Direction.SOUTH, Direction.WEST);

        private final List<Direction> directions;

        private EightWayDirections(Direction ... directions) {
            this.directions = Arrays.asList(directions);
        }

        public BlockPos offset(BlockPos pos) {
            BlockPos result = pos;
            for (Direction direction : this.directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }

    public static enum FourWayDirections {
        NORTH(Direction.NORTH),
        SOUTH(Direction.SOUTH),
        EAST(Direction.EAST),
        WEST(Direction.WEST);

        private final List<Direction> directions;

        private FourWayDirections(Direction ... directions) {
            this.directions = Arrays.asList(directions);
        }

        public BlockPos offset(BlockPos pos) {
            BlockPos result = pos;
            for (Direction direction : this.directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }
}
