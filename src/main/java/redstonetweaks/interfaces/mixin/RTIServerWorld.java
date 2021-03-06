package redstonetweaks.interfaces.mixin;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

import redstonetweaks.world.server.ServerNeighborUpdateScheduler;
import redstonetweaks.world.server.ServerIncompleteActionScheduler;

public interface RTIServerWorld  {
	
	public void tickTimeAccess();
	
	public boolean hasBlockEvent(BlockPos pos, Block block, int... types);
	
	public ServerNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ServerIncompleteActionScheduler getIncompleteActionScheduler();
	
	public void processWeather();
	
	public void processTime();
	
	public void tickRaidManager();
	
	public void startProcessingBlockEvents();
	
	public boolean tryContinueProcessingBlockEvents();
	
	public void startTickingEntities(Profiler profiler);
	
	public boolean tryContinueTickingEntities(Profiler profiler);
	
}
