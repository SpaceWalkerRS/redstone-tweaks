package redstonetweaks.setting;

import redstonetweaks.setting.types.GameModeToBooleanSetting;
import redstonetweaks.setting.types.IntegerSetting;

public class ServerConfig {
	
	private static final String CATEGORY_NAME = "Server Config";
	public static final SettingsCategory SERVER_CONFIG = new SettingsCategory(CATEGORY_NAME);
	
	public static class Settings {
		
		private static final String PACK_NAME = "Settings";
		private static final SettingsPack SETTINGS = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting EDIT_PERMISSION_LEVEL = new IntegerSetting("editPermissionLevel", "The permission level required to change settings.", 0, 2);
		public static final IntegerSetting LOCK_PERMISSION_LEVEL = new IntegerSetting("lockPermissionLevel", "The permission level required to lock settings.", 0, 2);
		public static final GameModeToBooleanSetting EDIT_GAME_MODES = new GameModeToBooleanSetting("editGameModes", "The game mode(s) required to change settings.");
		public static final GameModeToBooleanSetting LOCK_GAME_MODES = new GameModeToBooleanSetting("lockGameModes", "The game mode(s) required to lock settings.");
	}
	
	public static class Presets {
		
		private static final String PACK_NAME = "Presets";
		private static final SettingsPack PRESETS = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting EDIT_PERMISSION_LEVEL = new IntegerSetting("editPermissionLevel", "The permission level required to edit presets.", 0, 2);
		public static final GameModeToBooleanSetting EDIT_GAME_MODES = new GameModeToBooleanSetting("editGameModes", "The game mode(s) required to edit presets.");
	}
	
	public static void init() {
		redstonetweaks.setting.Settings.registerCategory(SERVER_CONFIG);
		
		redstonetweaks.setting.Settings.registerPack(SERVER_CONFIG, Settings.SETTINGS);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Settings.SETTINGS, Settings.EDIT_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Settings.SETTINGS, Settings.LOCK_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Settings.SETTINGS, Settings.EDIT_GAME_MODES);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Settings.SETTINGS, Settings.LOCK_GAME_MODES);
		
		redstonetweaks.setting.Settings.registerPack(SERVER_CONFIG, Presets.PRESETS);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Presets.PRESETS, Presets.EDIT_PERMISSION_LEVEL);
		redstonetweaks.setting.Settings.register(SERVER_CONFIG, Presets.PRESETS, Presets.EDIT_GAME_MODES);
	}
}
