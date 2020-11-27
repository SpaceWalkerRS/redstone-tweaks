package redstonetweaks.setting.types;

import net.minecraft.world.GameMode;

public class GameModeToBooleanSetting extends ArraySetting<GameMode, Boolean> {
	
	public GameModeToBooleanSetting(String name, String description) {
		super(name, description, new Boolean[GameMode.values().length]);
	}
	
	@Override
	public Boolean[] getEmptyArray(int size) {
		return new Boolean[size];
	}
	
	@Override
	public Boolean stringToElement(String string) {
		return Boolean.parseBoolean(string);
	}
	
	@Override
	public int getIndexFromKey(GameMode key) {
		return key.getId();
	}
	
	@Override
	public GameMode getKeyFromIndex(int index) {
		return GameMode.byId(index, GameMode.CREATIVE);
	}
}
