package redstonetweaks.world.common;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import redstonetweaks.util.UpdateType;

public class ShapeUpdate extends NeighborUpdate {
	
	private final BlockState notifierState;
	private final Direction direction;
	private final int flags;
	private final int depth;
	
	public ShapeUpdate(BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos, BlockState state, BlockState notifierState, Direction dir, int flags, int depth) {
		super(UpdateType.SHAPE_UPDATE, updatePos, notifierPos, sourcePos, state);
		
		this.notifierState = notifierState;
		this.direction = dir;
		this.flags = flags;
		this.depth = depth;
	}
	
	public BlockState getNotifierState() {
		return notifierState;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public int getDepth() {
		return depth;
	}
}
