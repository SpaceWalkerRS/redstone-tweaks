package redstone.tweaks.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin implements BlockOverrides {

	boolean ticking;

	@ModifyConstant(
		method = "findPoweredRailSignal",
		constant = @Constant(
			intValue = 8
		)
	)
	private int rtTweakPowerLimit(int powerLimit) {
		return powerLimit() - 1;
	}

	@Redirect(
		method = "isSameRailWithPower",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakNeighborQC(Level level, BlockPos pos) {
		Map<Direction, Boolean> qc = quasiConnectivity();
		boolean randQC = randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}

	@Redirect(
		method = "updateState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakHasSignal(Level _level, BlockPos _pos, BlockState state, Level level, BlockPos pos, Block neighborBlock) {
		if (ticking) {
			boolean powered = state.getValue(PoweredRailBlock.POWERED);
			boolean lazy = powered ? lazyFallingEdge() : lazyRisingEdge();

			if (lazy) {
				return !powered;
			}
		}

		Map<Direction, Boolean> qc = quasiConnectivity();
		boolean randQC = randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}

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
		int delay = powered ? delayFallingEdge() : delayRisingEdge();

		if (delay > 0 && !ticking) {
			TickPriority priority = powered ? tickPriorityFallingEdge() : tickPriorityRisingEdge();
			level.scheduleTick(pos, block(), delay, priority);

			ci.cancel();
		}
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return false;
	}

	private boolean isActivatorRail() {
		return block() == Blocks.ACTIVATOR_RAIL;
	}

	private int powerLimit() {
		return isActivatorRail() ? Tweaks.ActivatorRail.powerLimit() : Tweaks.PoweredRail.powerLimit();
	}

	private Map<Direction, Boolean> quasiConnectivity() {
		return isActivatorRail() ? Tweaks.ActivatorRail.quasiConnectivity() : Tweaks.PoweredRail.quasiConnectivity();
	}

	private boolean randomizeQuasiConnectivity() {
		return isActivatorRail() ? Tweaks.ActivatorRail.randomizeQuasiConnectivity() : Tweaks.PoweredRail.randomizeQuasiConnectivity();
	}

	private boolean lazyRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.lazyRisingEdge() : Tweaks.PoweredRail.lazyRisingEdge();
	}

	private boolean lazyFallingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.lazyFallingEdge() : Tweaks.PoweredRail.lazyFallingEdge();
	}

	private int delayRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.delayRisingEdge() : Tweaks.PoweredRail.delayRisingEdge();
	}

	private int delayFallingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.delayFallingEdge() : Tweaks.PoweredRail.delayFallingEdge();
	}

	private TickPriority tickPriorityRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.tickPriorityRisingEdge() : Tweaks.PoweredRail.tickPriorityRisingEdge();
	}

	private TickPriority tickPriorityFallingEdge() {
		return block() == Blocks.ACTIVATOR_RAIL ? Tweaks.ActivatorRail.tickPriorityFallingEdge() : Tweaks.PoweredRail.tickPriorityFallingEdge();
	}
}
