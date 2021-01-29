package redstonetweaks.setting.types;

import net.minecraft.world.TickPriority;

import redstonetweaks.setting.SettingsPack;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(SettingsPack pack, String name, String description) {
		super(pack, name, description);
	}
	
	@Override
	public TickPriority getBackupValue() {
		return TickPriority.NORMAL;
	}
	
	@Override
	public TickPriority stringToValue(String string) {
		return TickPriority.valueOf(string);
	}
}
