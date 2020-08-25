package redstonetweaks.setting;

public class ClientSettingsManager extends SettingsManager {
	
	public ClientSettingsManager() {
		
	}
	
	@Override
	public <T> void updateSetting(SettingsPack pack, Setting<? extends Property<T>> setting, T value) {
		pack.set(setting, value);
	}
	
	public void resetSettings() {
		for (SettingsPack pack : SETTINGS_PACKS.values()) {
			for (Setting<?> setting : pack.getSettings()) {
				pack.reset(setting);
			}
		}
	}
}
