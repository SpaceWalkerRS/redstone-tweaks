package redstonetweaks.setting.preset;

import redstonetweaks.interfaces.IChangeListener;

public interface IPresetChangeListener extends IChangeListener {
	
	@Override
	default void addChangeListener() {
		Presets.addChangeListener(this);
	}
	
	@Override
	default void removeChangeListener() {
		Presets.removeChangeListener(this);
	}
	
	public void presetChanged(Preset preset);
	
}
