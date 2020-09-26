package redstonetweaks.settings;

import java.util.List;

import redstonetweaks.settings.types.ISetting;

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
