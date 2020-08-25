package redstonetweaks.setting;

public class IntegerProperty extends Property<Integer> {
	
	private final int minValue;
	private final int maxValue;
	
	public IntegerProperty(int defaultValue, int minValue, int maxValue) {
		super(defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
	
	protected String[] generateCommandSuggestions() {
		return new String[] {getDefault().toString()};
	}
}