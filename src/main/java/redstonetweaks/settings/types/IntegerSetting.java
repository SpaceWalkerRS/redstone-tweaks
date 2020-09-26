package redstonetweaks.settings.types;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.gui.SettingsListWidget.Entry;
import redstonetweaks.gui.setting.IntegerSettingGUIEntry;

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
			int newValue = Integer.parseInt(text);
			
			if (newValue >= getMin() && newValue <= getMax()) {
				set(newValue);
			}
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public Entry createGUIEntry(MinecraftClient client) {
		return new IntegerSettingGUIEntry(client, this);
	}
	
	@Override
	public void set(Integer newValue) {
		setPrev();
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
