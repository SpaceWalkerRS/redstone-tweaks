package redstone.tweaks.g4mespeed;

import com.g4mesoft.core.GSIModule;
import com.g4mesoft.core.GSIModuleManager;
import com.g4mesoft.setting.GSSettingCategory;
import com.g4mesoft.setting.GSSettingManager;
import com.g4mesoft.setting.types.GSBooleanSetting;

import redstone.tweaks.RedstoneTweaksMod;

public class RedstoneTweaksModule implements GSIModule {

	private static final GSSettingCategory SETTING_CATEGORY = new GSSettingCategory(RedstoneTweaksMod.MOD_ID);

	public final GSBooleanSetting observerDisable = new GSBooleanSetting("observerDisable", false, false);

	@Override
	public void init(GSIModuleManager manager) {

	}

	@Override
	public void registerServerSettings(GSSettingManager settings) {
		settings.registerSetting(SETTING_CATEGORY, observerDisable);
	}
}
