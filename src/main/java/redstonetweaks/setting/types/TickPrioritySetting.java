package redstonetweaks.setting.types;

import net.minecraft.world.TickPriority;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String name, String description, TickPriority defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setValueFromString(String string) {
		try {
			set(TickPriority.valueOf(string));
		} catch (Exception e) {
			
		}
	}
}
