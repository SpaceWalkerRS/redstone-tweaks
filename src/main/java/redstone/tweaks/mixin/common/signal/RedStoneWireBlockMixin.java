package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Directions;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin implements BlockOverrides {

	@Shadow private boolean shouldSignal;

	@Inject(
		method = "calculateTargetStrength",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakWireSignal(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getNeighborSignal(level, pos));
	}

	private int getNeighborSignal(Level level, BlockPos pos) {
		shouldSignal = false;
		int signal = level.getBestNeighborSignal(pos);
		shouldSignal = true;

		if (signal < Redstone.SIGNAL_MAX) {
			BlockPos abovePos = pos.above();
			BlockPos belowPos = pos.below();
			BlockState aboveState = level.getBlockState(abovePos);
			BlockState belowState = level.getBlockState(belowPos);

			boolean invertFlowOnGlass = Tweaks.RedstoneWire.invertFlowOnGlass();

			for (Direction dir : Directions.HORIZONTAL) {
				BlockPos sidePos = pos.relative(dir);
				BlockState sideState = level.getBlockState(sidePos);

				signal = Math.max(signal, getWireSignal(level, sidePos, sideState, dir));

				if (sideState.isRedstoneConductor(level, sidePos) || (invertFlowOnGlass && isGlass(sideState))) {
					if (!aboveState.isRedstoneConductor(level, abovePos)) {
						signal = Math.max(signal, getWireSignal(level, sidePos.above(), dir));
					}
				} else if (!(invertFlowOnGlass && isGlass(belowState))) {
					signal = Math.max(signal, getWireSignal(level, sidePos.below(), dir));
				}
			}
		}

		return signal;
	}

	private int getWireSignal(Level level, BlockPos pos, Direction dir) {
		return getWireSignal(level, pos, level.getBlockState(pos), dir);
	}

	private int getWireSignal(Level level, BlockPos pos, BlockState state, Direction dir) {
		if (!state.is(block())) {
			return Redstone.SIGNAL_MIN;
		}

		if (Tweaks.MagentaGlazedTerracotta.signalDiode()) {
			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);

			if (belowState.is(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
				Direction facing = belowState.getValue(GlazedTerracottaBlock.FACING);

				if (facing != dir) {
					return Redstone.SIGNAL_MIN;
				}
			}
		}

		return state.getValue(RedStoneWireBlock.POWER);
	}

	private boolean isGlass(BlockState state) {
		return state.getBlock() instanceof AbstractGlassBlock;
	}
}
