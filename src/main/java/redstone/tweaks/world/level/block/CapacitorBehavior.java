package redstone.tweaks.world.level.block;

import net.minecraft.network.FriendlyByteBuf;

public class CapacitorBehavior {

	private Mode mode;
	private int step;
	private int chargeRate;
	private int dischargeRate;

	public CapacitorBehavior() {
		this.mode = Mode.DISABLED;
		this.step = 1;
		this.chargeRate = 2;
		this.dischargeRate = 2;
	}

	public CapacitorBehavior(CapacitorBehavior other) {
		this();

		set(other);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CapacitorBehavior)) {
			return false;
		}

		CapacitorBehavior other = (CapacitorBehavior)obj;

		return mode == other.mode && step == other.step && chargeRate == other.chargeRate && dischargeRate == other.dischargeRate;
	}

	public Mode getMode() {
		return mode;
	}

	public boolean isEnabled() {
		return mode != Mode.DISABLED;
	}

	public void setMode(Mode mode) {
		if (mode != null) {
			this.mode = mode;
		}
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		if (step > 0) {
			this.step = step;
		}
	}

	public int getChargeRate() {
		return chargeRate;
	}

	public void setChargeRate(int rate) {
		if (rate >= 0) {
			this.chargeRate = rate;
		}
	}

	public int getDischargeRate() {
		return dischargeRate;
	}

	public void setDischargeRate(int rate) {
		if (rate >= 0) {
			this.dischargeRate = rate;
		}
	}

	public void set(CapacitorBehavior other) {
		setMode(other.mode);
		setStep(other.step);
		setChargeRate(other.chargeRate);
		setDischargeRate(other.dischargeRate);
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeByte(mode.index);
		buffer.writeInt(step);
		buffer.writeInt(chargeRate);
		buffer.writeInt(dischargeRate);
	}

	public CapacitorBehavior decode(FriendlyByteBuf buffer) {
		setMode(Mode.byIndex(buffer.readByte()));
		setStep(buffer.readInt());
		setChargeRate(buffer.readInt());
		setDischargeRate(buffer.readInt());

		return this;
	}

	public static enum Mode {

		DISABLED(0),
		MATCH_INPUT(1),
		CHARGE_INDEFINITELY(2);

		private static final Mode[] ALL;

		static {

			Mode[] modes = values();
			ALL = new Mode[modes.length];

			for (Mode mode : modes) {
				ALL[mode.index] = mode;
			}
		}

		private final int index;

		private Mode(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public static Mode byIndex(int index) {
			if (index < 0) {
				index = ALL.length - 1;
			}
			if (index >= ALL.length) {
				index = 0;
			}

			return ALL[index];
		}
	}
}
