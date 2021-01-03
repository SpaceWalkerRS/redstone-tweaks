package redstonetweaks.world.client;

import redstonetweaks.packet.types.NeighborUpdateSchedulerPacket;
import redstonetweaks.world.common.INeighborUpdateScheduler;

public class ClientNeighborUpdateScheduler implements INeighborUpdateScheduler {
	
	public boolean hasScheduledNeighborUpdates = false;
	
	public ClientNeighborUpdateScheduler() {
		
	}
	
	@Override
	public boolean hasScheduledNeighborUpdates() {
		return hasScheduledNeighborUpdates;
	}
	
	public void onPacketReceived(NeighborUpdateSchedulerPacket packet) {
		hasScheduledNeighborUpdates = packet.hasScheduledUpdates;
	}
}
