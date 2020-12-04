package redstonetweaks.setting;

import java.util.ArrayList;
import java.util.List;

import redstonetweaks.setting.types.ISetting;

public class SettingsCategory {
	
	private final String name;
	private final List<ISetting> settings;
	private final List<SettingsPack> settingsPacks;
	
	private boolean locked;
	
	public SettingsCategory(String name) {
		this(name, new ArrayList<>(), new ArrayList<>());
	}
	
	public SettingsCategory(String name, List<ISetting> settings, List<SettingsPack> packs) {
		this.name = name;
		this.settings = settings;
		this.settingsPacks = packs;
	}
	
	public String getName() {
		return name;
	}
	
	public List<ISetting> getSettings() {
		return settings;
	}
	
	public List<SettingsPack> getSettingsPacks() {
		return settingsPacks;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void resetAll() {
		settings.forEach((setting) -> setting.reset());
	}
}
