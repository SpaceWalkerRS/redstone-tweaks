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

	public static BlockState PostProcessState(World world, BlockState state, BlockPos pos) {
		BlockState blockState = state;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : FACINGS) {
			mutable.set(pos, direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(mutable), world, pos, mutable);
		}
		return blockState;
	}
	
	public static Direction[] getFacings() {
		return FACINGS;
	}
}
