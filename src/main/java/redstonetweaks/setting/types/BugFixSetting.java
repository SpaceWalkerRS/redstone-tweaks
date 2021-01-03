package redstonetweaks.setting.types;

import redstonetweaks.setting.SettingsPack;

public class BugFixSetting extends BooleanSetting {
	
	public BugFixSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	public String getBugReportURL() {
		return "https://bugs.mojang.com/browse/" + getName();
	}
}
