package redstonetweaks.setting;

import java.util.List;

import redstonetweaks.setting.types.ISetting;

public class SettingsPack {
	
	private final String name;
	private final List<ISetting> settings;
	
	public SettingsPack(String name, List<ISetting> settings) {
		this.name = name;
		this.settings = settings;
	}
	
	public String getName() {
		return name;
	}
	
	public List<ISetting> getSettings() {
		return settings;
	}
}
