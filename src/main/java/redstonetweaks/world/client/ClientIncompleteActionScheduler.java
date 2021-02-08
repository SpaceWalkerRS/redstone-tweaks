package redstonetweaks.world.client;

import net.minecraft.client.world.ClientWorld;

import redstonetweaks.world.common.IIncompleteAction;
import redstonetweaks.world.common.IIncompleteActionScheduler;

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
	
	@Override
	public void scheduleAction(IIncompleteAction action) {
		action.tryContinue(world);
	}
}
