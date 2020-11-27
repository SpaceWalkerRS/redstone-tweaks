package redstonetweaks.setting.types;

public class BugFixSetting extends BooleanSetting {
	
	public BugFixSetting(String name, String description) {
		super(name, description);
	}
	
	public String getBugReportURL() {
		return "https://bugs.mojang.com/browse/" + getName();
	}
}
