package redstonetweaks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import redstonetweaks.block.redstonewire.RedstoneWireBlockEntity;

public class RedstoneTweaks implements ModInitializer {
	
	public static final RedstoneTweaksVersion MOD_VERSION = new RedstoneTweaksVersion(0, 8, 0);
	public static final RedstoneTweaksVersion SETTINGS_VERSION = new RedstoneTweaksVersion(1, 1, 0);
	
	public static BlockEntityType<RedstoneWireBlockEntity> REDSTONE_WIRE;
	
	private static RedstoneTweaks instance;
	
	@Override
	public void onInitialize() {
		instance = this;
		
		REDSTONE_WIRE = Registry.register(Registry.BLOCK_ENTITY_TYPE, "redstonetweaks", BlockEntityType.Builder.create(RedstoneWireBlockEntity::new, Blocks.REDSTONE_WIRE).build(null));
	}
	
	public static RedstoneTweaks getInstance() {
		return instance;
	}
}
