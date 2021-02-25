package redstonetweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import redstonetweaks.block.entity.BlockEntityTypes;
import redstonetweaks.setting.settings.Settings;

public class RedstoneTweaks implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("Redstone Tweaks");
	
	public static final RedstoneTweaksVersion MOD_VERSION = RedstoneTweaksVersion.createRelease(0, 9, 5);
	
	@Override
	public void onInitialize() {
		BlockEntityTypes.register();
		
		Settings.init();
		
		LOGGER.info(String.format("Initialized Redstone Tweaks %s", MOD_VERSION));
	}
}
