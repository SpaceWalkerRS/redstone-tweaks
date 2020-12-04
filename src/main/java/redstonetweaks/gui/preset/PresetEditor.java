package redstonetweaks.gui.preset;

import java.util.ArrayList;
import java.util.List;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.types.ISetting;

public class PresetEditor {
	
	private final Preset preset;
	private final List<ISetting> settings;
	
	private String name;
	private String description;
	private Preset.Mode mode;
	
	public PresetEditor(Preset preset) {
		this.preset = preset;
		this.settings = new ArrayList<>();
		
		this.name = preset.getName();
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
	
	public List<ISetting> getSettings() {
		return settings;
	}
	
	public void addSetting(ISetting setting) {
		settings.add(setting);
		setting.copyPresetValue(Presets.Default.DEFAULT, Presets.EDIT);
	}
	
	public void removeSetting(ISetting setting) {
		settings.remove(setting);
		setting.removePreset(Presets.EDIT);
	}
	
	private void fetchSettings() {
		for (ISetting setting : Settings.ALL) {
			if (setting.hasPreset(preset)) {
				settings.add(setting);
				setting.copyPresetValue(preset, Presets.EDIT);
			}
		}
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
	
	public boolean canSave() {
		return Presets.isNameValid(name) || preset.getName().equals(name);
	}
	
	public void saveChanges() {
		preset.setName(name);
		preset.setDescription(description);
		preset.setMode(mode);
		
		for (ISetting setting : settings) {
			setting.copyPresetValue(Presets.EDIT, preset);
		}
		removeEditPresetValues();
		
		if (!Presets.isRegistered(preset)) {
			Presets.register(preset);
		}
	}
	
	public void discardChanges() {
		removeEditPresetValues();
	}
	
	private void removeEditPresetValues() {
		for (ISetting setting : Settings.ALL) {
			setting.removePreset(Presets.EDIT);
		}
	}
}
