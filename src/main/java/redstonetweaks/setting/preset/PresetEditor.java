package redstonetweaks.setting.preset;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.PacketByteBuf;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.Setting;
import redstonetweaks.util.PacketUtils;

public class PresetEditor {
	
	private final Preset TEMP = new Preset(-Preset.nextId(), null, true, "TEMP", "A preset used to temporarily store values while a preset is being edited.", Preset.Mode.SET);
	
	private final Preset preset;
	
	private final Set<ISetting> currentSettings;
	private final Set<ISetting> changedSettings;
	private final Set<ISetting> addedSettings;
	private final Set<ISetting> removedSettings;
	
	private final int id;
	private final String previousName;
	private String name;
	private String description;
	private Preset.Mode mode;
	
	private boolean saved;
	
	public PresetEditor(Preset preset) {
		this(preset, preset.getId(), preset.getName(), preset.getName(), preset.getDescription(), preset.getMode());
	}
	
	private PresetEditor(Preset preset, int id, String previousName, String name, String description, Preset.Mode mode) {
		this.preset = preset;
		
		this.currentSettings = new HashSet<>();
		this.changedSettings = new HashSet<>();
		this.addedSettings = new HashSet<>();
		this.removedSettings = new HashSet<>();
		
		for (ISetting setting : Settings.getSettings()) {
			if (setting.hasPreset(preset)) {
				currentSettings.add(setting);
			}
		}
		
		this.id = id;
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
	
	public int getId() {
		return id;
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
		return (currentSettings.contains(setting) && !removedSettings.contains(setting)) || addedSettings.contains(setting);
	}
	
	public void addSetting(ISetting setting) {
		addSetting(setting, false);
	}
	
	public void addSetting(ISetting setting, boolean useCurrentValue) {
		if (currentSettings.contains(setting)) {
			removedSettings.remove(setting);
		} else {
			addedSettings.add(setting);
		}
		
		if (useCurrentValue) {
			setting.copyValueToPreset(TEMP);
		} else {
			setting.copyPresetValue(Presets.defaultPreset(), TEMP);
		}
	}
	
	public void removeSetting(ISetting setting) {
		if (currentSettings.contains(setting)) {
			removedSettings.add(setting);
		} else {
			addedSettings.remove(setting);
		}
		
		setting.removePreset(TEMP);
	}
	
	public <T> T getValue(Setting<T> setting) {
		return (saved || !setting.hasPreset(TEMP)) ? setting.getPresetValueOrDefault(preset) : setting.getPresetValueOrDefault(TEMP);
	}
	
	public <T> void setValue(Setting<T> setting, T value) {
		if (hasSetting(setting)) {
			setting.setPresetValue(TEMP, value);
			
			if (currentSettings.contains(setting)) {
				changedSettings.add(setting);
			}
		}
	}
	
	public void encode(PacketByteBuf buffer) {
		buffer.writeInt(changedSettings.size());
		for (ISetting setting : changedSettings) {
			buffer.writeString(setting.getId());
			encodeSetting(buffer, setting);
		}
		
		buffer.writeInt(addedSettings.size());
		for (ISetting setting : addedSettings) {
			buffer.writeString(setting.getId());
			encodeSetting(buffer, setting);
		}
		
		buffer.writeInt(removedSettings.size());
		for (ISetting setting : removedSettings) {
			buffer.writeString(setting.getId());
		}
	}
	
	private void encodeSetting(PacketByteBuf buffer, ISetting setting) {
		setting.encodePreset(buffer, saved || !setting.hasPreset(TEMP) ? preset : TEMP);
	}
	
	public void decode(PacketByteBuf buffer) {
		int changedCount = buffer.readInt();
		for (int i = 0; i < changedCount; i++) {
			ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			if (setting != null) {
				addSetting(setting);
				decodeSetting(buffer, setting);
			}
		}
		
		int addedCount = buffer.readInt();
		for (int i = 0; i < addedCount; i++) {
			ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			if (setting != null) {
				addSetting(setting);
				decodeSetting(buffer, setting);
			}
		}
		
		int removedCount = buffer.readInt();
		for (int i = 0; i < removedCount; i++) {
			ISetting setting = Settings.getSettingFromId(buffer.readString(PacketUtils.MAX_STRING_LENGTH));
			if (setting != null) {
				removeSetting(setting);
			}
		}
	}
	
	private void decodeSetting(PacketByteBuf buffer, ISetting setting) {
		setting.decodePreset(buffer, TEMP);
	}
	
	public void copyPresetValue(ISetting setting, Preset preset) {
		if (hasSetting(setting)) {
			setting.copyPresetValue(preset, TEMP);
			
			if (currentSettings.contains(setting)) {
				changedSettings.add(setting);
			}
		}
	}
	
	public boolean canSave() {
		return !saved && Presets.isNameValid(name) && (name.equals(previousName) || Presets.isNameAvailable(name));
	}
	
	public void trySaveChanges() {
		if (canSave()) {
			saveChanges();
		} else {
			discardChanges();
		}
	}
	
	public void saveChanges() {
		preset.setName(name);
		preset.setDescription(description);
		preset.setMode(mode);
		
		for (ISetting setting : changedSettings) {
			setting.copyPresetValue(TEMP, preset);
		}
		for (ISetting setting : addedSettings) {
			setting.copyPresetValue(TEMP, preset);
		}
		for (ISetting setting : removedSettings) {
			setting.removePreset(preset);
		}
		
		TEMP.remove();
		
		saved = true;
		
		Presets.presetChanged(this);
	}
	
	public void discardChanges() {
		TEMP.remove();
		
		saved = true;
	}
}
