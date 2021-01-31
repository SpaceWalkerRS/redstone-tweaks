package redstonetweaks.changelisteners;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.PresetEditor;

public interface IPresetChangeListener {
	
	public void presetChanged(PresetEditor editor);
	
	public void presetRemoved(Preset preset);
	
}
