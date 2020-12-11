package redstonetweaks.setting.preset;

import java.util.HashSet;
import java.util.Set;

import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.Setting;

public class PresetEditor {
	
	private final Preset TEMP = new Preset("TEMP", "A preset used to temporarily store values while a preset is being edited.", Preset.Mode.SET, true);
	
	private final Preset preset;
	
	private final Set<ISetting> changedSettings;
	private final Set<ISetting> addedSettings;
	private final Set<ISetting> removedSettings;
	
	private final String previousName;
	private String name;
	private String description;
	private Preset.Mode mode;
	
	private boolean saved;
	
	public PresetEditor(Preset preset) {
		this(preset, preset.getName(), preset.getName(), preset.getDescription(), preset.getMode());
	}
	
	public PresetEditor(String name, String description, Preset.Mode mode) {
		this(name, name, description, mode);
	}
	
	public PresetEditor(String previousName, String name, String description, Preset.Mode mode) {
		this(Presets.fromNameOrCreate(previousName == null ? name : previousName), previousName, name, description, mode);
	}
	
	private PresetEditor(Preset preset, String previousName, String name, String description, Preset.Mode mode) {
		this.preset = preset;
		
		this.changedSettings = new HashSet<>();
		this.addedSettings = new HashSet<>();
		this.removedSettings = new HashSet<>();
		
		this.previousName = previousName;
		this.name = name;
		this.description = description;
		this.mode = mode;
		
		this.saved = false;
	}
	
	public Preset getPreset() {
		return preset;
	}

	public boolean isEditable() {
		return preset.isEditable();
	}
	
	public Set<ISetting> getChangedSettings() {
		return changedSettings;
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
	
	public String getPreviousName() {
		return previousName;
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
	
	public boolean hasSetting(ISetting setting) {
		return (setting.hasPreset(preset) && !removedSettings.contains(setting)) || addedSettings.contains(setting);
	}
	
	public void addSetting(ISetting setting) {
		if (setting.hasPreset(preset)) {
			removedSettings.remove(setting);
		} else {
			addedSettings.add(setting);
		}
		setting.copyPresetValue(Presets.Default.DEFAULT, TEMP);
	}
	
	public void removeSetting(ISetting setting) {
		if (setting.hasPreset(preset)) {
			removedSettings.add(setting);
		} else {
			addedSettings.remove(setting);
		}
		setting.removePreset(TEMP);
	}
	
	public <T> T getValue(Setting<T> setting) {
		return (saved || !setting.hasPreset(TEMP)) ? setting.getPresetValueOrDefault(preset) : setting.getPresetValueOrDefault(TEMP);
	}
	
	public String getValueAsString(ISetting setting) {
		return (saved || !setting.hasPreset(TEMP)) ? setting.getPresetValueAsString(preset) : setting.getPresetValueAsString(TEMP);
	}
	
	public <T> void setValue(Setting<T> setting, T value) {
		if (hasSetting(setting)) {
			setting.setPresetValue(TEMP, value);
			
			if (setting.hasPreset(preset)) {
				changedSettings.add(setting);
			}
		}
	}
	
	public void setValueFromString(ISetting setting, String value) {
		if (hasSetting(setting)) {
			setting.setPresetValueFromString(TEMP, value);
			
			if (setting.hasPreset(preset)) {
				changedSettings.add(setting);
			}
		}
	}
	
	public void copyPresetValue(ISetting setting, Preset preset) {
		if (hasSetting(setting)) {
			setting.copyPresetValue(preset, TEMP);
			
			if (setting.hasPreset(this.preset)) {
				changedSettings.add(setting);
			}
		}
	}
	
	public boolean canSave() {
		return Presets.isNameValid(name) || name.equals(previousName);
	}
	
	public void saveChanges() {
		preset.setName(name);
		preset.setDescription(description);
		preset.setMode(mode);
		
		Presets.tryRegister(preset);
		
		for (ISetting setting : changedSettings) {
			setting.copyPresetValue(TEMP, preset);
			setting.removePreset(TEMP);
		}
		for (ISetting setting : addedSettings) {
			setting.copyPresetValue(TEMP, preset);
			setting.removePreset(TEMP);
		}
		for (ISetting setting : removedSettings) {
			setting.removePreset(preset);
			setting.removePreset(TEMP);
		}
		
		saved = true;
	}
	
	public void discardChanges() {
		for (ISetting setting : changedSettings) {
			setting.removePreset(TEMP);
		}
		for (ISetting setting : addedSettings) {
			setting.removePreset(TEMP);
		}
		for (ISetting setting : removedSettings) {
			setting.removePreset(TEMP);
		}
		
		saved = true;
	}
}
