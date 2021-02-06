package redstonetweaks.setting.types;

import java.util.HashMap;
import java.util.Map;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public abstract class Setting<T> implements ISetting {
	
	private final SettingsPack pack;
	private final String id;
	private final String name;
	private final String description;
	private final Map<Preset, T> presetValues;
	
	private boolean enabled;
	private boolean locked;
	private T value;
	
	public Setting(SettingsPack pack, String name, String description) {
		this.pack = pack;
		this.id = String.format("%s/%s", pack.getId(), name);
		this.name = name;
		this.description = description;
		this.presetValues = new HashMap<>();
		
		this.enabled = false;
		this.locked = true;
		this.value = getBackupValue();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Setting<?>) {
			return id.equals(((Setting<?>)other).id);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public SettingsPack getPack() {
		return pack;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public boolean isLocked() {
		return locked;
	}
	
	@Override
	public void setLocked(boolean locked) {
		boolean changed = this.locked != locked;
		
		this.locked = locked;
		
		if (changed) {
			Settings.settingLockedChanged(this);
		}
	}
	
	@Override
	public boolean isDefault() {
		return valueEquals(get(), getDefault());
	}
	
	@Override
	public void reset() {
		set(getDefault());
	}
	
	@Override
	public String getAsString() {
		return (isLocked() ? "1" : "0") + getValueAsString();
	}
	
	@Override
	public void setFromString(String string) {
		setLocked(string.charAt(0) == '1');
		setValueFromString(string.substring(1));
	}
	
	@Override
	public String getValueAsString() {
		return valueToString(get());
	}
	
	@Override
	public void setValueFromString(String string) {
		try {
			set(stringToValue(string));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String getPresetValueAsString(Preset preset) {
		return valueToString(getPresetValueOrDefault(preset));
	}
	
	@Override
	public void setPresetValueFromString(Preset preset, String string) {
		try {
			setPresetValue(preset, stringToValue(string));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void applyPreset(Preset preset) {
		T value = getPresetValue(preset);
		if (value != null) {
			set(value);
		}
	}
	
	@Override
	public void removePreset(Preset preset) {
		presetValues.remove(preset);
	}
	
	@Override
	public void copyPresetValue(Preset from, Preset to) {
		T value = getPresetValue(from);
		if (value != null) {
			setPresetValue(to, value);
		}
	}
	
	@Override
	public void copyValueToPreset(Preset preset) {
		setPresetValue(preset, get());
	}
	
	@Override
	public boolean hasPreset(Preset preset) {
		return presetValues.containsKey(preset);
	}
	
	protected abstract T getBackupValue();
	
	public T get() {
		return value;
	}
	
	public void set(T newValue) {
		boolean changed = !valueEquals(value, newValue);
		
		value = newValue;
		
		if (changed) {
			Settings.settingValueChanged(this);
		}
	}
	
	public T getDefault() {
		T value = getPresetValue(Presets.Default.DEFAULT);
		if (value == null) {
			return getBackupValue();
		}
		
		return value;
	}
	
	public String valueToString(T value) {
		return value.toString();
	}
	
	public abstract T stringToValue(String string);
	
	public T getPresetValue(Preset preset) {
		return presetValues.get(preset);
	}
	
	public T getPresetValueOrDefault(Preset preset) {
		return presetValues.getOrDefault(preset, getDefault());
	}
	
	public void setPresetValue(Preset preset, T value) {
		if (preset.isEditable() ? !opOnly() : !hasPreset(preset)) {
			presetValues.put(preset, value);
		}
	}
	
	public boolean valueEquals(T value1, T value2) {
		return value1.equals(value2);
	}
}
