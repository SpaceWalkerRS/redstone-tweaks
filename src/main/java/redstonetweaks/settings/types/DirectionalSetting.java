package redstonetweaks.settings.types;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;

import redstonetweaks.gui.SettingsListWidget.Entry;
import redstonetweaks.gui.setting.DirectionalSettingGUIEntry;

public abstract class DirectionalSetting<T> extends ArraySetting<T> {

	public DirectionalSetting(String prefix, String name, String description, T[] defaultValue) {
		super(prefix, name, description, defaultValue);
	}
	
	@Override
	public Entry createGUIEntry(MinecraftClient client) {
		return new DirectionalSettingGUIEntry(client, this);
	}
	
	public T get(Direction dir) {
		return get(dir.getId());
	}
	
	public void set(Direction dir, T value) {
		set(dir.getId(), value);
	}
}
