package redstonetweaks.setting.types;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.settings.Settings;

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
		if (this.locked != locked) {
			this.locked = locked;
			
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
	public void encode(PacketByteBuf buffer) {
		write(buffer, get());
	}
	
	@Override
	public void decode(PacketByteBuf buffer) {
		set(read(buffer));
	}
	
	@Override
	public void encodePreset(PacketByteBuf buffer, Preset preset) {
		write(buffer, getPresetValue(preset));
	}
	
	@Override
	public void decodePreset(PacketByteBuf buffer, Preset preset) {
		setPresetValue(preset, read(buffer));
	}
	
	@Override
	public void applyPreset(Preset preset) {
		T value = getPresetValue(preset);
		if (value == null) {
			if (preset.getMode() == Preset.Mode.SET_OR_DEFAULT) {
				reset();
			}
		} else {
			set(value);
		}
	}
	
	@Override
	public void removePreset(Preset preset) {
		presetValues.remove(preset);
	}
	
	@Override
	public void clearPresets() {
		presetValues.clear();
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
		if (!valueEquals(value, newValue)) {
			value = newValue;
			
			valueChanged();
		}
	}
	
	protected void valueChanged() {
		Settings.settingValueChanged(this);
	}
	
	public T getDefault() {
		T value = getPresetValue(Presets.defaultPreset());
		if (value == null) {
			return getBackupValue();
		}
		
		return value;
	}
	
	public abstract void write(PacketByteBuf buffer, T value);
	
	public abstract T read(PacketByteBuf buffer);
	
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
