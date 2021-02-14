package redstonetweaks.listeners;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;

public class Listeners {
	
	public static void clear() {
		Settings.clearListeners();
		Presets.clearListeners();
		PermissionManager.clearListeners();
	}
}
