package redstonetweaks.block.capacitor;

import net.minecraft.network.PacketByteBuf;

public class CapacitorBehavior {
	
	private Mode mode;
	private int stepSize;
	private int incrementDelay;
	private int decrementDelay;
	
	public CapacitorBehavior() {
		this.mode = Mode.DISABLED;
		this.stepSize = 1;
		this.incrementDelay = 2;
		this.decrementDelay = 2;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof CapacitorBehavior) {
			CapacitorBehavior behavior = (CapacitorBehavior)other;
			
			return mode == behavior.mode && stepSize == behavior.stepSize && incrementDelay == behavior.incrementDelay && decrementDelay == behavior.decrementDelay;
		}
		
		return false;
	}
	
	public CapacitorBehavior copy() {
		CapacitorBehavior copy = new CapacitorBehavior();
		
		copy.mode = mode;
		copy.stepSize = stepSize;
		copy.incrementDelay = incrementDelay;
		copy.decrementDelay = decrementDelay;
		
		return copy;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public boolean isEnabled() {
		return mode != Mode.DISABLED;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public void cycleMode(boolean next) {
		mode = next ? mode.next() : mode.previous();
	}
	
	public int getStepSize() {
		return stepSize;
	}
	
	public void setStepSize(int stepSize) {
		if (stepSize > 0) {
			this.stepSize = stepSize;
		}
	}
	
	public int getIncrementDelay() {
		return incrementDelay;
	}
	
	public void setIncrementDelay(int incrementDelay) {
		this.incrementDelay = incrementDelay;
	}
	
	public int getDecrementDelay() {
		return decrementDelay;
	}
	
	public void setDecrementDelay(int decrementDelay) {
		this.decrementDelay = decrementDelay;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(mode.getIndex());
		buffer.writeInt(stepSize);
		buffer.writeInt(incrementDelay);
		buffer.writeInt(decrementDelay);
	}
	
	public void decode(PacketByteBuf buffer) {
		mode = Mode.fromIndex(buffer.readByte());
		stepSize = buffer.readInt();
		incrementDelay = buffer.readInt();
		decrementDelay = buffer.readInt();
	}
	
	public enum Mode {
		
		DISABLED(0),
		MATCH_INPUT(1),
		CHARGE_INDEFINITELY(2);
		
		private static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		
		private Mode(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Mode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			
			return MODES[index];
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
		
		public Mode previous() {
			return fromIndex(index - 1);
		}
	}
}
