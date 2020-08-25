package redstonetweaks.helper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DirectionHelper {
	
	// Return the direction of pos2 relative to pos1 if they are direct neighbors
	public static Direction getFromPositions(BlockPos pos1, BlockPos pos2) {
		for (Direction dir : Direction.values()) {
			if (pos1.offset(dir).equals(pos2)) {
				return dir;
			}
		}
		return null;
	}
}
