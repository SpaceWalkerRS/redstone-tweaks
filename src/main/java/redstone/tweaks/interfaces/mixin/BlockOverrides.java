package redstone.tweaks.interfaces.mixin;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

public interface BlockOverrides {

	default Block block() {
		return (Block)this;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#neighborChanged BlockBehaviour.neighborChanged}.
	 * 
	 * @return whether to override the method call
	 */
	default boolean overrideNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		return false;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#neighborChanged BlockBehaviour.triggerEvent}.
	 * 
	 * @return the result of the method call, or null if not to override it
	 */
	default Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		return null;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#tick BlockBehaviour.tick}.
	 * 
	 * @return whether to override the method call
	 */
	default boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		return false;
	}

	public static void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state, int delay, TickPriority priority) {
		if (level instanceof ServerLevel) {
			if (delay > 0) {
				level.scheduleTick(pos, state.getBlock(), delay, priority);
			} else {
				state.tick((ServerLevel) level, pos, level.getRandom());
			}
		}
	}

	public static boolean scheduleOrDoMicroTick(LevelAccessor level, BlockPos pos, BlockState state, int type, int data) {
		if (level instanceof ServerLevel) {
			if (type > 1) {
				((ServerLevel) level).blockEvent(pos, state.getBlock(), type - 1, data);
			} else {
				state.tick((ServerLevel) level, pos, level.getRandom());
			}
		}

		return false;
	}

	public static boolean hasSignal(Level level, BlockPos pos, Map<Direction, Boolean> qc, boolean randQC) {
		return level.hasNeighborSignal(pos) || hasQuasiSignal(level, pos, qc, randQC);
	}

	public static boolean hasQuasiSignal(Level level, BlockPos pos, Map<Direction, Boolean> qc, boolean randQC) {
		for (Direction dir : Direction.values()) {
			if (qc.get(dir) && (!randQC || level.random.nextBoolean())) {
				if (level.hasNeighborSignal(pos.relative(dir))) {
					return true;
				}
			}
		}

		return false;
	}
}
