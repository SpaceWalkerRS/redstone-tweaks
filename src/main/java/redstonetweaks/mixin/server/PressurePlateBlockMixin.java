package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;

import redstonetweaks.interfaces.RTIPressurePlate;
import redstonetweaks.setting.Settings;
import redstonetweaks.world.common.UpdateOrder;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements RTIPressurePlate {
	
	public boolean isStone(BlockState state) {
		return state.getMaterial() == Material.STONE;
	}
	
	@Override
	public UpdateOrder updateOrder(BlockState state) {
		return isStone(state) ? Settings.StonePressurePlate.BLOCK_UPDATE_ORDER.get() : Settings.WoodenPressurePlate.BLOCK_UPDATE_ORDER.get();
	}
	
	@Override
	public int delayRisingEdge(BlockState state) {
		return isStone(state) ? Settings.StonePressurePlate.DELAY_RISING_EDGE.get() : Settings.WoodenPressurePlate.DELAY_RISING_EDGE.get();
	}
	
	@Override
	public int delayFallingEdge(BlockState state) {
		return isStone(state) ? Settings.StonePressurePlate.DELAY_FALLING_EDGE.get() : Settings.WoodenPressurePlate.DELAY_FALLING_EDGE.get();
	}
	
	@Override
	public int powerWeak(BlockView world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED) ? (isStone(state) ? Settings.StonePressurePlate.POWER_WEAK.get() : Settings.WoodenPressurePlate.POWER_WEAK.get()) : 0;
	}
	
	@Override
	public int powerStrong(BlockView world, BlockPos pos, BlockState state) {
		return state.get(Properties.POWERED) ? (isStone(state) ? Settings.StonePressurePlate.POWER_STRONG.get() : Settings.WoodenPressurePlate.POWER_STRONG.get()) : 0;
	}
	
	@Override
	public TickPriority tickPriorityRisingEdge(BlockState state) {
		return isStone(state) ? Settings.StonePressurePlate.TICK_PRIORITY_RISING_EDGE.get() : Settings.WoodenPressurePlate.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	@Override
	public TickPriority tickPriorityFallingEdge(BlockState state) {
		return isStone(state) ? Settings.StonePressurePlate.TICK_PRIORITY_FALLING_EDGE.get() : Settings.WoodenPressurePlate.TICK_PRIORITY_FALLING_EDGE.get();
	}
}
