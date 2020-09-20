package redstonetweaks.settings;

public class BugFixSetting extends BooleanSetting {
	
	private final String bugReportURL;
	
	public BugFixSetting(String name, String description, Boolean defaultValue, String reportURL) {
		super(name, description, defaultValue);
		
		this.bugReportURL = reportURL;
	}
	
	public String getBugReportURL() {
		return bugReportURL;
	}
}
