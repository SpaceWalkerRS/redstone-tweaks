package redstonetweaks;

import net.fabricmc.api.ModInitializer;

public class RedstoneTweaks implements ModInitializer {
	
	public static final RedstoneTweaksVersion MOD_VERSION = new RedstoneTweaksVersion(0, 7, 1);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = new RedstoneTweaksVersion(1, 0, 0);
	
	private static RedstoneTweaks instance;
	
	@Override
	public void onInitialize() {
		instance = this;
	}
	
	public static RedstoneTweaks getInstance() {
		return instance;
	}
}
