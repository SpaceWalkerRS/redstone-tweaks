package redstonetweaks.setting.types;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;

public class IntegerSetting extends Setting<Integer> {
	
	private int minValue;
	private int maxValue;
	
	public IntegerSetting(SettingsPack pack, String name, String description, int minValue, int maxValue) {
		super(pack, name, description, 0);
		
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public Integer stringToValue(String string) {
		return Integer.parseInt(string);
	}
	
	@Override
	public void set(Integer newValue) {
		if (inRange(newValue)) {
			super.set(newValue);
		}
	}
	
	@Override
	public void setPresetValue(Preset preset, Integer newValue) {
		if (inRange(newValue)) {
			super.setPresetValue(preset, newValue);
		}
	}
	
	public int getMin() {
		return minValue;
	}
	
	public int getMax() {
		return maxValue;
	}
	
	public boolean inRange(int value) {
		return value >= getMin() && value <= getMax();
	}
	
	public int getRange() {
		return getMax() - getMin();
	}
}
