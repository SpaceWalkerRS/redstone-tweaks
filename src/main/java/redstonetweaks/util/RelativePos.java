package redstonetweaks.util;

import java.util.function.Function;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public enum RelativePos {
	
	SELF (0 , "self" , Directionality.NONE      , (forward) -> null),
	DOWN (1 , "down" , Directionality.NONE      , (forward) -> Direction.DOWN),
	UP   (2 , "up"   , Directionality.NONE      , (forward) -> Direction.UP),
	NORTH(3 , "north", Directionality.NONE      , (forward) -> Direction.NORTH),
	SOUTH(4 , "south", Directionality.NONE      , (forward) -> Direction.SOUTH),
	WEST (5 , "west" , Directionality.NONE      , (forward) -> Direction.WEST),
	EAST (6 , "east" , Directionality.NONE      , (forward) -> Direction.EAST),
	FRONT(7 , "front", Directionality.BOTH      , (forward) -> forward),
	BACK (8 , "back" , Directionality.BOTH      , (forward) -> forward.getOpposite()),
	LEFT (9 , "left" , Directionality.HORIZONTAL, (forward) -> forward.rotateYCounterclockwise()),
	RIGHT(10, "right", Directionality.HORIZONTAL, (forward) -> forward.rotateYClockwise());
	
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
	private final Function<Direction, Direction> asDirection;
	
	private RelativePos(int index, String name, Directionality directionality, Function<Direction, Direction> asDirection) {
		this.index = index;
		this.name = name;
		this.directionality = directionality;
		this.asDirection = asDirection;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static RelativePos fromIndex(int index) {
		if (index < 0) {
			return POSITIONS[POSITIONS.length - 1];
		}
		if (index >= POSITIONS.length) {
			return POSITIONS[0];
		}
		return POSITIONS[index];
	}
	
	public String getName() {
		return name;
	}
	
	public Directionality getDirectionality() {
		return directionality;
	}
	
	public Direction asDirection(Direction forward) {
		return asDirection.apply(forward);
	}
	
	public BlockPos toBlockPos(BlockPos pos, Direction forward) {
		Direction dir = asDirection(forward);
		return dir == null ? pos : pos.offset(dir);
	}
	
	public boolean isValid(Directionality directionality) {
		if (this.directionality == directionality || this.directionality == Directionality.NONE) {
			return true;
		}
		return directionality != Directionality.NONE && this.directionality == Directionality.BOTH;
	}
	
	public RelativePos next(Directionality directionality) {
		RelativePos nextPos = fromIndex(index + 1);
		return nextPos.isValid(directionality) ? nextPos : nextPos.next(directionality);
	}
	
	public RelativePos previous(Directionality directionality) {
		RelativePos nextPos = fromIndex(index - 1);
		return nextPos.isValid(directionality) ? nextPos : nextPos.previous(directionality);
	}
}
