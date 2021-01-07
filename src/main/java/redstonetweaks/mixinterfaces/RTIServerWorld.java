package redstonetweaks.mixinterfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

import redstonetweaks.world.server.ServerNeighborUpdateScheduler;
import redstonetweaks.world.server.ServerIncompleteActionScheduler;

public interface RTIServerWorld  {
	
	public void tickTimeAccess();
	
	public boolean hasBlockEvent(BlockPos pos);
	
	public boolean hasBlockEvent(BlockPos pos, int... types);
	
	public ServerNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ServerIncompleteActionScheduler getIncompleteActionScheduler();
	
	public void processWeather();
	
	public void processTime();
	
	public void tickRaidManager();
	
	public void startProcessingBlockEvents();
	
	public boolean tryContinueProcessingBlockEvents();
	
	public void tickEntities(Profiler profiler);
	
}
