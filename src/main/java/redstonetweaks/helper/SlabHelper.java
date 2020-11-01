package redstonetweaks.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.Direction;

public class SlabHelper {

	public static SlabType getOppositeType(SlabType type) {
		switch (type) {
		case TOP:
			return SlabType.BOTTOM;
		case BOTTOM:
			return SlabType.TOP;
		case DOUBLE:
			return SlabType.DOUBLE;
		default:
			throw new IllegalStateException("Unknown type: " + type);
		}
	}

	public static boolean isSlab(BlockState state) {
		return isSlab(state.getBlock());
	}
	
	public static boolean isSlab(Block block) {
		return (block instanceof SlabBlock);
	}
	
	public static SlabType getTypeFromDirection(Direction dir) {
		switch (dir) {
		case UP:
			return SlabType.TOP;
		case DOWN:
			return SlabType.BOTTOM;
		default:
			return null;
		}
	}
	
	public static Direction getDirectionFromType(SlabType type) {
		switch (type) {
		case BOTTOM:
			return Direction.DOWN;
		case TOP:
			return Direction.UP;
		default:
			return null;
		}
	}
}
