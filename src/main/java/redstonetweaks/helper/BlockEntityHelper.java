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
			put(RedstoneTweaks.REDSTONE_POWER, 101);
			put(BlockEntityType.COMPARATOR, 102);
		}
	};
	
	public static final int getId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return IDS.get(blockEntityType);
	}
	
	public static final boolean hasId(BlockEntityType<? extends BlockEntity> blockEntityType) {
		return IDS.containsKey(blockEntityType);
	}
}
