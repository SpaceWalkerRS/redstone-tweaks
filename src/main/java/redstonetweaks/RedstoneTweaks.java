package redstonetweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import redstonetweaks.block.entity.PowerBlockEntity;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Presets;

public class RedstoneTweaks implements ModInitializer {
	
	public static final Logger LOGGER = LogManager.getLogger("Redstone Tweaks");
	
	public static final RedstoneTweaksVersion MOD_VERSION = RedstoneTweaksVersion.createSnapshot(0, 9, 0, 3);
	public static final RedstoneTweaksVersion PACKET_PROTOCOL = RedstoneTweaksVersion.createRelease(1, 0, 3);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = RedstoneTweaksVersion.createRelease(1, 2, 1);
	
	public static BlockEntityType<PowerBlockEntity> POWER_BLOCK_ENTITY_TYPE;
	
	@Override
	public void onInitialize() {
		POWER_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "redstonetweaks", BlockEntityType.Builder.create(() -> new PowerBlockEntity(), Blocks.REDSTONE_WIRE, Blocks.TARGET, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).build(null));
		
		Settings.init();
		Presets.init();
		
		LOGGER.info("Initialized Redstone Tweaks " + MOD_VERSION);
	}
}
