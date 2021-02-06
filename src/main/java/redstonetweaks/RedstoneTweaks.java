package redstonetweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

import redstonetweaks.block.entity.BlockEntityTypes;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Presets;

public class RedstoneTweaks implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("Redstone Tweaks");
	
	public static final RedstoneTweaksVersion MOD_VERSION = RedstoneTweaksVersion.createSnapshot(0, 9, 0, 6);
	public static final RedstoneTweaksVersion PACKET_PROTOCOL = RedstoneTweaksVersion.createRelease(1, 0, 5);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = RedstoneTweaksVersion.createRelease(1, 2, 2);
	
	@Override
	public void onInitialize() {
		BlockEntityTypes.register();
		
		Settings.init();
		Presets.init();
		
		LOGGER.info(String.format("Initialized Redstone Tweaks %s", MOD_VERSION));
	}
}
