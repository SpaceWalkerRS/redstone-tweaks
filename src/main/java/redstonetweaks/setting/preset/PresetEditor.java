package redstonetweaks.setting.preset;

import java.util.HashSet;
import java.util.Set;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.Setting;

public class PresetEditor {
	
	private final Preset preset;
	private final Set<ISetting> settings;
	private final Set<ISetting> addedSettings;
	private final Set<ISetting> removedSettings;
	
	private final String previousName;
	private String name;
	private String description;
	private Preset.Mode mode;
	
	public PresetEditor(Preset preset) {
		this(preset, preset.getName(), preset.getName());
	}
	
	public PresetEditor(String previousName, String name, String description, Preset.Mode mode) {
		this(Presets.fromNameOrCreate(previousName, description, mode), previousName, name);
	}
	
	private PresetEditor(Preset preset, String previousName, String name) {
		this.preset = preset;
		this.settings = new HashSet<>();
		this.addedSettings = new HashSet<>();
		this.removedSettings = new HashSet<>();
		
		this.previousName = previousName;
		this.name = name;
		this.description = preset.getDescription();
		this.mode = preset.getMode();
		
		fetchSettings();
	}
	
	public Preset getPreset() {
		return preset;
	}

	public boolean isEditable() {
		return preset.isEditable();
	}
	
	public Set<ISetting> getSettings() {
		return settings;
	}
	
	public Set<ISetting> getAddedSettings() {
		return addedSettings;
	}
	
	public Set<ISetting> getRemovedSettings() {
		return removedSettings;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Preset.Mode getMode() {
		return mode;
	}
	
	public void setMode(Preset.Mode mode) {
		this.mode = mode;
	}
	
	public void nextMode() {
		setMode(mode.next());
	}
	
	public void previousMode() {
		setMode(mode.previous());
	}
	
	private void fetchSettings() {
		for (ISetting setting : Settings.ALL) {
			if (setting.hasPreset(preset)) {
				settings.add(setting);
				setting.copyPresetValue(preset, Presets.TEMP);
			}
		}
	}
	
	public boolean hasSetting(ISetting setting) {
		return (settings.contains(setting) && !removedSettings.contains(setting)) || addedSettings.contains(setting);
	}
	
	public void addSetting(ISetting setting) {
		if (!settings.contains(setting) && !removedSettings.remove(setting)) {
			addedSettings.add(setting);
		}
		setting.copyPresetValue(Presets.Default.DEFAULT, Presets.TEMP);
	}
	
	public void removeSetting(ISetting setting) {
		if (settings.contains(setting) && !addedSettings.remove(setting)) {
			removedSettings.add(setting);
			
		}
		setting.removePreset(Presets.TEMP);
	}
	
	public <T> T getValue(Setting<T> setting) {
		return setting.getPresetValueOrDefault(Presets.TEMP);
	}
	
	public String getValueAsString(ISetting setting) {
		return setting.getPresetValueAsString(Presets.TEMP);
	}
	
	public <T> void setValue(Setting<T> setting, T value) {
		if (settings.contains(setting) || addedSettings.contains(setting)) {
			setting.setPresetValue(Presets.TEMP, value);
		}
	}
	
	public void setValueFromString(ISetting setting, String value) {
		if (settings.contains(setting) || addedSettings.contains(setting)) {
			setting.setPresetValueFromString(Presets.TEMP, value);
		}
	}
	
	public boolean canSave() {
		return Presets.isNameValid(name) || name.equals(previousName);
	}
	
	public void saveChanges() {
		preset.setName(name);
		preset.setDescription(description);
		preset.setMode(mode);
		
		if (!Presets.isRegistered(preset)) {
			Presets.register(preset);
		}
		
		for (ISetting setting : settings) {
			setting.copyPresetValue(Presets.TEMP, preset);
			setting.removePreset(Presets.TEMP);
		}
		for (ISetting setting : addedSettings) {
			setting.copyPresetValue(Presets.TEMP, preset);
			setting.removePreset(Presets.TEMP);
		}
		for (ISetting setting : removedSettings) {
			setting.removePreset(preset);
			setting.removePreset(Presets.TEMP);
		}
	}
	
	public void discardChanges() {
		for (ISetting setting : settings) {
			setting.removePreset(Presets.TEMP);
		}
	}
}
