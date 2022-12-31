package redstone.tweaks.world.level.block;

import java.util.EnumMap;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import redstone.tweaks.util.Directions;

public class QuasiConnectivity {

	private final EnumMap<Direction, Boolean> enabled;

	public QuasiConnectivity(QuasiConnectivity other) {
		this();

		set(other);
	}

	public QuasiConnectivity(Direction... enabled) {
		this();

		for (Direction dir : enabled) {
			this.enabled.put(dir, true);
		}
	}

	private QuasiConnectivity() {
		this.enabled = new EnumMap<>(Direction.class);

		for (Direction dir : Directions.ALL) {
			this.enabled.put(dir, false);
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

		return enabled.equals(other.enabled);
	}

	public boolean isEnabled(Direction dir) {
		return enabled.get(dir);
	}

	public void setEnabled(Direction dir, boolean enabled) {
		this.enabled.put(dir, enabled);
	}

	public void set(QuasiConnectivity other) {
		for (Direction dir : Directions.ALL) {
			enabled.put(dir, other.enabled.get(dir));
		}
	}

	public void encode(FriendlyByteBuf buffer) {
		byte flags = 0;

		for (Direction dir : Directions.ALL) {
			if (isEnabled(dir)) {
				flags |= 1 << dir.get3DDataValue();
			}
		}

		buffer.writeByte(flags);
	}

	public QuasiConnectivity decode(FriendlyByteBuf buffer) {
		byte flags = buffer.readByte();

		for (Direction dir : Directions.ALL) {
			if ((flags & (1 << dir.get3DDataValue())) != 0) {
				setEnabled(dir, true);
			}
		}

		return this;
	}
}
