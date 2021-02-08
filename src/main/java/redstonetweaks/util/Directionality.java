package redstonetweaks.util;

public enum Directionality {
	
	NONE(0),
	ALL(1),
	HORIZONTAL(2),
	VERTICAL(3);
	
	public static final Directionality[] DIRECTIONALITIES;
	
	static {
		DIRECTIONALITIES = new Directionality[values().length];
		
		for (Directionality order : Directionality.values()) {
			DIRECTIONALITIES[order.index] = order;
		}
	}
	
	private final int index;
	
	private Directionality(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static Directionality fromIndex(int index) {
		if (index >= 0 && index < DIRECTIONALITIES.length) {
			return DIRECTIONALITIES[index];
		}
		
		return NONE;
	}
}
