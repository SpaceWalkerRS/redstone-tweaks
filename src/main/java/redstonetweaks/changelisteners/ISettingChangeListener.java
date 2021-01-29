package redstonetweaks.changelisteners;

import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.ISetting;

public interface ISettingChangeListener {
	
	public void categoryLockedChanged(SettingsCategory category);
	
	public void packLockedChanged(SettingsPack pack);
	
	public void settingLockedChanged(ISetting setting);
	
	public void settingValueChanged(ISetting setting);
	
}
