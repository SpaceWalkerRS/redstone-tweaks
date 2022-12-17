package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IPistonMovingBlockEntity {

	void init(PistonOverrides source);

	float getAmountPerStep();

	PistonMovingBlockEntity recurseMovingBlock();

	void setMovedBlock(BlockState state, BlockEntity blockEntity);

	BlockEntity getMovedBlockEntity();

	BlockState recurseMovedState();

	BlockEntity recurseMovedBlockEntity();

	PistonMovingBlockEntity getParent();

	void setParent(PistonMovingBlockEntity movingBlockEntity);

	void tickMovedBlockEntity();

	boolean prepareMovedBlockPlacement();

}
