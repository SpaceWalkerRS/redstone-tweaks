package redstone.tweaks.interfaces.mixin;

import java.util.Map;
import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
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
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#neighborChanged BlockBehaviour.neighborChanged}.
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

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#isSignalSource BlockBehaviour.isSignalSource}.
	 * 
	 * @return the result of the method call, or null if not to override it
	 */
	default Boolean overrideIsSignalSource(BlockState state) {
		return null;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#getSignal BlockBehaviour.getSignal}.
	 * 
	 * @return the result of the method call, or null if not to override it
	 */
	default Integer overrideGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		return null;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#getDirectSignal BlockBehaviour.getDirectSignal}.
	 * 
	 * @return the result of the method call, or null if not to override it
	 */
	default Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		return null;
	}

	/**
	 * Override for {@link net.minecraft.world.level.block.state.BlockBehaviour#getPistonPushReaction BlockBehaviour.getPistonPushReaction}.
	 * 
	 * @return the result of the method call, or null if not to override it
	 */
	default PushReaction overrideGetPistonPushReaction(BlockState state) {
		return null;
	}

	/**
	 * @return whether this block is sticky in any way when moved by pistons
	 */
	default boolean isSticky(BlockState state) {
		return false;
	}

	/**
	 * @return whether the neighboring block is pulled along if this block is moved by pistons
	 */
	default boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		return false;
	}

	public static boolean scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state, int delay, TickPriority priority) {
		return scheduleOrDoTick(level, pos, state, delay, priority, () -> false);
	}

	public static boolean scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state, int delay, TickPriority priority, BooleanSupplier microtickMode) {
		if (level instanceof ServerLevel) {
			if (delay > 0) {
				if (microtickMode.getAsBoolean()) {
					((ServerLevel)level).blockEvent(pos, state.getBlock(), delay - 1, 0);
				} else {
					level.scheduleTick(pos, state.getBlock(), delay, priority);
				}
			} else {
				state.tick((ServerLevel)level, pos, level.getRandom());
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
