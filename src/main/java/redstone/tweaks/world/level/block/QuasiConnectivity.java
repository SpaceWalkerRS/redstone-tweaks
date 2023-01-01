package redstone.tweaks.world.level.block;

import java.util.EnumMap;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import redstone.tweaks.util.Directions;

public class QuasiConnectivity {

	private final EnumMap<Direction, Integer> range;

	public QuasiConnectivity(QuasiConnectivity other) {
		this();

		set(other);
	}

	public QuasiConnectivity(Direction... enabled) {
		this.range = new EnumMap<>(Direction.class);

		for (Direction dir : Directions.ALL) {
			this.range.put(dir, 0);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof QuasiConnectivity)) {
			return false;
		}

		QuasiConnectivity other = (QuasiConnectivity)obj;

		return range.equals(other.range);
	}

	public int getRange(Direction dir) {
		return range.get(dir);
	}

	public QuasiConnectivity setRange(Direction dir, int range) {
		if (range >= 0) {
			this.range.put(dir, range);
		}

		return this;
	}

	public void set(QuasiConnectivity other) {
		for (Direction dir : Directions.ALL) {
			range.put(dir, other.range.get(dir));
		}
	}

	public void encode(FriendlyByteBuf buffer) {
		for (Direction dir : Directions.ALL) {
			buffer.writeInt(getRange(dir));
		}
	}

	public QuasiConnectivity decode(FriendlyByteBuf buffer) {
		for (Direction dir : Directions.ALL) {
			setRange(dir, buffer.readInt());
		}

		return this;
	}
}
