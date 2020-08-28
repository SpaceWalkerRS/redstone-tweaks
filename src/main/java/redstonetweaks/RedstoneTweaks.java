package redstonetweaks;

import net.fabricmc.api.ModInitializer;

public class RedstoneTweaks implements ModInitializer {
	
	public static final RedstoneTweaksVersion VERSION = new RedstoneTweaksVersion(0, 6, 2);
	
	private static RedstoneTweaks instance;
	
	@Override
	public void onInitialize() {
		instance = this;
	}
	
	public static RedstoneTweaks getInstance() {
		return instance;
	}
}
