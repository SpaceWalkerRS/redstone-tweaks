package redstonetweaks.mixinterfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;

import redstonetweaks.world.common.UpdateOrder;

public interface RTIPressurePlate {
	
	public UpdateOrder updateOrder(BlockState state);
	
	public int delayRisingEdge(BlockState state);
	
	public int delayFallingEdge(BlockState state);
	
	public int powerWeak(BlockView world, BlockPos pos, BlockState state);
	
	public int powerStrong(BlockView world, BlockPos pos, BlockState state);
	
	public TickPriority tickPriorityRisingEdge(BlockState state);
	
	public TickPriority tickPriorityFallingEdge(BlockState state);
	
}
