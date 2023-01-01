package redstone.tweaks.mixin.common.ignore_updates_while_moving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PistonOverrides;
import redstone.tweaks.util.MotionType;

@Mixin(MovingPistonBlock.class)
public class MovingPistonBlockMixin implements BlockOverrides {

	private boolean ticking;

	@Shadow private PistonMovingBlockEntity getBlockEntity(BlockGetter level, BlockPos pos) { return null; }

	@Override
	public boolean overrideNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		PistonMovingBlockEntity mbe = getBlockEntity(level, pos);

		if (mbe != null && mbe.isSourcePiston()) {
			BlockState movedState = mbe.getMovedState();

			if (PistonOverrides.isBase(movedState)) {
				boolean isSticky = PistonOverrides.isBaseSticky(movedState);

				if (mbe.isExtending() ? !Tweaks.Piston.ignoreUpdatesWhileExtending(isSticky) : !Tweaks.Piston.ignoreUpdatesWhileRetracting(isSticky)) {
					return checkIfExtend(state, level, pos, movedState);
				}
			}
		}

		return false;
	}

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		PistonMovingBlockEntity mbe = getBlockEntity(level, pos);

		if (mbe != null && mbe.isSourcePiston()) {
			BlockState movedState = mbe.getMovedState();

			if (PistonOverrides.isBase(movedState)) {
				mbe.finalTick();
			}

			return true;
		}

		return null;
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return true;
	}

	private boolean checkIfExtend(BlockState state, Level level, BlockPos pos, BlockState pistonState) {
		PistonOverrides piston = (PistonOverrides)pistonState.getBlock();

		Direction facing = pistonState.getValue(PistonBaseBlock.FACING);
		boolean extended = pistonState.getValue(PistonBaseBlock.EXTENDED);

		boolean hasSignal = piston.hasSignal(level, pos, facing);
		boolean lazy = Tweaks.Piston.lazy(!extended, piston.isSticky());
		boolean shouldBeExtended = (ticking && lazy) ? !extended : hasSignal;

		if (extended != shouldBeExtended) {
			queueBlockEvent(piston, level, pos, state, MotionType.NONE, 0);
		}

		return true;
	}

	private void queueBlockEvent(PistonOverrides piston, Level level, BlockPos pos, BlockState state, int type, int data) {
		if (ticking) {
			level.blockEvent(pos, block(), type, data);
		} else {
			boolean extend = MotionType.isExtend(type);
			boolean isSticky = piston.isSticky();

			int delay = Tweaks.Piston.delay(extend, isSticky);
			TickPriority priority = Tweaks.Piston.tickPriority(extend, isSticky);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}
}
