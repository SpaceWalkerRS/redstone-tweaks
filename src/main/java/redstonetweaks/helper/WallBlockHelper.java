package redstonetweaks.helper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public class WallBlockHelper {
	
	public static final Map<Direction, EnumProperty<WallShape>> DIRECTION_TO_PROPERTY = new HashMap<>();
	
	static {
		
		DIRECTION_TO_PROPERTY.put(Direction.EAST, WallBlock.EAST_SHAPE);
		DIRECTION_TO_PROPERTY.put(Direction.NORTH, WallBlock.NORTH_SHAPE);
		DIRECTION_TO_PROPERTY.put(Direction.SOUTH, WallBlock.SOUTH_SHAPE);
		DIRECTION_TO_PROPERTY.put(Direction.WEST, WallBlock.WEST_SHAPE);
	}
}
