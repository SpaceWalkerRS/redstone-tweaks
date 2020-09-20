package redstonetweaks.settings;

import java.util.List;

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
	
	public ISetting getSettingFromName(String name) {
		for (ISetting setting : getSettings()) {
			if (setting.getName() == name) {
				return setting;
			}
		}
		
		return null;
	}
}
