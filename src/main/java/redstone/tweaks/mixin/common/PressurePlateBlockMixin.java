package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PressurePlateOverrides;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements PressurePlateOverrides {

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

	@Override
	public int delayRisingEdge() {
		return isStone() ? Tweaks.StonePressurePlate.delayRisingEdge() : Tweaks.WoodenPressurePlate.delayRisingEdge();
	}

	@Override
	public int delayFallingEdge() {
		return isStone() ? Tweaks.StonePressurePlate.delayFallingEdge() : Tweaks.WoodenPressurePlate.delayFallingEdge();
	}

	@Override
	public TickPriority tickPriorityRisingEdge() {
		return isStone() ? Tweaks.StonePressurePlate.tickPriorityRisingEdge() : Tweaks.WoodenPressurePlate.tickPriorityRisingEdge();
	}

	@Override
	public TickPriority tickPriorityFallingEdge() {
		return isStone() ? Tweaks.StonePressurePlate.tickPriorityFallingEdge() : Tweaks.WoodenPressurePlate.tickPriorityFallingEdge();
	}

	private boolean isStone() {
		return block().defaultBlockState().getMaterial() == Material.STONE;
	}
}
