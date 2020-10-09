package redstonetweaks.setting.types;

import redstonetweaks.setting.Settings;

public class BugFixSetting extends BooleanSetting {
	
	public BugFixSetting(String name, String description) {
		super(Settings.BugFixes.ID, name, description, false);
	}
	
	public String getBugReportURL() {
		return "https://bugs.mojang.com/browse/" + getName();
	}
}
