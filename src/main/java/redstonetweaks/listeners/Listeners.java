package redstonetweaks.listeners;

import redstonetweaks.client.PermissionManager;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Presets;

public class Listeners {
	
	public static void clear() {
		Settings.clearListeners();
		Presets.clearListeners();
		PermissionManager.clearListeners();
	}
}
