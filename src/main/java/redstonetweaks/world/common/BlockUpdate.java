package redstonetweaks.world.common;

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
		
		SELF           (0 , "self"           , PosType.NOTIFIER, Directionality.ALL       , (forward) -> null),
		ALL            (1 , "all"            , PosType.UPDATE  , Directionality.ALL       , (forward) -> null),
		ALL_EXCEPT_SELF(2 , "all except self", PosType.UPDATE  , Directionality.ALL       , (forward) -> null),
		DOWN           (3 , "down"           , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.DOWN),
		UP             (4 , "up"             , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.UP),
		NORTH          (5 , "north"          , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.NORTH),
		SOUTH          (6 , "south"          , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.SOUTH),
		WEST           (7 , "west"           , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.WEST),
		EAST           (8 , "east"           , PosType.BOTH    , Directionality.ALL       , (forward) -> Direction.EAST),
		FRONT          (9 , "front"          , PosType.BOTH    , Directionality.ALL       , (forward) -> forward),
		BACK           (10, "back"           , PosType.BOTH    , Directionality.ALL       , (forward) -> forward.getOpposite()),
		LEFT           (11, "left"           , PosType.BOTH    , Directionality.HORIZONTAL, (forward) -> forward.rotateYCounterclockwise()),
		RIGHT          (12, "right"          , PosType.BOTH    , Directionality.HORIZONTAL, (forward) -> forward.rotateYClockwise());
		
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
		private final Directionality directionality;
		private final Function<Direction, Direction> asDirection;
		
		private RelativePos(int index, String name, PosType type, Directionality directionality, Function<Direction, Direction> asDirection) {
			this.index = index;
			this.name = name;
			this.type = type;
			this.directionality = directionality;
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

		public Direction asDirection(Direction forward) {
			return asDirection.apply(forward);
		}
		
		public boolean matches(PosType type, Directionality directionality) {
			return matchesPosType(type) && matchesDirectionality(directionality);
		}
		
		public boolean matchesPosType(PosType type) {
			return this.type == type || this.type == PosType.BOTH;
		}
		
		public boolean matchesDirectionality(Directionality directionality) {
			return this.directionality == directionality || this.directionality == Directionality.ALL;
		}
		
		public RelativePos next(PosType type, Directionality directionality) {
			int nextIndex = index + 1;
			if (nextIndex >= POSITIONS.length) {
				nextIndex = 0;
			}
			
			RelativePos nextPos = POSITIONS[nextIndex];
			return matches(type, directionality) ? nextPos : nextPos.next(type, directionality);
		}
	}
	
	public enum PosType {
		BOTH,
		NOTIFIER,
		UPDATE;
	}
}
