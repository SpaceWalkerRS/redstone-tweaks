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
import redstone.tweaks.g4mespeed.setting.types.QuasiConnectivitySetting;
import redstone.tweaks.g4mespeed.setting.types.TickPrioritySetting;

public class RedstoneTweaksModule implements GSIModule {

	private static final boolean SHOW_IN_GUI = RedstoneTweaksMod.DEBUG;

	public final GSSettingCategory globalCategory = new GSSettingCategory("global");
	public final GSIntegerSetting globalSignalMax = new GSIntegerSetting("signalMax", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);

	public final GSSettingCategory activatorRailCategory = new GSSettingCategory("activatorRail");
	public final GSIntegerSetting activatorRailDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting activatorRailDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting activatorRailLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", false, SHOW_IN_GUI);
	public final GSBooleanSetting activatorRailLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_GUI);
	public final GSIntegerSetting activatorRailPowerLimit = new GSIntegerSetting("powerLimit", 9, 1, 1 << 10, SHOW_IN_GUI);
	public final QuasiConnectivitySetting activatorRailQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_GUI);
	public final GSBooleanSetting activatorRailRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_GUI);
	public final TickPrioritySetting activatorRailTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_GUI);
	public final TickPrioritySetting activatorRailTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory anvilCategory = new GSSettingCategory("anvil");
	public final GSBooleanSetting anvilCrushConcrete = new GSBooleanSetting("crushConcrete", false, SHOW_IN_GUI);
	public final GSBooleanSetting anvilCrushWool = new GSBooleanSetting("crushWool", false, SHOW_IN_GUI);

	public final GSSettingCategory bambooCategory = new GSSettingCategory("bamboo");
	public final GSIntegerSetting bambooDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting bambooTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory barrierCategory = new GSSettingCategory("barrier");
	public final GSBooleanSetting barrierMovable = new GSBooleanSetting("movable", false, SHOW_IN_GUI);

	public final GSSettingCategory bubbleColumnCategory = new GSSettingCategory("bubbleColumn");
	public final GSIntegerSetting bubbleColumnDelay = new GSIntegerSetting("delay", 5, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting bubbleColumnTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory cactusCategory = new GSSettingCategory("cactus");
	public final GSIntegerSetting cactusDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting cactusNou = new GSBooleanSetting("nou", false, SHOW_IN_GUI);
	public final TickPrioritySetting cactusTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory chorusPlantCategory = new GSSettingCategory("chorusPlant");
	public final GSIntegerSetting chorusPlantDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting chorusPlantTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory commandBlockCategory = new GSSettingCategory("commandBlock");
	public final GSIntegerSetting commandBlockDelay = new GSIntegerSetting("delay", 1, 1, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final QuasiConnectivitySetting commandBlockQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_GUI);
	public final GSBooleanSetting commandBlockRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_GUI);
	public final TickPrioritySetting commandBlockTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory comparatorCategory = new GSSettingCategory("comparator");
	public final GSBooleanSetting comparatorAdditionMode = new GSBooleanSetting("additionMode", false, SHOW_IN_GUI);
	public final GSIntegerSetting comparatorDelay = new GSIntegerSetting("delay", 2, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting comparatorMicroTickMode = new GSBooleanSetting("microTickMode", false, SHOW_IN_GUI);
	public final GSBooleanSetting comparatorRedstoneBlockAlternateInput = new GSBooleanSetting("redstoneBlockAlternateInput", true, SHOW_IN_GUI);
	public final TickPrioritySetting comparatorTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);
	public final TickPrioritySetting comparatorTickPriorityPrioritized = new TickPrioritySetting("tickPriorityPrioritized", TickPriority.HIGH, SHOW_IN_GUI);

	public final GSSettingCategory observerCategory = new GSSettingCategory("observer");
	public final GSIntegerSetting observerDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting observerDisable = new GSBooleanSetting("disable", false, SHOW_IN_GUI);
	public final GSBooleanSetting observerMicroTickMode = new GSBooleanSetting("microTickMode", false, SHOW_IN_GUI);
	public final GSBooleanSetting observerObserveBlockUpdates = new GSBooleanSetting("observeBlockUpdates", false, SHOW_IN_GUI);
	public final GSIntegerSetting observerSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting observerSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting observerTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_GUI);
	public final TickPrioritySetting observerTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory poweredRailCategory = new GSSettingCategory("poweredRail");
	public final GSIntegerSetting poweredRailDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting poweredRailDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting poweredRailLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", false, SHOW_IN_GUI);
	public final GSBooleanSetting poweredRailLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_GUI);
	public final GSIntegerSetting poweredRailPowerLimit = new GSIntegerSetting("powerLimit", 9, 1, 1 << 10, SHOW_IN_GUI);
	public final QuasiConnectivitySetting poweredRailQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_GUI);
	public final GSBooleanSetting poweredRailRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_GUI);
	public final TickPrioritySetting poweredRailTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_GUI);
	public final TickPrioritySetting poweredRailTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory repeaterCategory = new GSSettingCategory("repeater");
	public final GSIntegerSetting repeaterDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting repeaterDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting repeaterLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_GUI);
	public final GSBooleanSetting repeaterLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_GUI);
	public final GSBooleanSetting repeaterMicroTickMode = new GSBooleanSetting("microTickMode", false, SHOW_IN_GUI);
	public final GSIntegerSetting repeaterSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting repeaterSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting repeaterTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.HIGH, SHOW_IN_GUI);
	public final TickPrioritySetting repeaterTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.VERY_HIGH, SHOW_IN_GUI);
	public final TickPrioritySetting repeaterTickPriorityPrioritized = new TickPrioritySetting("tickPriorityPrioritized", TickPriority.EXTREMELY_HIGH, SHOW_IN_GUI);

	@Override
	public void init(GSIModuleManager manager) {

	}

	@Override
	public void registerServerSettings(GSSettingManager settings) {
		settings.registerSetting(globalCategory, globalSignalMax);

		settings.registerSetting(activatorRailCategory, activatorRailDelayRisingEdge);
		settings.registerSetting(activatorRailCategory, activatorRailDelayFallingEdge);
		settings.registerSetting(activatorRailCategory, activatorRailLazyRisingEdge);
		settings.registerSetting(activatorRailCategory, activatorRailLazyFallingEdge);
		settings.registerSetting(activatorRailCategory, activatorRailPowerLimit);
		settings.registerSetting(activatorRailCategory, activatorRailQuasiConnectivity);
		settings.registerSetting(activatorRailCategory, activatorRailRandomizeQuasiConnectivity);
		settings.registerSetting(activatorRailCategory, activatorRailTickPriorityRisingEdge);
		settings.registerSetting(activatorRailCategory, activatorRailTickPriorityFallingEdge);

		settings.registerSetting(anvilCategory, anvilCrushConcrete);
		settings.registerSetting(anvilCategory, anvilCrushWool);

		settings.registerSetting(bambooCategory, bambooDelay);
		settings.registerSetting(bambooCategory, bambooTickPriority);

		settings.registerSetting(barrierCategory, barrierMovable);

		settings.registerSetting(bubbleColumnCategory, bubbleColumnDelay);
		settings.registerSetting(bubbleColumnCategory, bubbleColumnTickPriority);

		settings.registerSetting(cactusCategory, cactusDelay);
		settings.registerSetting(cactusCategory, cactusNou);
		settings.registerSetting(cactusCategory, cactusTickPriority);

		settings.registerSetting(chorusPlantCategory, chorusPlantDelay);
		settings.registerSetting(chorusPlantCategory, chorusPlantTickPriority);

		settings.registerSetting(commandBlockCategory, commandBlockDelay);
		settings.registerSetting(commandBlockCategory, commandBlockQuasiConnectivity);
		settings.registerSetting(commandBlockCategory, commandBlockRandomizeQuasiConnectivity);
		settings.registerSetting(commandBlockCategory, commandBlockTickPriority);

		settings.registerSetting(comparatorCategory, comparatorAdditionMode);
		settings.registerSetting(comparatorCategory, comparatorDelay);
		settings.registerSetting(comparatorCategory, comparatorMicroTickMode);
		settings.registerSetting(comparatorCategory, comparatorRedstoneBlockAlternateInput);
		settings.registerSetting(comparatorCategory, comparatorTickPriority);
		settings.registerSetting(comparatorCategory, comparatorTickPriorityPrioritized);

		settings.registerSetting(observerCategory, observerDelayRisingEdge);
		settings.registerSetting(observerCategory, observerDelayFallingEdge);
		settings.registerSetting(observerCategory, observerDisable);
		settings.registerSetting(observerCategory, observerMicroTickMode);
		settings.registerSetting(observerCategory, observerObserveBlockUpdates);
		settings.registerSetting(observerCategory, observerSignal);
		settings.registerSetting(observerCategory, observerSignalDirect);
		settings.registerSetting(observerCategory, observerTickPriorityRisingEdge);
		settings.registerSetting(observerCategory, observerTickPriorityFallingEdge);

		settings.registerSetting(poweredRailCategory, poweredRailDelayRisingEdge);
		settings.registerSetting(poweredRailCategory, poweredRailDelayFallingEdge);
		settings.registerSetting(poweredRailCategory, poweredRailLazyRisingEdge);
		settings.registerSetting(poweredRailCategory, poweredRailLazyFallingEdge);
		settings.registerSetting(poweredRailCategory, poweredRailPowerLimit);
		settings.registerSetting(poweredRailCategory, poweredRailQuasiConnectivity);
		settings.registerSetting(poweredRailCategory, poweredRailRandomizeQuasiConnectivity);
		settings.registerSetting(poweredRailCategory, poweredRailTickPriorityRisingEdge);
		settings.registerSetting(poweredRailCategory, poweredRailTickPriorityFallingEdge);

		settings.registerSetting(repeaterCategory, repeaterDelayRisingEdge);
		settings.registerSetting(repeaterCategory, repeaterDelayFallingEdge);
		settings.registerSetting(repeaterCategory, repeaterLazyRisingEdge);
		settings.registerSetting(repeaterCategory, repeaterLazyFallingEdge);
		settings.registerSetting(repeaterCategory, repeaterMicroTickMode);
		settings.registerSetting(repeaterCategory, repeaterSignal);
		settings.registerSetting(repeaterCategory, repeaterSignalDirect);
		settings.registerSetting(repeaterCategory, repeaterTickPriorityRisingEdge);
		settings.registerSetting(repeaterCategory, repeaterTickPriorityFallingEdge);
		settings.registerSetting(repeaterCategory, repeaterTickPriorityPrioritized);
	}

	private static class Constants {

		public static final int DELAY_MAX = 1 << 10;
		public static final int SIGNAL_MIN = 0;
		public static final int SIGNAL_MAX = 1 << 10;

	}
}
