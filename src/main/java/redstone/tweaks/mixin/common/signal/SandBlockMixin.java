package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Directions;

@Mixin(SandBlock.class)
public class SandBlockMixin implements BlockOverrides {

	@Override
	public boolean overrideOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (state.is(Blocks.RED_SAND) && !oldState.is(Blocks.RED_SAND)) {
			updateIndirectNeighbors(level, pos);
		}

		return false;
	}

	@Override
	public boolean overrideOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (state.is(Blocks.RED_SAND) && !newState.is(Blocks.RED_SAND)) {
			updateIndirectNeighbors(level, pos);
		}

		return false;
	}

	@Override
	public Boolean overrideIsSignalSource(BlockState state) {
		if (state.is(Blocks.RED_SAND)) {
			return Tweaks.RedSand.signal() > Redstone.SIGNAL_MIN || Tweaks.RedSand.signalDirect() > Redstone.SIGNAL_MIN;
		}

		return null;
	}

	@Override
	public Integer overrideGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		if (state.is(Blocks.RED_SAND)) {
			return Tweaks.RedSand.signal();
		}

		return null;
	}

	@Override
	public Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		if (state.is(Blocks.RED_SAND)) {
			return Tweaks.RedSand.signalDirect();
		}

		return null;
	}

	private void updateIndirectNeighbors(Level level, BlockPos pos) {
		if (Tweaks.RedSand.signalDirect() > Redstone.SIGNAL_MIN) {
			for (Direction dir : Directions.ALL) {
				level.updateNeighborsAt(pos.relative(dir), block());
			}
		}
	}
}
