package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.util.Directions;

@Mixin(PoweredBlock.class)
public class PoweredBlockMixin implements BlockOverrides {

	@Override
	public boolean overrideOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!state.is(oldState.getBlock())) {
			updateIndirectNeighbors(level, pos);
		}

		return false;
	}

	@Override
	public boolean overrideOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			updateIndirectNeighbors(level, pos);
		}

		return false;
	}

	@Override
	public Boolean overrideIsSignalSource(BlockState state) {
		return Tweaks.RedstoneBlock.signal() > Redstone.SIGNAL_MIN || Tweaks.RedstoneBlock.signalDirect() > Redstone.SIGNAL_MIN;
	}

	@Override
	public Integer overrideGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		return Tweaks.RedstoneBlock.signal();
	}

	@Override
	public Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		return Tweaks.RedstoneBlock.signalDirect();
	}

	private void updateIndirectNeighbors(Level level, BlockPos pos) {
		if (Tweaks.RedstoneBlock.signalDirect() > Redstone.SIGNAL_MIN) {
			for (Direction dir : Directions.ALL) {
				level.updateNeighborsAt(pos.relative(dir), block());
			}
		}
	}
}
