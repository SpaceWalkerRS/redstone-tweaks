package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (state.getValue(DetectorRailBlock.POWERED)) {
			cir.setReturnValue(Tweaks.DetectorRail.signal());
		}
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignalDirect(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (state.getValue(DetectorRailBlock.POWERED) && dir == Direction.UP) {
			cir.setReturnValue(Tweaks.DetectorRail.signalDirect());
		}
	}
}
