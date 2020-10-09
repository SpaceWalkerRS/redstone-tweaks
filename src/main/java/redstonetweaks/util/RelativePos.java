package redstonetweaks.util;

import java.util.function.BiFunction;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public enum RelativePos {
	
	SELF (0 , "self" , Directionality.NONE      , (sourcePos, forward) -> sourcePos),
	DOWN (1 , "down" , Directionality.NONE      , (sourcePos, forward) -> sourcePos.down()),
	UP   (2 , "up"   , Directionality.NONE      , (sourcePos, forward) -> sourcePos.up()),
	NORTH(3 , "north", Directionality.NONE      , (sourcePos, forward) -> sourcePos.north()),
	SOUTH(4 , "south", Directionality.NONE      , (sourcePos, forward) -> sourcePos.south()),
	WEST (5 , "west" , Directionality.NONE      , (sourcePos, forward) -> sourcePos.west()),
	EAST (6 , "east" , Directionality.NONE      , (sourcePos, forward) -> sourcePos.east()),
	FRONT(7 , "front", Directionality.ALL       , (sourcePos, forward) -> sourcePos.offset(forward)),
	BACK (8 , "back" , Directionality.ALL       , (sourcePos, forward) -> sourcePos.offset(forward.getOpposite())),
	LEFT (9 , "left" , Directionality.HORIZONTAL, (sourcePos, forward) -> sourcePos.offset(forward.rotateYCounterclockwise())),
	RIGHT(10, "right", Directionality.HORIZONTAL, (sourcePos, forward) -> sourcePos.offset(forward.rotateYClockwise()));
	
	private static final RelativePos[] POSITIONS;
	
	static {
		POSITIONS = new RelativePos[values().length];
		
		for (RelativePos dir : values()) {
			POSITIONS[dir.index] = dir;
		}
	}
	
	private final int index;
	private final String name;
	private final Directionality directionality;
	private final BiFunction<BlockPos, Direction, BlockPos> toBlockPos;
	
	private RelativePos(int index, String name, Directionality directionality, BiFunction<BlockPos, Direction, BlockPos> toBlockPos) {
		this.index = index;
		this.name = name;
		this.directionality = directionality;
		this.toBlockPos = toBlockPos;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static RelativePos fromIndex(int index) {
		if (index >= 0 && index < POSITIONS.length) {
			return POSITIONS[index];
		}
		return SELF;
	}
	
	public String getName() {
		return name;
	}
	
	public BlockPos getPos(BlockPos sourcePos, Direction sourceFacing) {
		return toBlockPos.apply(sourcePos, sourceFacing);
	}
	
	public boolean matches(Directionality directionality) {
		if (this.directionality == directionality || this.directionality == Directionality.NONE) {
			return true;
		}
		return directionality != Directionality.NONE && this.directionality == Directionality.ALL;
	}
	
	public RelativePos next(Directionality directionality) {
		RelativePos nextPos = fromIndex(index + 1);
		return nextPos.matches(directionality) ? nextPos : nextPos.next(directionality);
	}
}
