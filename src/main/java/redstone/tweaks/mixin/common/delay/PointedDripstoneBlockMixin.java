package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

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

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakBelowDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		delay = Tweaks.PointedDripstone.delayBelow();
		TickPriority priority = Tweaks.PointedDripstone.tickPriorityBelow();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		delay = Tweaks.PointedDripstone.delay();
		TickPriority priority = Tweaks.PointedDripstone.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@ModifyConstant(
		method = "maybeTransferFluid",
		constant = @Constant(
			intValue = 50
		)
	)
	private static int rtTweakCauldronDelay(int delay) {
		return Tweaks.Cauldron.delay();
	}

	@Redirect(
		method = "maybeTransferFluid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private static void rtTweakCauldronTickPriority(ServerLevel level, BlockPos pos, Block block, int delay) {
		BlockOverrides.scheduleOrDoTick(level, pos, level.getBlockState(pos), delay, Tweaks.Cauldron.tickPriority());
	}
}
