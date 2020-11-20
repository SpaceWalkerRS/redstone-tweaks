package redstonetweaks.world.server;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ScheduledNeighborUpdate implements Comparable<ScheduledNeighborUpdate> {
	
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
	
	public ScheduledNeighborUpdate(BlockPos pos, BlockPos notifierPos, BlockPos sourcePos, Direction direction, int flags, int depth, UpdateType updateType, long time) {
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
		if (object instanceof ScheduledNeighborUpdate) {
			ScheduledNeighborUpdate update = (ScheduledNeighborUpdate)object;
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
	public int compareTo(ScheduledNeighborUpdate other) {
		int t = Long.compare(other.time, time);
		return t == 0 ? Long.compare(id, other.id) : t;
	}
	
	public enum UpdateType {
		NONE(-1),
		BLOCK_UPDATE(0),
		COMPARATOR_UPDATE(1),
		SHAPE_UPDATE(2);

		private final int index;

		UpdateType(int index) {
			this.index = index;
		}

		public static UpdateType fromIndex(int index) {
			switch (index) {
			case 0:
				return BLOCK_UPDATE;
			case 1:
				return COMPARATOR_UPDATE;
			case 2:
				return SHAPE_UPDATE;
			default:
				return NONE;
			}
		}

		public int getIndex() {
			return index;
		}
	}
}
