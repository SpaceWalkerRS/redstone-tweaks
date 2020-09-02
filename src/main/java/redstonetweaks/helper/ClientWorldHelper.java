package redstonetweaks.helper;

import redstonetweaks.world.client.ClientNeighborUpdateScheduler;
import redstonetweaks.world.client.ClientUnfinishedEventScheduler;

public interface ClientWorldHelper {
	
	public ClientNeighborUpdateScheduler getNeighborUpdateScheduler();
	
	public ClientUnfinishedEventScheduler getUnfinishedEventScheduler();
	
}
