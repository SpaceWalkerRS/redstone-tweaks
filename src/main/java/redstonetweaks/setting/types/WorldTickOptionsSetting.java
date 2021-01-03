package redstonetweaks.setting.types;

import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldTickOptionsSetting extends Setting<WorldTickOptions> {
	
	public WorldTickOptionsSetting(SettingsPack pack, String name, String description) {
		super(pack, name, description, new WorldTickOptions());
	}
	
	@Override
	public WorldTickOptions stringToValue(String string) {
		return WorldTickOptions.parseWorldTickOptions(string);
	}
	
	@Override
	public void set(WorldTickOptions newValue) {
		super.set(newValue.copy());
	}
	
	@Override
	public void setPresetValue(Preset preset, WorldTickOptions newValue) {
		super.setPresetValue(preset, newValue.copy());
	}
}
