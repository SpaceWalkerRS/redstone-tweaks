package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(BasePressurePlateBlock.class)
public abstract class BasePressurePlateBlockMixin implements PressurePlateOverrides {

	@Inject(
		method = "getSignal", 
		cancellable = true, 
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		Integer override = overrideGetSignal(state, level, pos, dir);

		if (override != null) {
			cir.setReturnValue(override);
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
		Integer override = overrideGetDirectSignal(state, level, pos, dir);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}
}
