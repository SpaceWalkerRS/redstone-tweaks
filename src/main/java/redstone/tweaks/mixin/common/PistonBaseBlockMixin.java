package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

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

	@Redirect(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
		)
	)
	private static PushReaction rtTweakHayMovable(BlockState _state, BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
		if (state.is(Blocks.HAY_BLOCK) && Tweaks.Hay.blockMisalignedPistonMove()) {
			if (state.getValue(HayBlock.AXIS) != moveDir.getAxis()) {
				return PushReaction.BLOCK;
			}
		}

		return state.getPistonPushReaction();
	}
}
