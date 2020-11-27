package redstonetweaks.world.server;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import redstonetweaks.util.UpdateType;

public class ScheduledNeighborUpdate_ implements Comparable<ScheduledNeighborUpdate_> {
	
	private static long idCounter = 0;
	
	public final BlockPos pos;
	public final BlockPos notifierPos;
	public final BlockPos sourcePos;
	public final Direction direction;
	public final int flags;
	public final int depth;
	public final UpdateType updateType;
	public final long time;
	public final long id;
	
	public ScheduledNeighborUpdate_(BlockPos pos, BlockPos notifierPos, BlockPos sourcePos, Direction direction, int flags, int depth, UpdateType updateType, long time) {
		this.pos = pos;
		this.notifierPos = notifierPos;
		this.sourcePos = sourcePos;
		this.direction = direction;
		this.flags = flags;
		this.depth = depth;
		this.updateType = updateType;
		this.time = time;
		this.id = idCounter++;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof ScheduledNeighborUpdate_) {
			ScheduledNeighborUpdate_ update = (ScheduledNeighborUpdate_)object;
			return update.time == time && update.updateType == updateType && update.id == id;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return (int)(time * 31 + id) * 31 + updateType.getIndex();
	}

	@Override
	public int compareTo(ScheduledNeighborUpdate_ other) {
		int t = Long.compare(other.time, time);
		return t == 0 ? Long.compare(id, other.id) : t;
	}
}
