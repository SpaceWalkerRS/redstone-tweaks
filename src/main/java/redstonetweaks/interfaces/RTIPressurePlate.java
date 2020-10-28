package redstonetweaks.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.world.TickPriority;
import redstonetweaks.world.common.UpdateOrder;

public interface RTIPressurePlate {
	
	public UpdateOrder updateOrder(BlockState state);
	
	public int delayRisingEdge(BlockState state);
	
	public int delayFallingEdge(BlockState state);
	
	public int powerWeak(BlockState state);
	
	public int powerStrong(BlockState state);
	
	public TickPriority tickPriorityRisingEdge(BlockState state);
	
	public TickPriority tickPriorityFallingEdge(BlockState state);
	
}
