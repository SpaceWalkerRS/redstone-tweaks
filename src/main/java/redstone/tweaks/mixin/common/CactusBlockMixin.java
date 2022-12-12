package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(CactusBlock.class)
public class CactusBlockMixin {

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z"
		)
	)
	private void rtDestroyNeighbors(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (Tweaks.Cactus.nou() && isSupported(level, pos)) {
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				BlockPos side = pos.relative(dir);
				BlockState sideState = level.getBlockState(side);
				Material material = sideState.getMaterial();

				if (material.isSolid() || sideState.getFluidState().is(FluidTags.LAVA)) {
					level.destroyBlock(side, true);
				}
			}

			BlockPos above = pos.above();
			BlockState aboveState = level.getBlockState(above);
			Material material = aboveState.getMaterial();

			if (material.isLiquid()) {
				level.destroyBlock(above, true);
			}

			ci.cancel();
		}
	}

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		delay = Tweaks.Cactus.delay();
		TickPriority priority = Tweaks.Cactus.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	private boolean isSupported(Level level, BlockPos pos) {
		BlockPos below = pos.below();
		BlockState belowState = level.getBlockState(below);

		return belowState.is(Blocks.CACTUS) || belowState.is(Blocks.SAND) || belowState.is(Blocks.RED_SAND);
	}
}
