package redstonetweaks.world.server;

import redstonetweaks.world.common.NeighborUpdate;

public class ScheduledNeighborUpdate implements Comparable<ScheduledNeighborUpdate> {
	
	private static long idCounter = 0;
	
	private final long id;
	private final long time;
	private final NeighborUpdate neighborUpdate;
	
	public ScheduledNeighborUpdate(long time, NeighborUpdate neighborUpdate) {
		this.id = idCounter++;
		this.time = time;
		this.neighborUpdate = neighborUpdate;
	}
	
	@Override
	public int hashCode() {
		return (int)(time * 31 + id);
	}
	
	@Override
	public int compareTo(ScheduledNeighborUpdate other) {
		int t = Long.compare(other.time, time);
		
		return t == 0 ? Long.compare(id, other.id) : t;
	}
	
	public long getTime() {
		return time;
	}
	
	public NeighborUpdate getNeighborUpdate() {
		return neighborUpdate;
	}
}
