package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements PistonOverrides {

	@Shadow @Final private boolean isSticky;

	@Override
	public boolean isSticky(BlockState state) {
		return (!Tweaks.Piston.looseHead(isSticky) && Tweaks.Piston.movableWhenExtended(isSticky)) || (isSticky && Tweaks.StickyPiston.superSticky());
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		Direction facing = state.getValue(PistonBaseBlock.FACING);

		// pistons are only ever sticky on their face
		if (facing != dir) {
			return false;
		}
		if (isSticky && Tweaks.StickyPiston.superSticky() && !state.getValue(PistonBaseBlock.EXTENDED)) {
			return true;
		}
		if (Tweaks.Piston.looseHead(isSticky)) {
			return false;
		}
		if (!Tweaks.Piston.movableWhenExtended(isSticky)) {
			return false;
		}
		if (!neighborState.is(Blocks.PISTON_HEAD)) {
			return false;
		}

		boolean headSticky = (neighborState.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY);

		if (isSticky != headSticky) {
			return false;
		}

		return facing == neighborState.getValue(PistonHeadBlock.FACING);
	}

	@Override
	public boolean isSticky() {
		return isSticky;
	}
}
