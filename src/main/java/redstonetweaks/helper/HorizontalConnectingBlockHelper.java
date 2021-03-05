package redstonetweaks.helper;

import java.util.Map;

import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;

public abstract class HorizontalConnectingBlockHelper extends HorizontalConnectingBlock {
	
	public static final Map<Direction, BooleanProperty> DIRECTION_TO_PROPERTY = FACING_PROPERTIES;
	
	protected HorizontalConnectingBlockHelper(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
		super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
	}
}
