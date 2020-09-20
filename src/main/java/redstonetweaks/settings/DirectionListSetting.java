package redstonetweaks.settings;

import java.util.List;

import net.minecraft.util.math.Direction;

public class DirectionListSetting extends ListSetting<Direction> {
	
	public DirectionListSetting(String name, String description, List<Direction> defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	protected Direction getElementFromText(String text) {
		return Direction.byName(text);
	}
}
