package redstonetweaks.setting;

import net.minecraft.world.TickPriority;

public class TickPriorityProperty extends Property<TickPriority> {
	
	public TickPriorityProperty(TickPriority defaultValue) {
		super(defaultValue);
	}
	
	protected String[] generateCommandSuggestions() {
		return new String[] {Integer.toString(getDefault().getIndex())};
	}
}