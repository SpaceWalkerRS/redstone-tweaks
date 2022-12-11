package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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

	public static boolean microTick(Level level, BlockPos pos, BlockState state, int type, int data) {
		if (!level.isClientSide()) {
			if (type > 1) {
				level.blockEvent(pos, state.getBlock(), type - 1, data);
			} else {
				state.tick((ServerLevel)level, pos, level.random);
			}
		}

		return false;
	}
}
