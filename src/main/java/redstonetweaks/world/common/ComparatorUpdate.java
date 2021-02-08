package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class ComparatorUpdate extends NeighborUpdate {
	
	private final Block sourceBlock;
	
	public ComparatorUpdate(BlockPos updatePos, BlockPos notifierPos, BlockPos sourcePos, Block sourceBlock) {
		super(UpdateType.COMPARATOR_UPDATE, updatePos, notifierPos, sourcePos);
		
		this.sourceBlock = sourceBlock;
	}
	
	public Block getSourceBlock() {
		return sourceBlock;
	}
}
