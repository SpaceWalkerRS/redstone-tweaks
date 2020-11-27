package redstonetweaks.setting.types;

import net.minecraft.world.TickPriority;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String name, String description) {
		super(name, description, TickPriority.NORMAL);
	}
	
	@Override
	public TickPriority stringToValue(String string) {
		return TickPriority.valueOf(string);
	}
}
