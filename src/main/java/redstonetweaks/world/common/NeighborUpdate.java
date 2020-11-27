package redstonetweaks.world.common;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import redstonetweaks.util.UpdateType;

// A neighbor update object with absolute positions
public class NeighborUpdate {
	
	private final UpdateType type;
	private final BlockPos updatePos;
	private final BlockPos notifierPos;
	private final BlockPos sourcePos;
	private final BlockState state;
	
	public NeighborUpdate(UpdateType type, BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos, BlockState state) {
		this.type = type;
		this.updatePos = updatePos;
		this.notifierPos = notifierPos;
		this.sourcePos = sourcePos;
		this.state = state;
	}
	
	public UpdateType getType() {
		return type;
	}
	
	public BlockPos getUpdatePos() {
		return updatePos;
	}
	
	public BlockPos getNotifierPos() {
		return notifierPos;
	}
	
	public BlockPos getSourcePos() {
		return sourcePos;
	}
	
	public BlockState getState() {
		return state;
	}
}
