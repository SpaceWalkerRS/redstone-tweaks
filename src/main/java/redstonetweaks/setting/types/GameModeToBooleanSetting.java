package redstonetweaks.setting.types;

import net.minecraft.world.GameMode;

public class GameModeToBooleanSetting extends ArraySetting<GameMode, Boolean> {
	
	public GameModeToBooleanSetting(String name, String description, Boolean[] defaultValues) {
		super(name, description, defaultValues);
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
