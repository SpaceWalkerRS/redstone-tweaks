package redstonetweaks.mixinterfaces;

import redstonetweaks.world.client.ClientNeighborUpdateScheduler;
import redstonetweaks.world.client.ClientIncompleteActionScheduler;

public interface RTIClientWorld {
	
	public ClientNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ClientIncompleteActionScheduler getIncompleteActionScheduler();
	
}
