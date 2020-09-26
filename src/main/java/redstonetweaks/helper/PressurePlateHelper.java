package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.world.TickPriority;

public interface PressurePlateHelper {
	
	public int delayRisingEdge(BlockState state);
	
	public int delayFallingEdge(BlockState state);
	
	public int powerWeak(BlockState state);
	
	public int powerStrong(BlockState state);
	
	public TickPriority tickPriorityRisingEdge(BlockState state);
	
	public TickPriority tickPriorityFallingEdge(BlockState state);
	
}
