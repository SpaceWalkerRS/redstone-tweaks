package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin {

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
