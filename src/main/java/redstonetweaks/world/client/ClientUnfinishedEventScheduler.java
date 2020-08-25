package redstonetweaks.world.client;

import net.minecraft.client.world.ClientWorld;

import redstonetweaks.packet.UnfinishedEventPacket;
import redstonetweaks.world.common.UnfinishedEventScheduler;
import redstonetweaks.world.server.UnfinishedEvent;

public class ClientUnfinishedEventScheduler extends UnfinishedEventScheduler {
	
	public boolean hasScheduledEvents;
	
	public ClientUnfinishedEventScheduler(ClientWorld world) {
		super(world);
	}
	
	@Override
	public boolean hasScheduledEvents() {
		return hasScheduledEvents || super.hasScheduledEvents();
	}
	
	public void onUnfinishedEventPacketReceived(UnfinishedEventPacket packet) {
		UnfinishedEvent event = new UnfinishedEvent(packet.source, packet.pos, packet.block.getDefaultState(), packet.type);
		continueEvent(event);
	}
}
