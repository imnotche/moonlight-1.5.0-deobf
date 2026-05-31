package me.twerknation28.moonlight.util;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import java.util.Arrays;
import net.minecraft.util.math.Direction;
import java.util.List;

public class DirectionUtil
{
    public static Boolean isCardinal(final EightWayDirections dir) {
        return dir.equals(EightWayDirections.EAST) || dir.equals(EightWayDirections.WEST) || dir.equals(EightWayDirections.SOUTH) || dir.equals(EightWayDirections.NORTH);
    }
    
    public enum EightWayDirections
    {
        NORTH(new Direction[] { Direction.NORTH }), 
        SOUTH(new Direction[] { Direction.SOUTH }), 
        EAST(new Direction[] { Direction.EAST }), 
        WEST(new Direction[] { Direction.WEST }), 
        NORTHEAST(new Direction[] { Direction.NORTH, Direction.EAST }), 
        NORTHWEST(new Direction[] { Direction.NORTH, Direction.WEST }), 
        SOUTHEAST(new Direction[] { Direction.SOUTH, Direction.EAST }), 
        SOUTHWEST(new Direction[] { Direction.SOUTH, Direction.WEST });
        
        private final List<Direction> directions;
        
        private EightWayDirections(final Direction[] directions) {
            this.directions = Arrays.asList(directions);
        }
        
        public BlockPos offset(final BlockPos pos) {
            BlockPos result = pos;
            for (final Direction direction : this.directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }
    
    public enum FourWayDirections
    {
        NORTH(new Direction[] { Direction.NORTH }), 
        SOUTH(new Direction[] { Direction.SOUTH }), 
        EAST(new Direction[] { Direction.EAST }), 
        WEST(new Direction[] { Direction.WEST });
        
        private final List<Direction> directions;
        
        private FourWayDirections(final Direction[] directions) {
            this.directions = Arrays.asList(directions);
        }
        
        public BlockPos offset(final BlockPos pos) {
            BlockPos result = pos;
            for (final Direction direction : this.directions) {
                result = result.offset(direction);
            }
            return result;
        }
    }
}
