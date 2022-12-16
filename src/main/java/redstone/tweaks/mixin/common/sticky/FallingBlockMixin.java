package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Directions;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {

	@Shadow private static boolean isFree(BlockState belowState) { return false; }

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/FallingBlock;isFree(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtTweakSuspendedByStickyBlocks(BlockState belowState, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		return isFree(belowState) && !isSuspended(level, pos, state);
	}

	private boolean isSuspended(Level level, BlockPos pos, BlockState state) {
		if (Tweaks.FallingBlock.suspendedByStickyBlocks()) {
			for (Direction dir : Directions.HORIZONTAL) {
				if (isSuspended(level, pos, state, dir)) {
					return true;
				}
			}
			if (isSuspended(level, pos, state, Direction.UP)) {
				return true;
			}
		}

		return false;
	}

	private boolean isSuspended(Level level, BlockPos pos, BlockState state, Direction dir) {
		BlockPos neighborPos = pos.relative(dir);
		BlockState neighborState = level.getBlockState(neighborPos);
		BlockOverrides neighborBlock = (BlockOverrides)neighborState.getBlock();

		if (!neighborBlock.isSticky(neighborState)) {
			return false;
		}

		return neighborBlock.isStickyToNeighbor(level, neighborPos, neighborState, pos, state, dir.getOpposite(), dir);
	}
}
