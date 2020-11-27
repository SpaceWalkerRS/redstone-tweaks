package redstonetweaks.setting.types;

import redstonetweaks.setting.preset.Preset;

public interface ISetting {
	
	public String getId();
	
	public void setId(String id);
	
	public String getName();
	
	public String getDescription();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean isLocked();
	
	public void setLocked(boolean locked);
	
	public boolean isDefault();
	
	public void reset();
	
	public String getAsString();
	
	public void setFromString(String string);
	
	public String getValueAsString();
	
	public void setValueFromString(String string);
	
	public String getPresetValueAsString(Preset preset);
	
	public void setPresetValueFromString(Preset preset, String string) ;
	
	public void applyPreset(Preset preset);
	
}
