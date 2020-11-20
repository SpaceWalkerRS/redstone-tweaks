package redstonetweaks.setting.types;

import net.minecraft.util.math.Direction;

public class DirectionToBooleanSetting extends ArraySetting<Direction, Boolean> {
	
	public DirectionToBooleanSetting(String name, String description, Boolean[] defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public Boolean stringToElement(String string) {
		return Boolean.parseBoolean(string);
	}
	
	@Override
	public int getIndexFromKey(Direction key) {
		return key.getId();
	}
	
	@Override
	public Direction getKeyFromIndex(int index) {
		return Direction.byId(index);
	}
}
