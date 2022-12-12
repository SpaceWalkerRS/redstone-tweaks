package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

	@Inject(
		method = "isPushable",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
		)
	)
	private static void rtTweakBarrierMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(Blocks.BARRIER) && Tweaks.Barrier.movable()) {
			cir.setReturnValue(true);
		}
	}
}
