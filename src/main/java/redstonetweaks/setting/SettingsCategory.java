package redstonetweaks.setting;

import java.util.LinkedHashSet;
import java.util.Set;

public class SettingsCategory {
	
	private final String name;
	private final Set<SettingsPack> packs;
	
	private boolean locked;
	
	public SettingsCategory(String name) {
		this.name = name;
		this.packs = new LinkedHashSet<>();
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
	
	public Set<SettingsPack> getPacks() {
		return packs;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		boolean changed = this.locked != locked;
		
		this.locked = locked;
		
		if (changed) {
			Settings.categoryLockedChanged(this);
		}
	}
	
	public boolean addPack(SettingsPack pack) {
		return packs.add(pack);
	}
	
	public boolean isDefault() {
		for (SettingsPack pack : packs) {
			if (!pack.isDefault()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void resetAll() {
		packs.forEach((pack) -> pack.resetAll());
	}
}
