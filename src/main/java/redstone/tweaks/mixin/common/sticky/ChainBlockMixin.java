package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ChainBlock.class)
public class ChainBlockMixin implements BlockOverrides {

	private boolean skipOppositeDir;

	@Override
	public boolean isSticky(BlockState state) {
		return Tweaks.Global.chainstone();
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		// While this tweak is inspired by the Carpet mod setting of the same name,
		// it behaves differently. The idea is that chains are strong when in tension,
		// which we model here by making them 'sticky' if the chain is fully anchored,
		// i.e. if the chain is attached to some other block on both ends.
		// We achieve this through recursive calls to this method. If the neighboring
		// block is a chain, check the next block over and repeat. If the neighboring
		// block is not a chain, then either it is a sturdy block (success!), and we
		// can check the chain in the other direction, or it is not a sturdy block,
		// and the whole chain is not anchored, and not sticky.

		Axis axis = state.getValue(ChainBlock.AXIS);

		if (dir.getAxis() != axis) {
			return false;
		}
		if (neighborState.is(block())) {
			Axis neighborAxis = neighborState.getValue(ChainBlock.AXIS);

			if (neighborAxis != axis) {
				return false;
			}

			return isStickyToNeighbor(level, neighborPos, dir, moveDir);
		} else if (neighborState.isFaceSturdy(level, neighborPos, dir.getOpposite(), SupportType.CENTER)) {
			// found anchor point, now check the opposite direction

			if (!skipOppositeDir) {
				// prevent infinite recursion
				skipOppositeDir = true;
				boolean sticky = isStickyToNeighbor(level, pos, dir.getOpposite(), moveDir);
				skipOppositeDir = false;

				return sticky;
			}

			return true;
		}

		return false;
	}

	private static boolean isStickyToNeighbor(Level level, BlockPos pos, Direction dir, Direction moveDir) {
		// request block state again in case it's a moving block
		// the MovingPistonBlockMixin will deal with that situation for us
		BlockState state = level.getBlockState(pos);
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighborState = level.getBlockState(neighborPos);

		return ((BlockOverrides)state.getBlock()).isStickyToNeighbor(level, pos, state, neighborPos, neighborState, dir, moveDir);
	}
}
