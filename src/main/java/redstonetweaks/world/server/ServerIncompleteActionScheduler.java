package redstonetweaks.world.server;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.server.world.ServerWorld;

import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.AbstractRedstoneTweaksPacket;
import redstonetweaks.world.common.IIncompleteAction;
import redstonetweaks.world.common.IIncompleteActionScheduler;

public class ServerIncompleteActionScheduler implements IIncompleteActionScheduler {
	
	private final ServerWorld world;
	private final ObjectLinkedOpenHashSet<IIncompleteAction> incompleteActions;
	
	public ServerIncompleteActionScheduler(ServerWorld world) {
		this.world = world;
		this.incompleteActions = new ObjectLinkedOpenHashSet<>();
	}
	
	@Override
	public void scheduleAction(IIncompleteAction action) {
		incompleteActions.add(action);
	}
	
	public boolean hasScheduledActions() {
		return !incompleteActions.isEmpty();
	}
	
	public void tick() {
		ServerNeighborUpdateScheduler neighborUpdateScheduler = ((RTIServerWorld)world).getNeighborUpdateScheduler();
		
		while (!neighborUpdateScheduler.hasScheduledUpdates() && hasScheduledActions()) {
			IIncompleteAction action = incompleteActions.removeLast();
			
			if (action.tryContinue(world)) {
				syncClientIncompleteActionScheduler(action);
			}
		}
	}
	
	private void syncClientIncompleteActionScheduler(IIncompleteAction action) {
		AbstractRedstoneTweaksPacket packet = action.toPacket();
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)world.getServer()).getPacketHandler();
		
		if (action.getViewDistance() < 0) {
			packetHandler.sendPacketToDimension(packet, world.getRegistryKey());
		} else {
			packetHandler.sendPacketToAround(packet, world.getRegistryKey(), action.getPos(), action.getViewDistance());
		}
	}
}
