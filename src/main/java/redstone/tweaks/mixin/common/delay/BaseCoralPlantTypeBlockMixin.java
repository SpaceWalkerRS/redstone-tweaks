package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {

	@Redirect(
		method = "tryScheduleDieTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(LevelAccessor _level, BlockPos _pos, Block block, int delay, BlockState state, LevelAccessor level, BlockPos pos) {
		int min = Tweaks.Coral.delayMin();
		int max = Tweaks.Coral.delayMax();
		delay = min + level.getRandom().nextInt(max - min);
		TickPriority priority = Tweaks.Bamboo.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

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
}
