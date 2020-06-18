package redstonetweaks;

import net.fabricmc.api.ModInitializer;

import redstonetweaks.setting.Settings;

public class RedstoneTweaks implements ModInitializer {
	
	public static RedstoneTweaks instance;
	
	@Override
	public void onInitialize() {
		instance = this;
		
		Settings.registerSettings();
	}
	
	public static RedstoneTweaks getInstance() {
		return instance;
	}
}
