package redstonetweaks.setting.preset;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.ISetting;

public class Preset {
	
	private final boolean editable;
	
	private String name;
	private String previousName;
	private String description;
	private Mode mode;
	
	public Preset(String name, Mode mode) {
		this(name, mode, true);
	}
	
	public Preset(String name, Mode mode, boolean editable) {
		this(name, "", mode, editable);
	}
	
	public Preset(String name, String description, Mode mode, boolean editable) {
		this.editable = editable;
		
		this.name = name;
		this.description = description;
		this.mode = mode;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPreviousName() {
		return previousName;
	}
	
	public void setName(String name) {
		this.previousName = this.name;
		this.name = name;
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
		Settings.applyPreset(this);
	}
	
	public void duplicate() {
		Preset duplicate = new Preset(name + " - copy", description, mode, editable);
		Presets.register(duplicate);
		
		for (ISetting setting : Settings.ALL) {
			setting.copyPresetValue(this, duplicate);
		}
	}
	
	public enum Mode {
		SET(0),
		SET_OR_DEFAULT(1);
		
		private static final Mode[] MODES;
		
		static {
			MODES = new Mode[values().length];
			
			for (Mode mode : values()) {
				MODES[mode.index] = mode;
			}
		}
		
		private final int index;
		
		private Mode(int index) {
			this.index = index;
		}
		
		public int getIndex() {
			return index;
		}
		
		public static Mode fromIndex(int index) {
			if (index < 0) {
				return MODES[MODES.length - 1];
			}
			if (index >= MODES.length) {
				return MODES[0];
			}
			return MODES[index];
		}
		
		public Mode next() {
			return fromIndex(index + 1);
		}
		
		public Mode previous() {
			return fromIndex(index - 1);
		}
	}
}
