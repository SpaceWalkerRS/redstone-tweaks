package redstonetweaks.world.common;

import net.minecraft.network.PacketByteBuf;

public class WorldTickOptions {
	
	private Mode mode;
	private DimensionFilter dimensionFilter;
	private int interval;
	
	public WorldTickOptions() {
		this(Mode.NORMAL, DimensionFilter.ALL, 1);
	}
	
	public WorldTickOptions(Mode mode, DimensionFilter filter, int interval) {
		this.mode = mode;
		this.dimensionFilter = filter;
		this.interval = interval;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof WorldTickOptions) {
			WorldTickOptions options = (WorldTickOptions)other;
			
			return options.mode == mode && options.dimensionFilter == dimensionFilter && options.interval == interval;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return mode + ";" + dimensionFilter + ";" + interval;
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeByte(mode.getIndex());
		buffer.writeByte(dimensionFilter.getIndex());
		buffer.writeInt(interval);
	}
	
	public void decode(PacketByteBuf buffer) {
		mode = Mode.fromIndex(buffer.readByte());
		dimensionFilter = DimensionFilter.fromIndex(buffer.readByte());
		interval = buffer.readInt();
	}
	
	public static WorldTickOptions parseWorldTickOptions(String string) {
		String[] args = string.split(";");
		
		WorldTickOptions options = new WorldTickOptions();
		
		options.setMode(Mode.valueOf(args[0]));
		options.setDimensionFilter(DimensionFilter.valueOf(args[1]));
		options.setInterval(Integer.parseInt(args[2]));
		
		return options;
	}
	
	public WorldTickOptions copy() {
		WorldTickOptions options = new WorldTickOptions();
		
		options.setMode(mode);
		options.setDimensionFilter(dimensionFilter);
		options.setInterval(interval);
		
		return options;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public void cycleMode() {
		setMode(getMode().next());
	}
	
	public DimensionFilter getDimensionFilter() {
		return dimensionFilter;
	}
	
	public void setDimensionFilter(DimensionFilter dimensionFilter) {
		this.dimensionFilter = dimensionFilter;
	}
	
	public void cycleDimensionFilter() {
		setDimensionFilter(getDimensionFilter().next());
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setInterval(int interval) {
		if (interval > 0) {
			this.interval = interval;
		}
	}
	
	public enum Mode {
		
		NORMAL(0, "Normal"),
		STEP_BY_STEP(1, "Step by step");
		
		public static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		private final String name;
		
		private Mode(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Mode fromIndex(int index) {
			if (index >= 0 && index < MODES.length) {
				return MODES[index];
			}
			return NORMAL;
		}
		
		public String getName() {
			return name;
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
	}
	
	public enum DimensionFilter {
		
		ALL(0, "All"),
		ACTIVE(1, "Active");
		
		public static final DimensionFilter[] FILTERS;
		
		static {
			FILTERS = new DimensionFilter[values().length];
			
			for (DimensionFilter filter : values()) {
				FILTERS[filter.index] = filter;
			}
		}
		
		private final int index;
		private final String name;
		
		private DimensionFilter(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static DimensionFilter fromIndex(int index) {
			if (index >= 0 && index < FILTERS.length) {
				return FILTERS[index];
			}
			return ALL;
		}
		
		public String getName() {
			return name;
		}
		
		public DimensionFilter next() {
			return fromIndex(index + 1);
		}
	}
}
