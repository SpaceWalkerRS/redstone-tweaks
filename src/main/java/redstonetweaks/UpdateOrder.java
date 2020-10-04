package redstonetweaks;

import java.util.List;

public class UpdateOrder {
	
	private Mode mode;
	private List<BlockUpdate> blockUpdates;
	
	public UpdateOrder(Mode defaultMode) {
		this.mode = defaultMode;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public List<BlockUpdate> getBlockUpdates() {
		return blockUpdates;
	}
	
	public void insert(int index) {
		if (index >= 0 && index < blockUpdates.size()) {
			
		}
	}
	
	public void addNew(int index) {
		add(index, new BlockUpdate());
	}
	
	public void add(int index, BlockUpdate update) {
		
	}
	
	public void remove(int index) {
		if (index >= 0 && index < blockUpdates.size()) {
			blockUpdates.remove(index);
		}
	}
	
	public enum Mode {
		DIRECTIONAL(0, "Directional"),
		LOCATIONAL(1, "Locational"),
		RANDOM(2, "Random");
		
		public static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : Mode.values()) {
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
		
		public String getName() {
			return name;
		}
		
		public Mode next() {
			int nextIndex = index + 1;
			if (nextIndex >= MODES.length) {
				nextIndex = 0;
			}
			
			return MODES[nextIndex];
		}
	}
}
