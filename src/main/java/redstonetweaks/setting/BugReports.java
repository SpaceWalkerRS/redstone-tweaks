package redstonetweaks.setting;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class BugReports {
	
	public static final Map<Setting<?>, String> BUG_REPORTS = new LinkedHashMap<>();
	
	static {
		BUG_REPORTS.put(MC54711, "https://bugs.mojang.com/browse/MC-54711");
		BUG_REPORTS.put(MC120986, "https://bugs.mojang.com/browse/MC-120986");
		BUG_REPORTS.put(MC136566, "https://bugs.mojang.com/browse/MC-136566");
		BUG_REPORTS.put(MC137127, "https://bugs.mojang.com/browse/MC-137127");
		BUG_REPORTS.put(MC172213, "https://bugs.mojang.com/browse/MC-172213");
	}
}
