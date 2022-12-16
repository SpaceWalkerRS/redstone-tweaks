package redstone.tweaks.mixin.common.sticky;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(SlimeBlock.class)
public class SlimeBlockMixin implements BlockOverrides {

	@Override
	public boolean isSticky(BlockState state) {
		return true;
	}

	@Override
	public boolean isStickyToNeighbor(Level level, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction dir, Direction moveDir) {
		return !neighborState.is(Blocks.HONEY_BLOCK);
	}
}
