package redstonetweaks.setting.types;

import net.minecraft.world.TickPriority;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String prefix, String name, String description, TickPriority defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	@Override
	public void setFromText(String text) {
		try {
			set(TickPriority.byIndex(Integer.parseInt(text)));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String getAsText() {
		return String.valueOf(get().getIndex());
	}
}
