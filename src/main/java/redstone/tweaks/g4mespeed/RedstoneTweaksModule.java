package redstone.tweaks.g4mespeed;

import com.g4mesoft.core.GSIModule;
import com.g4mesoft.core.GSIModuleManager;
import com.g4mesoft.setting.GSSettingCategory;
import com.g4mesoft.setting.GSSettingManager;
import com.g4mesoft.setting.types.GSBooleanSetting;
import com.g4mesoft.setting.types.GSIntegerSetting;

import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.RedstoneTweaksMod;

public class RedstoneTweaksModule implements GSIModule {

	private static final GSSettingCategory SETTING_CATEGORY = new GSSettingCategory(RedstoneTweaksMod.MOD_ID);
	private static final boolean SHOW_IN_GUI = RedstoneTweaksMod.DEBUG;

	public final GSIntegerSetting observerDelayRisingEdge = new GSIntegerSetting("observerDelayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerDelayFallingEdge = new GSIntegerSetting("observerDelayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting observerDisable = new GSBooleanSetting("observerDisable", false, SHOW_IN_GUI);
	public final GSBooleanSetting observerMicroTickMode = new GSBooleanSetting("observerMicroTickMode", false, SHOW_IN_GUI);
	public final GSBooleanSetting observerObserveBlockUpdates = new GSBooleanSetting("observerObserveBlockUpdates", false, SHOW_IN_GUI);
	public final GSIntegerSetting observerSignal = new GSIntegerSetting("observerSignal", Constants.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerSignalDirect = new GSIntegerSetting("observerSignalDirect", Constants.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerTickPriorityRisingEdge = new GSIntegerSetting("observerTickPriorityRisingEdge", TickPriority.NORMAL.getValue(), Constants.TICK_PRIORITY_MIN, Constants.TICK_PRIORITY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerTickPriorityFallingEdge = new GSIntegerSetting("observerTickPriorityFallingEdge", TickPriority.NORMAL.getValue(), Constants.TICK_PRIORITY_MIN, Constants.TICK_PRIORITY_MAX, SHOW_IN_GUI);

	@Override
	public void init(GSIModuleManager manager) {

	}

	@Override
	public void registerServerSettings(GSSettingManager settings) {
		settings.registerSetting(SETTING_CATEGORY, observerDelayRisingEdge);
		settings.registerSetting(SETTING_CATEGORY, observerDelayFallingEdge);
		settings.registerSetting(SETTING_CATEGORY, observerDisable);
		settings.registerSetting(SETTING_CATEGORY, observerMicroTickMode);
		settings.registerSetting(SETTING_CATEGORY, observerObserveBlockUpdates);
		settings.registerSetting(SETTING_CATEGORY, observerSignal);
		settings.registerSetting(SETTING_CATEGORY, observerSignalDirect);
		settings.registerSetting(SETTING_CATEGORY, observerTickPriorityRisingEdge);
		settings.registerSetting(SETTING_CATEGORY, observerTickPriorityFallingEdge);
	}

	private static class Constants {

		public static final int DELAY_MAX = 1 << 10;
		public static final int SIGNAL_MIN = Redstone.SIGNAL_MIN;
		public static final int SIGNAL_MAX = Redstone.SIGNAL_MAX;
		public static final int TICK_PRIORITY_MIN = -3;
		public static final int TICK_PRIORITY_MAX = 3;

	}
}
