package redstonetweaks.world.common;

public enum UpdateType {
	NONE(-1, "none"),
	BLOCK_UPDATE(0, "Block Update"),
	COMPARATOR_UPDATE(1, "Comparator Update"),
	SHAPE_UPDATE(2, "Shape Update");
	
	private final int index;
	private final String name;
	
	UpdateType(int index, String name) {
		this.index = index;
		this.name = name;
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
	
	public String getName() {
		return name;
	}
}
