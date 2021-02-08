package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockUpdate extends NeighborUpdate {
	
	private final Block sourceBlock;
	
	public BlockUpdate(BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos, Block sourceBlock) {
		super(UpdateType.BLOCK_UPDATE, updatePos, notifierPos, sourcePos);
		
		this.sourceBlock = sourceBlock;
	}
	
	public Block getSourceBlock() {
		return sourceBlock;
	}
}
