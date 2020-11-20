package redstonetweaks.setting;

import java.util.ArrayList;

import redstonetweaks.setting.types.IntegerSetting;

public class ServerConfig {
	
	public static final String CATEGORY_NAME = "Server Config";
	public static final SettingsCategory SERVER_CONFIG = new SettingsCategory(CATEGORY_NAME, new ArrayList<>(), new ArrayList<>());
	
	public static class Tweaks {
		
		private static final String PACK_NAME = "Tweaks";
		private static final SettingsPack TWEAKS = new SettingsPack(PACK_NAME, new ArrayList<>());
		
		public static final IntegerSetting EDIT_PERMISSION_LEVEL = new IntegerSetting("editPermissionLevel", "The permission level required to change settings.", 2, 0, 4);
		public static final IntegerSetting LOCK_PERMISSION_LEVEL = new IntegerSetting("lockPermissionLevel", "The permission level required to lock settings.", 2, 0, 4);
	}
	
	public static void init() {
		Settings.registerCategory(SERVER_CONFIG);
		
		Settings.registerPack(SERVER_CONFIG, Tweaks.TWEAKS);
		Settings.register(SERVER_CONFIG, Tweaks.TWEAKS, Tweaks.EDIT_PERMISSION_LEVEL);
		Settings.register(SERVER_CONFIG, Tweaks.TWEAKS, Tweaks.LOCK_PERMISSION_LEVEL);
	}
}
