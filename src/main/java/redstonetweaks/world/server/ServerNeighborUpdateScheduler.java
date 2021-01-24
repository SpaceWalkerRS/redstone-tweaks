package redstonetweaks.world.server;

import java.util.TreeSet;

import net.minecraft.server.world.ServerWorld;
import redstonetweaks.interfaces.mixin.RTIMinecraftServer;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.packet.types.NeighborUpdateSchedulerPacket;
import redstonetweaks.packet.types.NeighborUpdateVisualizerPacket;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.NeighborUpdate;
import redstonetweaks.world.common.INeighborUpdateScheduler;

public class ServerNeighborUpdateScheduler implements INeighborUpdateScheduler {
	
	private final TreeSet<ScheduledNeighborUpdate> scheduledNeighborUpdates = new TreeSet<>();
	private final ServerWorld world;
	
	private long tickTime;
	
	private ScheduledNeighborUpdate currentScheduledUpdate;
	
	public ServerNeighborUpdateScheduler(ServerWorld world) {
		this.world = world;
		
		this.tickTime = 0L;
	}
	
	@Override
	public boolean hasScheduledNeighborUpdates() {
		return !scheduledNeighborUpdates.isEmpty();
	}
	
	public void tick() {
		tickTime++;
		
		if (!Tweaks.Global.SHOW_NEIGHBOR_UPDATES.get()) {
			clearUpdates();
		} else if (scheduledNeighborUpdates.isEmpty()) {
			clearCurrentUpdate();
		} else {
			currentScheduledUpdate = scheduledNeighborUpdates.pollFirst();
			
			dispatchNeighborUpdate();
			syncNeighborUpdateVisualizer();
		}
	}
	
	public void resetTickTime() {
		tickTime = 0L;
	}
	
	public void clearUpdates() {
		while (hasScheduledNeighborUpdates()) {
			currentScheduledUpdate = scheduledNeighborUpdates.pollFirst();
			dispatchNeighborUpdate();
		}
		clearCurrentUpdate();
	}
	
	private void dispatchNeighborUpdate() {
		((RTIWorld)world).dispatchNeighborUpdate(true, currentScheduledUpdate.getNeighborUpdate());
	}
	
	private void clearCurrentUpdate() {
		if (currentScheduledUpdate != null) {
			currentScheduledUpdate = null;
			
			syncNeighborUpdateVisualizer();
			syncClientNeighborUpdateScheduler();
		}
	}
	
	public void schedule(NeighborUpdate neighborUpdate) {
		boolean hadScheduledUpdates = hasScheduledNeighborUpdates();
		
		scheduledNeighborUpdates.add(new ScheduledNeighborUpdate(tickTime, neighborUpdate));
		
		if (!hadScheduledUpdates) {
			syncClientNeighborUpdateScheduler();
		}
	}
	
	private void syncNeighborUpdateVisualizer() {
		NeighborUpdateVisualizerPacket packet = new  NeighborUpdateVisualizerPacket(currentScheduledUpdate);
		((RTIMinecraftServer)world.getServer()).getPacketHandler().sendPacketToDimension(packet, world.getRegistryKey());
	}
	
	private void syncClientNeighborUpdateScheduler() {
		NeighborUpdateSchedulerPacket packet = new NeighborUpdateSchedulerPacket(hasScheduledNeighborUpdates());
		((RTIMinecraftServer)world.getServer()).getPacketHandler().sendPacketToDimension(packet, world.getRegistryKey());
	}
}
