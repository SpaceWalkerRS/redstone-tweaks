package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface RedstoneWireOverrides extends BlockOverrides {

	boolean blocksWireSignal(BlockGetter level, BlockPos pos, BlockState state, Direction side);

}
