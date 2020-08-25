package redstonetweaks.world.server;

import net.minecraft.server.world.ServerWorld;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.packet.UnfinishedEventPacket;
import redstonetweaks.world.common.UnfinishedEventScheduler;

public class ServerUnfinishedEventScheduler extends UnfinishedEventScheduler {
	
	public ServerUnfinishedEventScheduler(ServerWorld world) {
		super(world);
	}
	
	public void tick() {
		ServerNeighborUpdateScheduler neighborUpdateScheduler = ((ServerWorldHelper)world).getNeighborUpdateScheduler();
		
		while (!neighborUpdateScheduler.hasScheduledNeighborUpdates() && hasScheduledEvents()) {
			UnfinishedEvent event = unfinishedEvents.removeLast();
			
			if (continueEvent(event)) {
				syncClientNeighborUpdateScheduler(event);
			}
		}
	}
	
	private void syncClientNeighborUpdateScheduler(UnfinishedEvent event) {
		UnfinishedEventPacket packet = new UnfinishedEventPacket(event);
		if (event.viewDistance < 0) {
			((MinecraftServerHelper)world.getServer()).getPacketHandler().sendPacketToDimension(packet, world.getRegistryKey());
		} else {
			((MinecraftServerHelper)world.getServer()).getPacketHandler().sendPacketToAround(packet, world.getRegistryKey(), event.pos, event.viewDistance);
		}
	}

	@Override
	public void onUnfinishedEventPacketReceived(UnfinishedEventPacket unfinishedEventPacket) {
		
	}
}
