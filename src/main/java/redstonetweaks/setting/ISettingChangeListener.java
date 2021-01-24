package redstonetweaks.setting;

import redstonetweaks.interfaces.IChangeListener;
import redstonetweaks.setting.types.ISetting;

public interface ISettingChangeListener extends IChangeListener {
	
	@Override
	default void addChangeListener() {
		Settings.addChangeListener(this);
	}
	
	@Override
	default void removeChangeListener() {
		Settings.removeChangeListener(this);
	}
	
	public void settingLockedChanged(ISetting setting);
	
	public void settingValueChanged(ISetting setting);
	
}
