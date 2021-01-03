package redstonetweaks.world.common;

import net.minecraft.util.math.BlockPos;

import redstonetweaks.util.UpdateType;

// A neighbor update object with absolute positions
public class NeighborUpdate {
	
	private final UpdateType type;
	private final BlockPos updatePos;
	private final BlockPos notifierPos;
	private final BlockPos sourcePos;
	
	public NeighborUpdate(UpdateType type, BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos) {
		this.type = type;
		this.updatePos = updatePos;
		this.notifierPos = notifierPos;
		this.sourcePos = sourcePos;
	}
	
	public UpdateType getType() {
		return type;
	}
	
	public boolean is(UpdateType type) {
		return this.type == type;
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
}
