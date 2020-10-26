package redstonetweaks.helper;

import java.util.Map;

import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;

public interface PistonHandlerHelper {

	public Map<BlockPos, SlabType> getSplitSlabTypes();
	
}
