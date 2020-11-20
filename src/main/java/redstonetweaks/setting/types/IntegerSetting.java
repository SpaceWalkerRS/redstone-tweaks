package redstonetweaks.setting.types;

public class IntegerSetting extends Setting<Integer> {
	
	private int minValue;
	private int maxValue;
	
	public IntegerSetting(String name, String description, int defaultValue, int minValue, int maxValue) {
		super(name, description, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		// we need to initialize the value here because the min/max values are not yet
		// initialized when the super class tries to initialize the value.
		set(getDefault());
	}
	
	@Override
	public void setValueFromString(String string) {
		try {
			set(Integer.parseInt(string));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void set(Integer newValue) {
		if (newValue >= getMin() && newValue <= getMax()) {
			super.set(newValue);
		};
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
}
