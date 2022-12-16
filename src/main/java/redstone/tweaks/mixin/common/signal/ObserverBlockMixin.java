package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin implements BlockOverrides {

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (level instanceof Level) {
			cir.setReturnValue(getSignal((Level)level, pos, state, dir, false));
		}
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (level instanceof Level) {
			cir.setReturnValue(getSignal((Level)level, pos, state, dir, true));
		}
	}

	private int getSignal(Level level, BlockPos pos, BlockState state, Direction dir, boolean direct) {
		if (level.isClientSide() || !state.getValue(ObserverBlock.POWERED)) {
			return Redstone.SIGNAL_MIN;
		}

		Direction facing = state.getValue(ObserverBlock.FACING);

		if (facing != dir) {
			return Redstone.SIGNAL_MIN;
		}

		return direct ? Tweaks.Observer.signalDirect() : Tweaks.Observer.signal();
	}
}
