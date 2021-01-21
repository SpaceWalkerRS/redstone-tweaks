package redstonetweaks.block.piston;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public class MovedBlock {
	
	private final BlockState state;
	private final BlockEntity blockEntity;
	
	public MovedBlock(BlockState state, BlockEntity blockEntity) {
		this.state = state;
		this.blockEntity = blockEntity;
	}
	
	public BlockState getBlockState() {
		return state;
	}
	
	public BlockEntity getBlockEntity() {
		return blockEntity;
	}
}
