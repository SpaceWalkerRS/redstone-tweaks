package redstonetweaks.setting.settings;

import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.BooleanSetting;

public class ServerConfig {
	
	public static final SettingsCategory SERVER_CONFIG = new SettingsCategory("Server Config", true);
	
	public static class Permissions {
		
		private static final SettingsPack PERMISSIONS = new SettingsPack(SERVER_CONFIG, "Permissions");
		
		public static final BooleanSetting EDIT_SETTINGS = new BooleanSetting(PERMISSIONS, "editSettings", "Allow non-OP players to change settings.");
		public static final BooleanSetting EDIT_PRESETS = new BooleanSetting(PERMISSIONS, "editPresets", "Allow non-OP players to edit presets.");
		public static final BooleanSetting TICK_COMMAND = new BooleanSetting(PERMISSIONS, "tickCommand", "Allow non-OP players to use the tick command and its hotkeys.");
	}
	
	public static void init() {
		Settings.register(SERVER_CONFIG);
		
		Settings.register(Permissions.PERMISSIONS);
		Settings.register(Permissions.EDIT_SETTINGS);
		Settings.register(Permissions.EDIT_PRESETS);
		Settings.register(Permissions.TICK_COMMAND);
	}
}
