package redstonetweaks.world.server;

import net.minecraft.server.world.ServerWorld;

import redstonetweaks.helper.MinecraftServerHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.UnfinishedEventPacket;
import redstonetweaks.world.common.UnfinishedEvent;
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
		ServerPacketHandler packetHandler = ((MinecraftServerHelper)world.getServer()).getPacketHandler();
		
		if (event.viewDistance < 0) {
			packetHandler.sendPacketToDimension(packet, world.getRegistryKey());
		} else {
			packetHandler.sendPacketToAround(packet, world.getRegistryKey(), event.pos, event.viewDistance);
		}
	}
}
