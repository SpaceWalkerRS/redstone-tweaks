package redstonetweaks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import redstonetweaks.block.AnaloguePowerComponentBlockEntity;

public class RedstoneTweaks implements ModInitializer {
	
	public static final RedstoneTweaksVersion MOD_VERSION = new RedstoneTweaksVersion(0, 9, 0);
	public static final RedstoneTweaksVersion PACKET_PROTOCOL = new RedstoneTweaksVersion(1, 0, 0);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = new RedstoneTweaksVersion(1, 1, 0);
	
	public static RedstoneTweaksVersion SERVER_VERSION = null;
	
	public static BlockEntityType<AnaloguePowerComponentBlockEntity> REDSTONE_POWER;
	
	@Override
	public void onInitialize() {
		REDSTONE_POWER = Registry.register(Registry.BLOCK_ENTITY_TYPE, "redstonetweaks", BlockEntityType.Builder.create(AnaloguePowerComponentBlockEntity::new, Blocks.REDSTONE_WIRE, Blocks.TARGET).build(null));
	}
}
