package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

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

	@Redirect(
		method = "checkPressed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, Level level, BlockPos pos, BlockState state) {
		delay = Tweaks.DetectorRail.delay();
		TickPriority priority = Tweaks.DetectorRail.tickPriority();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}
}
