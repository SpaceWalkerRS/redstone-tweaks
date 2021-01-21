package redstonetweaks.world.server;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import redstonetweaks.mixinterfaces.RTIMinecraftServer;
import redstonetweaks.mixinterfaces.RTIServerWorld;
import redstonetweaks.packet.ServerPacketHandler;
import redstonetweaks.packet.types.RedstoneTweaksPacket;
import redstonetweaks.world.common.IIncompleteAction;
import redstonetweaks.world.common.IIncompleteActionScheduler;
import redstonetweaks.world.common.IncompleteBlockAction;

public class ServerIncompleteActionScheduler implements IIncompleteActionScheduler {
	
	private final ServerWorld world;
	private final ObjectLinkedOpenHashSet<IIncompleteAction> incompleteActions;
	
	public ServerIncompleteActionScheduler(ServerWorld world) {
		this.world = world;
		this.incompleteActions = new ObjectLinkedOpenHashSet<>();
	}
	
	@Override
	public boolean hasScheduledActions() {
		return !incompleteActions.isEmpty();
	}
	
	public void tick() {
		ServerNeighborUpdateScheduler neighborUpdateScheduler = ((RTIServerWorld)world).getNeighborUpdateScheduler();
		
		while (!neighborUpdateScheduler.hasScheduledNeighborUpdates() && hasScheduledActions()) {
			IIncompleteAction action = incompleteActions.removeLast();
			
			if (action.tryContinue(world)) {
				syncClientIncompleteActionScheduler(action);
			}
		}
	}
	
	public void scheduleBlockAction(BlockPos pos, int type, Block block) {
		scheduleBlockAction(pos, type, -1, block);
	}
	
	public void scheduleBlockAction(BlockPos pos, int type, double viewDistance, Block block) {
		scheduleAction(new IncompleteBlockAction(pos, type, viewDistance, block));
	}
	
	private void scheduleAction(IIncompleteAction action) {
		incompleteActions.add(action);
	}
	
	private void syncClientIncompleteActionScheduler(IIncompleteAction action) {
		RedstoneTweaksPacket packet = action.toPacket();
		ServerPacketHandler packetHandler = ((RTIMinecraftServer)world.getServer()).getPacketHandler();
		
		if (action.getViewDistance() < 0) {
			packetHandler.sendPacketToDimension(packet, world.getRegistryKey());
		} else {
			packetHandler.sendPacketToAround(packet, world.getRegistryKey(), action.getPos(), action.getViewDistance());
		}
	}
}
