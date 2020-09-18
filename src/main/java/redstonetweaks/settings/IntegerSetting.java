package redstonetweaks.settings;

public class IntegerSetting extends Setting<Integer> {
	
	private int minValue;
	private int maxValue;
	
	public IntegerSetting(String name, String description, int defaultValue, int minValue, int maxValue) {
		super(name, description, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public void setFromText(String text) {
		try {
			int newValue = Integer.parseInt(text);
			set(newValue);
		} catch (Exception e) {
			
		}
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
}
