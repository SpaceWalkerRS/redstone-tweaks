package redstonetweaks.settings;

import net.minecraft.world.TickPriority;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String name, String description, TickPriority defaultValue) {
		super(name, description, defaultValue);
	}
	
	@Override
	public void setFromText(String text) {
		int index = Integer.parseInt(text);
		set(TickPriority.byIndex(index));
	}
}
