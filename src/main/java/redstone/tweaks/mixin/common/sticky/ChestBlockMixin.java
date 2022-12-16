package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ChestBlock.class)
public class ChestBlockMixin implements BlockOverrides {

	@Override
	public boolean isSticky(BlockState state) {
		return Tweaks.Global.movableBlockEntities();
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		if (!neighborState.is((Block)(Object)this)) {
			return false;
		}

		ChestType type = state.getValue(ChestBlock.TYPE);
		ChestType neighborType = neighborState.getValue(ChestBlock.TYPE);

		if (type == ChestType.SINGLE || neighborType == ChestType.SINGLE) {
			return false;
		}
		if (type != neighborType.getOpposite()) {
			return false;
		}

		Direction facing = state.getValue(ChestBlock.FACING);
		Direction neighborFacing = state.getValue(ChestBlock.FACING);

		return facing == neighborFacing;
	}
}
