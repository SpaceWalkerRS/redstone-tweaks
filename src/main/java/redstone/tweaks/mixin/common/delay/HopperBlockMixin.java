package redstone.tweaks.mixin.common.delay;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(HopperBlock.class)
public class HopperBlockMixin implements BlockOverrides {

	@Shadow private void checkPoweredState(Level level, BlockPos pos, BlockState state) { }

	@Redirect(
		method = "checkPoweredState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private boolean onUpdateEnabledRedirectSetBlockState(Level _level, BlockPos _pos, BlockState newState, int flags, Level level, BlockPos pos, BlockState state) {
		boolean enabled = state.getValue(HopperBlock.ENABLED);

		int delay = delay(enabled);
		TickPriority priority = tickPriority(enabled);

		return BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		Map<Direction, Boolean> qc = Tweaks.Hopper.quasiConnectivity();
		boolean randQC = Tweaks.Hopper.randomizeQuasiConnectivity();

		boolean enabled = state.getValue(HopperBlock.ENABLED);
		boolean lazy = lazy(enabled);
		boolean hasSignal = BlockOverrides.hasSignal(level, pos, qc, randQC);
		boolean shouldBeEnabled = lazy ? !enabled : !hasSignal;

		if (enabled != shouldBeEnabled) {
			level.setBlock(pos, state = state.setValue(HopperBlock.ENABLED, shouldBeEnabled), Block.UPDATE_INVISIBLE);

			if (shouldBeEnabled == hasSignal) {
				checkPoweredState(level, pos, state);
			}
		}

		return true;
	}

	private int delay(boolean enabled) {
		return enabled ? Tweaks.Hopper.delayRisingEdge() : Tweaks.Hopper.delayFallingEdge();
	}

	private boolean lazy(boolean enabled) {
		return enabled ? Tweaks.Hopper.lazyRisingEdge() : Tweaks.Hopper.lazyFallingEdge();
	}

	private TickPriority tickPriority(boolean enabled) {
		return enabled ? Tweaks.Hopper.tickPriorityRisingEdge() : Tweaks.Hopper.tickPriorityFallingEdge();
	}
}
