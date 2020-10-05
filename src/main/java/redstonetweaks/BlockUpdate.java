package redstonetweaks;

import java.util.function.Function;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockUpdate {
	
	private RelativePos notifierPos;
	private RelativePos updatePos;
	
	public BlockUpdate() {
		
	}
	
	public void initPos(BlockPos sourcePos) {
		
	}
	
	public void initPos(BlockPos sourcePos, Direction sourceFacing) {
		
	}
	
	public RelativePos getNotifierPos() {
		return notifierPos;
	}
	
	public RelativePos getUpdatePos() {
		return updatePos;
	}
	
	public void setNotifierPos(RelativePos pos) {
		notifierPos = pos;
	}
	
	public void setUpdatePos(RelativePos pos) {
		updatePos = pos;
	}
	
	public enum RelativePos {
		
		SELF(0, "self", PosType.NOTIFIER, (forward) -> null),
		ALL(1, "all", PosType.UPDATE, (forward) -> null),
		ALL_EXCEPT_SELF(2, "all except self", PosType.UPDATE, (forward) -> null),
		DOWN(3, "down", PosType.BOTH, (forward) -> Direction.DOWN),
		UP(4, "up", PosType.BOTH, (forward) -> Direction.UP),
		NORTH(5, "north", PosType.BOTH, (forward) -> Direction.NORTH),
		SOUTH(6, "south", PosType.BOTH, (forward) -> Direction.SOUTH),
		WEST(7, "west", PosType.BOTH, (forward) -> Direction.WEST),
		EAST(8, "east", PosType.BOTH, (forward) -> Direction.EAST),
		FRONT(9, "front", PosType.BOTH, (forward) -> forward),
		BACK(10, "back", PosType.BOTH, (forward) -> forward.getOpposite()),
		LEFT(11, "left", PosType.BOTH, (forward) -> forward.rotateYCounterclockwise()),
		RIGHT(12, "right", PosType.BOTH, (forward) -> forward.rotateYClockwise());
		
		private static final RelativePos[] POSITIONS;
		
		static {
			POSITIONS = new RelativePos[values().length];
			
			for (RelativePos dir : values()) {
				POSITIONS[dir.index] = dir;
			}
		}
		
		private final int index;
		private final String name;
		private final PosType type;
		private final Function<Direction, Direction> asDirection;
		
		private RelativePos(int index, String name, PosType type, Function<Direction, Direction> asDirection) {
			this.index = index;
			this.name = name;
			this.type = type;
			this.asDirection = asDirection;
		}
		
		public int getIndex() {
			return index;
		}
		
		public String getName() {
			return name;
		}
		
		public PosType getType() {
			return type;
		}
		
		public RelativePos next(PosType type) {
			int nextIndex = index + 1;
			if (nextIndex >= POSITIONS.length) {
				nextIndex = 0;
			}
			
			RelativePos nextPos = POSITIONS[nextIndex];
			return nextPos.type == type || nextPos.type == PosType.BOTH ? nextPos : nextPos.next(type);
		}
		
		public Direction asDirection(Direction forward) {
			return asDirection.apply(forward);
		}
	}
	
	public enum PosType {
		BOTH,
		NOTIFIER,
		UPDATE;
	}
}
