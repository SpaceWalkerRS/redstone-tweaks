package redstonetweaks.settings.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.TickPriority;

import redstonetweaks.gui.SettingsListWidget.Entry;
import redstonetweaks.gui.setting.TickPrioritySettingGUIEntry;

public class TickPrioritySetting extends Setting<TickPriority> {
	
	public TickPrioritySetting(String prefix, String name, String description, TickPriority defaultValue) {
		super(prefix, name, description, defaultValue);
		
		set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			int index = Integer.parseInt(text);
			set(TickPriority.byIndex(index));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String getAsText() {
		return String.valueOf(get().getIndex());
	}
	
	@Override
	public Entry createGUIEntry(MinecraftClient client) {
		return new TickPrioritySettingGUIEntry(client, this);
	}
}
