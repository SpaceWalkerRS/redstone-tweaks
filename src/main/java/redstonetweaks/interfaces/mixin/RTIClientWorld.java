package redstonetweaks.interfaces.mixin;

import redstonetweaks.world.client.ClientNeighborUpdateScheduler;
import redstonetweaks.world.client.ClientIncompleteActionScheduler;

public interface RTIClientWorld {
	
	public ClientNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ClientIncompleteActionScheduler getIncompleteActionScheduler();
	
}
