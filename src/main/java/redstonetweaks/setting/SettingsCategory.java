package redstonetweaks.setting;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsCategory {
	
	private final String name;
	private final Map<String, SettingsPack> packs;
	
	private boolean locked;
	
	public SettingsCategory(String name) {
		this.name = name;
		this.packs = new LinkedHashMap<>();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SettingsCategory) {
			return name.equals(((SettingsCategory)other).name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, SettingsPack> getPacks() {
		return packs;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void resetAll() {
		packs.forEach((packName, pack) -> pack.getSettings().forEach((settingName, setting) -> setting.reset()));
	}
}
