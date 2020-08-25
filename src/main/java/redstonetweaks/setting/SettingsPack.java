package redstonetweaks.setting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SettingsPack {
	
	private final String name;
	
	private Map<Setting<?>, Property<?>> settings = new HashMap<>();
	
	public SettingsPack(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public <T extends Property<?>> void register(Setting<T> setting, T property) {
		settings.put(setting, property);
	}
	
	public boolean contains(Setting<?> setting) {
		return settings.containsKey(setting);
	}
	
	public Collection<Setting<?>> getSettings() {
		return settings.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Property<?>> T getProperty(Setting<T> setting) {
		return (T)settings.get(setting);
	}
	
	public <T> T get(Setting<? extends Property<T>> setting) {
		return getProperty(setting).get();
	}
	
	public <T> void set(Setting<? extends Property<T>> setting, T value) {
		 getProperty(setting).set(value);
	}
	
	public void reset(Setting<?> setting) {
		 getProperty(setting).reset();
	}
	
	public <T> T getDefault(Setting<? extends Property<T>> setting) {
		return getProperty(setting).getDefault();
	}
	
	public <T> boolean isDefault(Setting<? extends Property<T>> setting) {
		return getProperty(setting).isDefault();
	}
	
	public String[] getCommandSuggestions(Setting<?> setting) {
		return getProperty(setting).getCommandSuggestions();
	}
}
