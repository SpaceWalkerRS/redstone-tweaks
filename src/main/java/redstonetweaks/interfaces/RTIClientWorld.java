package redstonetweaks.interfaces;

import redstonetweaks.world.client.ClientNeighborUpdateScheduler;
import redstonetweaks.world.client.ClientUnfinishedEventScheduler;

public interface RTIClientWorld {
	
	public ClientNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ClientUnfinishedEventScheduler getUnfinishedEventScheduler();
	
}
