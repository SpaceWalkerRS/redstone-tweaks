package redstonetweaks.setting.types;

import redstonetweaks.setting.SettingsPack;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public Boolean getBackupValue() {
		return false;
	}
	
	@Override
	public Boolean stringToValue(String string) {
		return Boolean.parseBoolean(string);
	}
}
