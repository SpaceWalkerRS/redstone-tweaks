package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;
import redstone.tweaks.interfaces.mixin.PropertyOverrides;

@Mixin(DiodeBlock.class)
public abstract class DiodeBlockMixin implements DiodeOverrides {

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getSignal(level, pos, state, dir, false));
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignalDirect(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getSignal(level, pos, state, dir, true));
	}

	@Inject(
		method = "getAlternateSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void rtTweakAlternateSignal(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		if (invertAlternateSignal(state)) {
			cir.setReturnValue(-cir.getReturnValue());
		}
	}

	@Inject(
		method = "isAlternateInput",
		cancellable = true,
		at = @At(
			value = "RETURN"
		)
	)
	private void rtTweakRedstoneBlockAlternateInput(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (!Tweaks.Comparator.redstoneBlockAlternateInput() && state.is(Blocks.REDSTONE_BLOCK)) {
			cir.setReturnValue(false);
		}
	}

	private int getSignal(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean direct) {
		if (state.getValue(DiodeBlock.POWERED) && state.getValue(DiodeBlock.FACING) == dir) {
			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);

			if (direct) {
				return PropertyOverrides.overrideDirectSignal(belowState, signalDirect(level, pos, state));
			} else {
				return PropertyOverrides.overrideSignal(belowState, signal(level, pos, state));
			}
		}

		return Redstone.SIGNAL_MIN;
	}
}
