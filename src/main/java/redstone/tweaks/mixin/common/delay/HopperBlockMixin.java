package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.HopperOverrides;

@Mixin(HopperBlock.class)
public class HopperBlockMixin implements HopperOverrides {

	private boolean ticking;

	@Shadow private void checkPoweredState(Level level, BlockPos pos, BlockState state) { }

	@Inject(
		method = "checkPoweredState",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakDelayAndTickPriority(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (!ticking) {
			boolean enabled = state.getValue(HopperBlock.ENABLED);
			int delay = delay(enabled);

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, tickPriority(enabled));

				ci.cancel();
			}
		}
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return true;
	}

	@Override
	public boolean isTicking() {
		return ticking;
	}

	private int delay(boolean enabled) {
		return enabled ? Tweaks.Hopper.delayRisingEdge() : Tweaks.Hopper.delayFallingEdge();
	}

	private TickPriority tickPriority(boolean enabled) {
		return enabled ? Tweaks.Hopper.tickPriorityRisingEdge() : Tweaks.Hopper.tickPriorityFallingEdge();
	}
}
