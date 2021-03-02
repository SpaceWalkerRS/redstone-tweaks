package redstonetweaks.block.entity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypes {
	
	// These ids are used when sending block entity update packets
	private static final Map<BlockEntityType<? extends BlockEntity>, Integer> IDS = new HashMap<>();
	
	public static BlockEntityType<PowerBlockEntity> POWER_BLOCK;
	
	public static void register() {
		POWER_BLOCK = Registry.register(Registry.BLOCK_ENTITY_TYPE, "redstonetweaks", BlockEntityType.Builder.create(() -> new PowerBlockEntity(), Blocks.REDSTONE_WIRE, Blocks.TARGET, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.REDSTONE_ORE).build(null));
		
		// 1-14 are used by some vanilla block entities
		// In case they add more, just make the numbers sufficiently large
		IDS.put(BlockEntityType.COMPARATOR, 101);
		IDS.put(BlockEntityTypes.POWER_BLOCK, 102);
		IDS.put(BlockEntityType.DAYLIGHT_DETECTOR, 103);
	}
	
	public static int getId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return IDS.getOrDefault(blockEntityType, -1);
	}
}
