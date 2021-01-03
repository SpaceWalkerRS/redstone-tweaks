package redstonetweaks.setting;

import redstonetweaks.setting.types.GameModeToBooleanSetting;
import redstonetweaks.setting.types.IntegerSetting;

public class ServerConfig {
	
	public static final SettingsCategory SERVER_CONFIG = new SettingsCategory("Server Config");
	
	public static class Settings {
		
		private static final SettingsPack SETTINGS = new SettingsPack(SERVER_CONFIG, "Settings");
		
		public static final IntegerSetting EDIT_PERMISSION_LEVEL = new IntegerSetting(SETTINGS, "editPermissionLevel", "The permission level required to change settings.", 0, 2);
		public static final IntegerSetting LOCK_PERMISSION_LEVEL = new IntegerSetting(SETTINGS, "lockPermissionLevel", "The permission level required to lock settings.", 0, 2);
		public static final GameModeToBooleanSetting EDIT_GAME_MODES = new GameModeToBooleanSetting(SETTINGS, "editGameModes", "The game mode(s) required to change settings.");
		public static final GameModeToBooleanSetting LOCK_GAME_MODES = new GameModeToBooleanSetting(SETTINGS, "lockGameModes", "The game mode(s) required to lock settings.");
	}
	
	public static class Presets {
		
		private static final SettingsPack PRESETS = new SettingsPack(SERVER_CONFIG, "Presets");
		
		public static final IntegerSetting EDIT_PERMISSION_LEVEL = new IntegerSetting(PRESETS, "editPermissionLevel", "The permission level required to edit presets.", 0, 2);
		public static final GameModeToBooleanSetting EDIT_GAME_MODES = new GameModeToBooleanSetting(PRESETS, "editGameModes", "The game mode(s) required to edit presets.");
	}
	
	public static void init() {
		redstonetweaks.setting.Settings.register(SERVER_CONFIG);
		
		redstonetweaks.setting.Settings.register(Settings.SETTINGS);
		redstonetweaks.setting.Settings.register(Settings.EDIT_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(Settings.LOCK_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(Settings.EDIT_GAME_MODES);
		redstonetweaks.setting.Settings.register(Settings.LOCK_GAME_MODES);
		
		redstonetweaks.setting.Settings.register(Presets.PRESETS);
		redstonetweaks.setting.Settings.register(Presets.EDIT_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(Presets.EDIT_GAME_MODES);
	}
}
