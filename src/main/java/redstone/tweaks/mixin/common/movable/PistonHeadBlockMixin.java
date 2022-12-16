package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements BlockOverrides {

	@Shadow private boolean isFittingBase(BlockState headState, BlockState behindState) { return false; }

	@Redirect(
		method = "onRemove",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonHeadBlock;isFittingBase(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtShouldDestroyBaseOnRemove(PistonHeadBlock head, BlockState headState, BlockState behindState, BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		boolean isSticky = (state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY);
		return !movedByPiston && !Tweaks.Piston.looseHead(isSticky) && isFittingBase(headState, behindState);
	}

	@Inject(
		method = "canSurvive",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtCanSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (isMovable(state)) {
			cir.setReturnValue(true);
		}
	}

	@Override
	public PushReaction overrideGetPistonPushReaction(BlockState state) {
		return isMovable(state) ? PushReaction.NORMAL : null;
	}

	private static boolean isMovable(BlockState state) {
		boolean isSticky = (state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY);

		if (Tweaks.Piston.looseHead(isSticky)) {
			return true;
		}
		if (Tweaks.Piston.movableWhenExtended(isSticky)) {
			return true;
		}

		return false;
	}
}
