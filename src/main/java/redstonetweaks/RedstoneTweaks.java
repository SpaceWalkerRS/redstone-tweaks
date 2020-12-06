package redstonetweaks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import redstonetweaks.block.PowerBlockEntity;
import redstonetweaks.setting.Settings;

public class RedstoneTweaks implements ModInitializer {
	
	public static final RedstoneTweaksVersion MOD_VERSION = RedstoneTweaksVersion.create(0, 9, 0);
	public static final RedstoneTweaksVersion PACKET_PROTOCOL = RedstoneTweaksVersion.create(1, 0, 1);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = RedstoneTweaksVersion.create(1, 2, 0);
	
	public static BlockEntityType<PowerBlockEntity> POWER_BLOCK_ENTITY_TYPE;
	
	@Override
	public void onInitialize() {
		POWER_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "redstonetweaks", BlockEntityType.Builder.create(PowerBlockEntity::new, Blocks.REDSTONE_WIRE, Blocks.TARGET, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE).build(null));
		
		Settings.init();
		
		System.out.println("Initialized Redstone Tweaks " + MOD_VERSION);
	}
}
