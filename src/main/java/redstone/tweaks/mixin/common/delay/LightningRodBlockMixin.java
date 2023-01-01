package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(LightningRodBlock.class)
public class LightningRodBlockMixin {

	private boolean ticking;

	@Shadow private void onLightningStrike(BlockState blockState, Level level, BlockPos blockPos) { }

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"
		)
	)
	private void rtTweakWaterTickPriority(LevelAccessor _level, BlockPos _pos, Fluid fluid, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		FluidOverrides.scheduleOrDoTick(level, pos, state.getFluidState(), delay, Tweaks.Water.tickPriority());
	}

	@Inject(
		method = "onLightningStrike",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakRisingEdgeDelayAndTickPriority(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		if (!ticking) {
			int delay = Tweaks.LightningRod.delayRisingEdge();

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.LightningRod.tickPriorityRisingEdge());

				ci.cancel();
			}
		}
	}

	@Redirect(
		method = "onLightningStrike",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakFallingEdgeDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, BlockState state, Level level, BlockPos pos) {
		delay = Tweaks.LightningRod.delayFallingEdge();
		TickPriority priority = Tweaks.LightningRod.tickPriorityFallingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakRisingEdgeDelay(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!state.getValue(LightningRodBlock.POWERED)) {
			ticking = true;
			onLightningStrike(state, level, pos);
			ticking = false;

			ci.cancel();
		}
	}
}
