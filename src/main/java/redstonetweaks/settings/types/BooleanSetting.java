package redstonetweaks.settings.types;

import net.minecraft.client.MinecraftClient;

import redstonetweaks.gui.SettingsListWidget.Entry;
import redstonetweaks.gui.setting.BooleanSettingGUIEntry;

public class BooleanSetting extends Setting<Boolean> {
	
	public BooleanSetting(String prefix, String name, String description, Boolean defaultValue) {
		super(prefix, name, description, defaultValue);
		
		set(getDefault());
	}
	
	@Override
	public void setFromText(String text) {
		try {
			boolean newValue = Boolean.parseBoolean(text);
			set(newValue);
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public Entry createGUIEntry(MinecraftClient client) {
		return new BooleanSettingGUIEntry(client, this);
	}
}
