/*
 * Code in this package is based on code from gnembon's Carpet mod
 * (https://github.com/gnembon/fabric-carpet). See the CARPET_LICENSE
 * file located in this package.
 */

package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {

	@Shadow @Final private Level level;
	@Shadow @Final private Direction pushDirection;

	@Inject(
		method = "isSticky",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtIsSticky(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(((BlockOverrides)state.getBlock()).isSticky(state));
	}

	// fields that are needed because @Redirects cannot capture locals
	private BlockPos pos_addBlockLine;
	private BlockPos behindPos_addBlockLine;

	@Inject(
		method = "addBlockLine",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private void rtCaptureBlockLinePositions(BlockPos pos, Direction fromDir, CallbackInfoReturnable<Boolean> cir, BlockState state, int dst, BlockPos behindPos) {
		pos_addBlockLine = behindPos.relative(pushDirection);
		behindPos_addBlockLine = behindPos;
	}

	@Redirect(
		method = "addBlockLine",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtIsStickyToNeighbor(BlockState state, BlockState behindState) {
		return ((BlockOverrides)state.getBlock()).isStickyToNeighbor(level, pos_addBlockLine, state, behindPos_addBlockLine, behindState, pushDirection.getOpposite(), pushDirection);
	}

	// fields that are needed because @Redirects cannot capture locals
	private Direction dir_addBranchingBlocks;
	private BlockPos neighborPos_addBranchingBlocks;

	@Inject(
		method = "addBranchingBlocks",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private void rtCaptureNeighborPositions(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, Direction[] dirs, int i, int j, Direction dir, BlockPos neighborPos) {
		dir_addBranchingBlocks = dir;
		neighborPos_addBranchingBlocks = neighborPos;
	}

	@Redirect(
		method = "addBranchingBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtIsStickyToNeighbor(BlockState neighborState, BlockState state, BlockPos pos) {
		return ((BlockOverrides)state.getBlock()).isStickyToNeighbor(level, pos, state, neighborPos_addBranchingBlocks, neighborState, dir_addBranchingBlocks, pushDirection);
	}
}
