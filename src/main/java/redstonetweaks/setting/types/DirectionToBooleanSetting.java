package redstonetweaks.setting.types;

import net.minecraft.util.math.Direction;

import redstonetweaks.setting.SettingsPack;

public class DirectionToBooleanSetting extends ArraySetting<Direction, Boolean> {
	
	public DirectionToBooleanSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description, new Boolean[] {false});
	}
	
	@Override
	protected Boolean[] getEmptyArray(int size) {
		return new Boolean[size];
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
