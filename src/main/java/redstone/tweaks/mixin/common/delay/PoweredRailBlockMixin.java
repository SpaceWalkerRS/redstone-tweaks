package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PoweredRailOverrides;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements PoweredRailOverrides {

	private boolean ticking;

	@Inject(
		method = "updateState",
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Block neighborBlock, CallbackInfo ci, boolean powered) {
		if (!ticking) {
			int delay = powered ? delayFallingEdge() : delayRisingEdge();

			if (delay > 0) {
				TickPriority priority = powered ? tickPriorityFallingEdge() : tickPriorityRisingEdge();
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);

				ci.cancel();
			}
		}
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return false;
	}

	@Override
	public boolean isTicking() {
		return ticking;
	}
}
