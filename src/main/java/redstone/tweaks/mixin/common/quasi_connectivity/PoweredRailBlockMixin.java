package redstone.tweaks.mixin.common.quasi_connectivity;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PoweredRailOverrides;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin implements PoweredRailOverrides {

	@Shadow private boolean findPoweredRailSignal(Level level, BlockPos pos, BlockState state, boolean towardsPositive, int depth) { return false; }

	@Redirect(
		method = "isSameRailWithPower",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakNeighborQuasiConnectivity(Level level, BlockPos pos) {
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
	private boolean rtTweakQuasiConnectivity(Level _level, BlockPos _pos, BlockState state, Level level, BlockPos pos, Block neighborBlock) {
		if (isTicking()) {
			boolean powered = state.getValue(PoweredRailBlock.POWERED);
			boolean lazy = powered ? lazyFallingEdge() : lazyRisingEdge();

			if (lazy) {
				return !powered;
			}
		}

		Map<Direction, Boolean> qc = quasiConnectivity();
		boolean randQC = randomizeQuasiConnectivity();

		if (BlockOverrides.hasSignal(level, pos, qc, randQC)) {
			return true;
		}

		return findPoweredRailSignal(level, pos, state, true, 0) || findPoweredRailSignal(level, pos, state, false, 0);
	}

	@Redirect(
		method = "updateState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/PoweredRailBlock;findPoweredRailSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;ZI)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(PoweredRailBlock poweredRail, Level level, BlockPos pos, BlockState state, boolean towardsPositive, int depth) {
		// replaced by redirect above
		return false;
	}
}
