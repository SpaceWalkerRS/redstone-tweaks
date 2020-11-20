package redstonetweaks.helper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import redstonetweaks.RedstoneTweaks;

public class BlockEntityHelper {
	
	// 1-14 are used by some vanilla block entities
	// In case they add more, just make the numbers sufficiently large
	@SuppressWarnings("serial")
	public static final Map<BlockEntityType<? extends BlockEntity>, Integer> IDS = new HashMap<BlockEntityType<? extends BlockEntity>, Integer>() {
		{
			put(RedstoneTweaks.POWER_BLOCK_ENTITY_TYPE, 101);
			put(BlockEntityType.COMPARATOR, 102);
		}
	};
	
	public static int getId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		Integer id = IDS.get(blockEntityType);
		return id == null ? -1 : id;
	}
	
	public static boolean hasId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return IDS.containsKey(blockEntityType);
	}
}
