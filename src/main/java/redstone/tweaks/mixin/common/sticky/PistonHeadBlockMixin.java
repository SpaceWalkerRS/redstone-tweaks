package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin implements BlockOverrides {

	@Override
	public boolean isSticky(BlockState state) {
		boolean isSticky = (state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY);
		return (!Tweaks.Piston.looseHead(isSticky) && Tweaks.Piston.movableWhenExtended(isSticky)) || (isSticky && Tweaks.StickyPiston.superSticky());
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		Direction facing = state.getValue(PistonHeadBlock.FACING);

		// piston heads can be sticky on their face or their base
		if (facing.getAxis() != dir.getAxis()) {
			return false;
		}

		boolean isSticky = (state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY);

		if (facing == dir) {
			return isSticky && Tweaks.StickyPiston.superSticky();
		} else {
			if (Tweaks.Piston.looseHead(isSticky)) {
				return false;
			}
			if (!Tweaks.Piston.movableWhenExtended(isSticky)) {
				return false;
			}
			if (!(neighborState.getBlock() instanceof PistonOverrides)) {
				return false;
			}

			boolean baseSticky = ((PistonOverrides)neighborState.getBlock()).isSticky();

			if (isSticky != baseSticky) {
				return false;
			}

			return facing == neighborState.getValue(PistonBaseBlock.FACING);
		}
	}
}
