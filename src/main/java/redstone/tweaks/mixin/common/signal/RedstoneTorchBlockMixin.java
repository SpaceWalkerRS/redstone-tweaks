package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BehaviorOverrides;
import redstone.tweaks.interfaces.mixin.RedstoneTorchOverrides;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin implements RedstoneTorchOverrides {

	private boolean requestDirectSignal;

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakSignal(int signal, BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		Direction facing = getFacing(state);
		BlockPos behindPos = pos.relative(facing.getOpposite());
		BlockState behindState = level.getBlockState(behindPos);

		if (requestDirectSignal()) {
			return BehaviorOverrides.overrideDirectSignal(behindState, Tweaks.RedstoneTorch.signalDirect());
		} else {
			return BehaviorOverrides.overrideSignal(behindState, Tweaks.RedstoneTorch.signal());
		}
	}

	@Inject(
		method = "getDirectSignal",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getSignal(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
		)
	)
	private void rtTweakDirectSignal1(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		requestDirectSignal = true;
	}

	@Inject(
		method = "getDirectSignal",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/world/level/block/state/BlockState;getSignal(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
		)
	)
	private void rtTweakDirectSignal2(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		requestDirectSignal = false;
	}

	@Override
	public boolean requestDirectSignal() {
		return requestDirectSignal;
	}
}
