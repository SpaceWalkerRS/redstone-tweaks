package redstonetweaks.setting.preset;

import redstonetweaks.setting.SettingsCategory;

public class Preset {
	
	private final String name;
	private final SettingsCategory category;
	private String description;
	private Mode mode;
	
	private final boolean editable;
	
	public Preset(String name, SettingsCategory category, Mode mode) {
		this(name, category, mode, true);
	}
	
	public Preset(String name, SettingsCategory category, Mode mode, boolean editable) {
		this(name, category, "", mode, editable);
	}
	
	public Preset(String name, SettingsCategory category, String description, Mode mode, boolean editable) {
		this.name = name;
		this.category = category;
		this.description = description;
		this.mode = mode;
		
		this.editable = editable;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public String getName() {
		return name;
	}
	
	public SettingsCategory getCategory() {
		return category;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode newMode) {
		mode = newMode;
	}
	
	public void apply() {
		getCategory().applyPreset(this);
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public enum Mode {
		SET,
		SET_OR_DEFAULT;
	}
}
