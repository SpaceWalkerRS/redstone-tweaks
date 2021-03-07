package redstonetweaks.listeners;

import redstonetweaks.player.PermissionManager;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.world.common.UpdateOrder;

public class Listeners {
	
	public static void clear() {
		Settings.clearListeners();
		Presets.clearListeners();
		PermissionManager.clearListeners();
		UpdateOrder.clearListeners();
	}
}
