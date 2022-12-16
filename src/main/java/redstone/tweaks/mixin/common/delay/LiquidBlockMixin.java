package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {

	@Shadow private FlowingFluid fluid;

	@Redirect(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Fluid fluid, int delay, BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		FluidOverrides.scheduleOrDoTick(level, pos, state.getFluidState(), delay, ((FluidOverrides)fluid).tickPriority());
	}

	@Redirect(
		method = "updateShape",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Fluid fluid, int delay, BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		FluidOverrides.scheduleOrDoTick(level, pos, state.getFluidState(), delay, ((FluidOverrides)fluid).tickPriority());
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Fluid fluid, int delay, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		FluidOverrides.scheduleOrDoTick(level, pos, state.getFluidState(), delay, ((FluidOverrides)fluid).tickPriority());
	}
}
