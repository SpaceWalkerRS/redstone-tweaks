package redstonetweaks.listeners;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;

public interface IPresetListener {
	
	public void presetChanged(PresetEditor editor);
	
	public void presetAdded(Preset preset);
	
	public void presetRemoved(Preset preset);
	
}
