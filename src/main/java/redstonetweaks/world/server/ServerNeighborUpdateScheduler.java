package redstonetweaks.world.server;

import java.util.TreeSet;

import net.minecraft.server.world.ServerWorld;

import redstonetweaks.interfaces.RTIMinecraftServer;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.packet.NeighborUpdateSchedulerPacket;
import redstonetweaks.packet.NeighborUpdateVisualizerPacket;
import redstonetweaks.packet.RedstoneTweaksPacket;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.world.common.NeighborUpdate;
import redstonetweaks.world.common.NeighborUpdateScheduler;

public class ServerNeighborUpdateScheduler extends NeighborUpdateScheduler {
	
	private final TreeSet<ScheduledNeighborUpdate> scheduledNeighborUpdates = new TreeSet<>();
	private final ServerWorld world;
	
	private long tickTime;
	
	private ScheduledNeighborUpdate currentUpdate;
	
	public ServerNeighborUpdateScheduler(ServerWorld world) {
		this.world = world;
		
		this.tickTime = 0L;
	}
	
	public void tick() {
		tickTime++;
		
		if (!Tweaks.Global.SHOW_NEIGHBOR_UPDATES.get()) {
			clearUpdates();
		} else if (scheduledNeighborUpdates.isEmpty()) {
			clearCurrentUpdate();
		} else {
			currentUpdate = scheduledNeighborUpdates.pollFirst();
			
			dispatchNeighborUpdate();
			syncNeighborUpdateVisualizer();
		}
	}
	
	public void resetTickTime() {
		tickTime = 0L;
	}
	
	public void clearUpdates() {
		while (!scheduledNeighborUpdates.isEmpty()) {
			currentUpdate = scheduledNeighborUpdates.pollFirst();
			dispatchNeighborUpdate();
		}
		clearCurrentUpdate();
	}
	
	private void dispatchNeighborUpdate() {
		((RTIWorld)world).dispatchNeighborUpdate(true, currentUpdate.getNeighborUpdate());
	}
	
	private void clearCurrentUpdate() {
		if (currentUpdate != null) {
			currentUpdate = null;
			
			syncNeighborUpdateVisualizer();
			syncClientNeighborUpdateScheduler();
		}
	}
	
	public void schedule(NeighborUpdate neighborUpdate) {
		boolean wasEmpty = !hasScheduledNeighborUpdates();
		long time = Tweaks.Global.SHOW_PROCESSING_ORDER.get() > 0 ? tickTime : world.getTime();
		
		scheduledNeighborUpdates.add(new ScheduledNeighborUpdate(time, neighborUpdate));
		
		if (wasEmpty) {
			syncClientNeighborUpdateScheduler();
		}
	}
	
	@Override
	public boolean hasScheduledNeighborUpdates() {
		return !scheduledNeighborUpdates.isEmpty();
	}
	
	private void syncNeighborUpdateVisualizer() {
		NeighborUpdateVisualizerPacket packet = new  NeighborUpdateVisualizerPacket(currentUpdate);
		sendPacket(packet);
	}
	
	private void syncClientNeighborUpdateScheduler() {
		NeighborUpdateSchedulerPacket packet = new NeighborUpdateSchedulerPacket(hasScheduledNeighborUpdates());
		sendPacket(packet);
	}
	
	private void sendPacket(RedstoneTweaksPacket packet) {
		((RTIMinecraftServer)world.getServer()).getPacketHandler().sendPacketToDimension(packet, world.getRegistryKey());
	}
}
