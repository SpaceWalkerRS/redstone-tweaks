package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(PressurePlateBlock.class)
public abstract class PressurePlateBlockMixin implements PressurePlateOverrides {

	@Override
	public Integer overrideGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		if (state.getValue(PressurePlateBlock.POWERED)) {
			return isStone() ? Tweaks.StonePressurePlate.signal() : Tweaks.WoodenPressurePlate.signal();
		}

		return Redstone.SIGNAL_MIN;
	}

	@Override
	public Integer overrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
		if (dir == Direction.UP && state.getValue(PressurePlateBlock.POWERED)) {
			return isStone() ? Tweaks.StonePressurePlate.signalDirect() : Tweaks.WoodenPressurePlate.signalDirect();
		}

		return Redstone.SIGNAL_MIN;
	}

	private boolean isStone() {
		return block().defaultBlockState().getMaterial() == Material.STONE;
	}
}
