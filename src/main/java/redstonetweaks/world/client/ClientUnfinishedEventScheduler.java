package redstonetweaks.world.client;

import net.minecraft.client.world.ClientWorld;
import redstonetweaks.packet.types.IncompleteBlockActionPacket;
import redstonetweaks.world.common.IIncompleteActionScheduler;
import redstonetweaks.world.common.IncompleteBlockAction;

public class ClientUnfinishedEventScheduler implements IIncompleteActionScheduler {
	
	private final ClientWorld world;
	
	public boolean hasScheduledEvents;
	
	public ClientUnfinishedEventScheduler(ClientWorld world) {
		this.world = world;
	}
	
	@Override
	public boolean hasScheduledActions() {
		return hasScheduledEvents;
	}
	
	public void onUnfinishedEventPacketReceived(IncompleteBlockActionPacket packet) {
		new IncompleteBlockAction(packet.pos, packet.type, packet.block).tryContinue(world);
	}
}
