package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import redstonetweaks.util.UpdateType;

public class BlockUpdate extends NeighborUpdate {
	
	private final Block sourceBlock;
	
	public BlockUpdate(BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos, BlockState state, Block sourceBlock) {
		super(UpdateType.BLOCK_UPDATE, updatePos, notifierPos, sourcePos, state);
		
		this.sourceBlock = sourceBlock;
	}
	
	public Block getSourceBlock() {
		return sourceBlock;
	}
}
