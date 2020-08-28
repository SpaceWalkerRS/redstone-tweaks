package redstonetweaks.helper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class BlockHelper extends AbstractBlock {
	
	public BlockHelper(Settings settings) {
		super(settings);
	}

	public static BlockState postProcessState(World world, BlockState state, BlockPos pos) {
		BlockState blockState = state;
		
		for (Direction direction : FACINGS) {
			BlockPos neighborPos = pos.offset(direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(neighborPos), world, pos, neighborPos);
		}
		return blockState;
	}
	
	public static Direction[] getFacings() {
		return FACINGS;
	}
}
