package redstonetweaks.world.client;

import net.minecraft.client.world.ClientWorld;
import redstonetweaks.packet.types.IncompleteBlockActionPacket;
import redstonetweaks.world.common.IIncompleteActionScheduler;
import redstonetweaks.world.common.IncompleteBlockAction;

public class ClientIncompleteActionScheduler implements IIncompleteActionScheduler {
	
	private final ClientWorld world;
	
	public boolean hasScheduledActions;
	
	public ClientIncompleteActionScheduler(ClientWorld world) {
		this.world = world;
	}
	
	@Override
	public boolean hasScheduledActions() {
		return hasScheduledActions;
	}
	
	public void onIncompleteActionPacketReceived(IncompleteBlockActionPacket packet) {
		new IncompleteBlockAction(packet.pos, packet.type, packet.block).tryContinue(world);
	}
}
