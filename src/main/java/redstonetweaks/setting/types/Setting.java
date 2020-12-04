package redstonetweaks.setting.types;

import java.util.HashMap;
import java.util.Map;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;

public abstract class Setting<T> implements ISetting {
	
	private String id;
	private final String name;
	private final String description;
	private final T backupValue;
	private final Map<Preset, T> presetValues;
	
	private boolean enabled;
	private boolean locked;
	private T value;
	
	public Setting(String name, String description, T backupValue) {
		this.name = name;
		this.description = description;
		this.backupValue = backupValue;
		this.presetValues = new HashMap<>();
		
		this.enabled = false;
		this.locked = true;
		this.value = this.backupValue;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		if (this.id == null) {
			this.id = id;
		}
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
		this.locked = locked;
	}
	
	@Override
	public boolean isDefault() {
		return get().equals(getDefault());
	}
	
	@Override
	public void reset() {
		set(getDefault());
	}
	
	@Override
	public String getAsString() {
		return (isLocked() ? '1' : '0') + getValueAsString();
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
		return valueToString(getPresetValue(preset));
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
		T value = presetValues.get(preset);
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
		if (hasPreset(from)) {
			setPresetValue(to, getPresetValue(from));
		}
	}
	
	@Override
	public boolean hasPreset(Preset preset) {
		return presetValues.containsKey(preset);
	}
	
	public T get() {
		return value;
	}
	
	public void set(T newValue) {
		value = newValue;
	}
	
	public T getDefault() {
		T value = presetValues.get(Presets.Default.DEFAULT);
		if (value == null) {
			return backupValue;
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
	
	public void setPresetValue(Preset preset, T value) {
		presetValues.put(preset, value);
	}
}
