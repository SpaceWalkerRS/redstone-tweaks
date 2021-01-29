package redstonetweaks.setting;

import java.util.LinkedHashSet;
import java.util.Set;

import redstonetweaks.setting.types.ISetting;

public class SettingsPack {
	
	private final SettingsCategory category;
	private final String id;
	private final String name;
	private final Set<ISetting> settings;
	
	private boolean locked;
	
	public SettingsPack(SettingsCategory category, String name) {
		this.category = category;
		this.id = String.format("%s/%s", category.getName(), name);
		this.name = name;
		this.settings = new LinkedHashSet<>();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof SettingsPack) {
			return id.equals(((SettingsPack)other).id);
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
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<ISetting> getSettings() {
		return settings;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		boolean changed = this.locked != locked;
		
		this.locked = locked;
		
		if (changed) {
			Settings.packLockedChanged(this);
		}
	}
	
	public boolean addSetting(ISetting setting) {
		return settings.add(setting);
	}
	
	public boolean isDefault() {
		for (ISetting setting : settings) {
			if (!setting.isDefault()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void resetAll() {
		settings.forEach((setting) -> setting.reset());
	}
}
