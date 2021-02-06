package redstonetweaks.listeners;

import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.ISetting;

public interface ISettingListener {
	
	public void categoryLockedChanged(SettingsCategory category);
	
	public void packLockedChanged(SettingsPack pack);
	
	public void settingLockedChanged(ISetting setting);
	
	public void settingValueChanged(ISetting setting);
	
}
