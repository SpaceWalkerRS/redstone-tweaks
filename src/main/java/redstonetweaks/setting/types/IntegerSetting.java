package redstonetweaks.setting.types;

public class IntegerSetting extends Setting<Integer> {
	
	private int minValue;
	private int maxValue;
	
	public IntegerSetting(String prefix, String name, String description, int defaultValue, int minValue, int maxValue) {
		super(prefix, name, description, defaultValue);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		super.set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			set(Integer.parseInt(text));
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
