package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ILevel {

	boolean hasBlockEvent(BlockPos pos, Block block);

	void prepareMovedBlockEntityPlacement(BlockPos pos, BlockState state, BlockEntity blockEntity);

	BlockEntity getMovedBlockEntityForPlacement(BlockPos pos, BlockState state);

}
