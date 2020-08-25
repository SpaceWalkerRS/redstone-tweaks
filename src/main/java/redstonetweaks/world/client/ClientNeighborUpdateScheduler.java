package redstonetweaks.world.client;

import redstonetweaks.packet.NeighborUpdateSchedulerPacket;
import redstonetweaks.world.common.NeighborUpdateScheduler;

public class ClientNeighborUpdateScheduler extends NeighborUpdateScheduler {
	
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
