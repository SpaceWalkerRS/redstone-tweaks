package redstonetweaks.setting;

import java.util.LinkedHashMap;
import java.util.Map;

import redstonetweaks.setting.types.ISetting;

public class SettingsPack {
	
	private final SettingsCategory category;
	private final String name;
	private final Map<String, ISetting> settings;
	
	private boolean locked;
	
	public SettingsPack(SettingsCategory category, String name) {
		this.category = category;
		this.name = name;
		this.settings = new LinkedHashMap<>();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SettingsPack) {
			return category.equals(((SettingsPack)other).category) && name.equals(((SettingsPack)other).name);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return category.hashCode() * 31 + name.hashCode();
	}
	
	public SettingsCategory getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, ISetting> getSettings() {
		return settings;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
