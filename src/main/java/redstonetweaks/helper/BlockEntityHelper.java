package redstonetweaks.helper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import redstonetweaks.RedstoneTweaks;

public class BlockEntityHelper {
	
	public static final Map<BlockEntityType<? extends BlockEntity>, Integer> IDS;
	
	public static int getId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		Integer id = IDS.get(blockEntityType);
		return id == null ? -1 : id;
	}
	
	public static boolean hasId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return IDS.containsKey(blockEntityType);
	}
	
	static {
		
		IDS = new HashMap<>();
		
		// 1-14 are used by some vanilla block entities
		// In case they add more, just make the numbers sufficiently large
		IDS.put(RedstoneTweaks.POWER_BLOCK_ENTITY_TYPE, 101);
		IDS.put(BlockEntityType.COMPARATOR, 102);
		
	}
}
