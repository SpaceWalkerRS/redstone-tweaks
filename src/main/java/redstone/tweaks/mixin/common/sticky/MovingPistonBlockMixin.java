package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.IPistonMovingBlockEntity;

@Mixin(MovingPistonBlock.class)
public class MovingPistonBlockMixin implements BlockOverrides {

	@Override
	public boolean isSticky(BlockState state) {
		return Tweaks.Global.movableMovingBlocks();
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof PistonMovingBlockEntity)) {
			return false;
		}

		PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;
		BlockState movedState = ((IPistonMovingBlockEntity)mbe).recurseMovedState();

		if (movedState.is(block())) {
			return false;
		}

		Block movedBlock = movedState.getBlock();

		if (((BlockOverrides)movedBlock).isSticky(movedState)) {
			if (((BlockOverrides)movedBlock).isStickyToNeighbor(level, pos, movedState, neighborPos, neighborState, dir, moveDir)) {
				return true;
			}
			if (neighborState.is(block())) {
				BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);

				if (!(neighborBlockEntity instanceof PistonMovingBlockEntity)) {
					return false;
				}

				PistonMovingBlockEntity nmbe = (PistonMovingBlockEntity)neighborBlockEntity;
				BlockState neighborMovedState = ((IPistonMovingBlockEntity)nmbe).recurseMovedState();

				if (neighborMovedState.is(block())) {
					return false;
				}

				return ((BlockOverrides)movedBlock).isStickyToNeighbor(level, pos, movedState, neighborPos, neighborMovedState, dir, moveDir);
			}
		}

		return false;
	}
}
