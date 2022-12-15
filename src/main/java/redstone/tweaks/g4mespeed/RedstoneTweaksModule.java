package redstone.tweaks.g4mespeed;

import java.util.LinkedHashSet;
import java.util.Set;

import com.g4mesoft.core.GSIModule;
import com.g4mesoft.core.GSIModuleManager;
import com.g4mesoft.setting.GSSetting;
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

	public final Set<GSSettingCategory> categories = new LinkedHashSet<>();

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

	public final GSSettingCategory composterCategory = new GSSettingCategory("composter");
	public final GSIntegerSetting composterDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting composterTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory coralCategory = new GSSettingCategory("coral");
	public final GSIntegerSetting coralDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting coralDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting coralTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory daylightDetectorCategory = new GSSettingCategory("daylightDetector");
	public final GSBooleanSetting daylightDetectorEmitDirectSignal = new GSBooleanSetting("emitDirectSignal", true, SHOW_IN_GUI);

	public final GSSettingCategory detectorRailCategory = new GSSettingCategory("detectorRail");
	public final GSIntegerSetting detectorRailDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting detectorRailSignal = new GSIntegerSetting("signal", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting detectorRailSignalDirect = new GSIntegerSetting("signalDirect", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting detectorRailTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory dirtPathCategory = new GSSettingCategory("grassPath");
	public final GSIntegerSetting dirtPathDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting dirtPathTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory dispenserCategory = new GSSettingCategory("dispenser");
	public final GSIntegerSetting dispenserDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting dispenserLazy = new GSBooleanSetting("lazy", true, SHOW_IN_GUI);
	public final QuasiConnectivitySetting dispenserQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_GUI);
	public final GSBooleanSetting dispenserRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_GUI);
	public final TickPrioritySetting dispenserTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory dragonEggCategory = new GSSettingCategory("dragonEgg");
	public final GSIntegerSetting dragonEggDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);

	public final GSSettingCategory dropperCategory = new GSSettingCategory("dropper");
	public final GSIntegerSetting dropperDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting dropperLazy = new GSBooleanSetting("lazy", true, SHOW_IN_GUI);
	public final QuasiConnectivitySetting dropperQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_GUI);
	public final GSBooleanSetting dropperRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_GUI);
	public final TickPrioritySetting dropperTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory fallingBlockCategory = new GSSettingCategory("fallingBlock");
	public final GSIntegerSetting fallingBlockDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSBooleanSetting fallingBlockSuspendedByStickyBlocks = new GSBooleanSetting("suspendedByStickyBlocks", false, SHOW_IN_GUI);
	public final TickPrioritySetting fallingBlockTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory farmlandCategory = new GSSettingCategory("farmland");
	public final GSIntegerSetting farmlandDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting farmlandTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory fireCategory = new GSSettingCategory("fire");
	public final GSIntegerSetting fireDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting fireDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting fireTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory frostedIceCategory = new GSSettingCategory("frostedIce");
	public final GSIntegerSetting frostedIceDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final GSIntegerSetting frostedIceDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_GUI);
	public final TickPrioritySetting frostedIceTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_GUI);

	public final GSSettingCategory hayCategory = new GSSettingCategory("hay");
	public final GSBooleanSetting hayBlockMisalignedPistonMove = new GSBooleanSetting("blockMisalignedPistonMove", true, SHOW_IN_GUI);

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
	public void registerClientSettings(GSSettingManager manager) {
		registerCommonSettings(manager);
	}

	@Override
	public void registerServerSettings(GSSettingManager manager) {
		registerCommonSettings(manager);
	}

	private void registerCommonSettings(GSSettingManager manager) {
		categories.clear();

		registerSettings(manager, globalCategory,
			globalSignalMax);
		registerSettings(manager, activatorRailCategory,
			activatorRailDelayRisingEdge,
			activatorRailDelayFallingEdge,
			activatorRailLazyRisingEdge,
			activatorRailLazyFallingEdge,
			activatorRailPowerLimit,
			activatorRailQuasiConnectivity,
			activatorRailRandomizeQuasiConnectivity,
			activatorRailTickPriorityRisingEdge,
			activatorRailTickPriorityFallingEdge);
		registerSettings(manager, anvilCategory,
			anvilCrushConcrete,
			anvilCrushWool);
		registerSettings(manager, bambooCategory,
			bambooDelay,
			bambooTickPriority);
		registerSettings(manager, barrierCategory,
			barrierMovable);
		registerSettings(manager, bubbleColumnCategory,
			bubbleColumnDelay,
			bubbleColumnTickPriority);
		registerSettings(manager, cactusCategory,
			cactusDelay,
			cactusNou,
			cactusTickPriority);
		registerSettings(manager, chorusPlantCategory,
			chorusPlantDelay,
			chorusPlantTickPriority);
		registerSettings(manager, commandBlockCategory,
			commandBlockDelay,
			commandBlockQuasiConnectivity,
			commandBlockRandomizeQuasiConnectivity,
			commandBlockTickPriority);
		registerSettings(manager, comparatorCategory,
			comparatorAdditionMode,
			comparatorDelay,
			comparatorMicroTickMode,
			comparatorRedstoneBlockAlternateInput,
			comparatorTickPriority,
			comparatorTickPriorityPrioritized);
		registerSettings(manager, composterCategory,
			composterDelay,
			composterTickPriority);
		registerSettings(manager, coralCategory,
			coralDelayMin,
			coralDelayMax,
			coralTickPriority);
		registerSettings(manager, daylightDetectorCategory,
			daylightDetectorEmitDirectSignal);
		registerSettings(manager, detectorRailCategory,
			detectorRailDelay,
			detectorRailSignal,
			detectorRailSignalDirect,
			detectorRailTickPriority);
		registerSettings(manager, dirtPathCategory,
			dirtPathDelay,
			dirtPathTickPriority);
		registerSettings(manager, dispenserCategory,
			dispenserDelay,
			dispenserLazy,
			dispenserQuasiConnectivity,
			dispenserRandomizeQuasiConnectivity,
			dispenserTickPriority);
		registerSettings(manager, dragonEggCategory,
			dragonEggDelay);
		registerSettings(manager, dropperCategory,
			dropperDelay,
			dropperLazy,
			dropperQuasiConnectivity,
			dropperRandomizeQuasiConnectivity,
			dropperTickPriority);
		registerSettings(manager, fallingBlockCategory,
			fallingBlockDelay,
			fallingBlockSuspendedByStickyBlocks,
			fallingBlockTickPriority);
		registerSettings(manager, farmlandCategory,
			farmlandDelay,
			farmlandTickPriority);
		registerSettings(manager, fireCategory,
			fireDelayMin,
			fireDelayMax,
			fireTickPriority);
		registerSettings(manager, frostedIceCategory,
			frostedIceDelayMin,
			frostedIceDelayMax,
			frostedIceTickPriority);
		registerSettings(manager, hayCategory,
			hayBlockMisalignedPistonMove);
		registerSettings(manager, observerCategory,
			observerDelayRisingEdge,
			observerDelayFallingEdge,
			observerDisable,
			observerMicroTickMode,
			observerObserveBlockUpdates,
			observerSignal,
			observerSignalDirect,
			observerTickPriorityRisingEdge,
			observerTickPriorityFallingEdge);
		registerSettings(manager, poweredRailCategory,
			poweredRailDelayRisingEdge,
			poweredRailDelayFallingEdge,
			poweredRailLazyRisingEdge,
			poweredRailLazyFallingEdge,
			poweredRailPowerLimit,
			poweredRailQuasiConnectivity,
			poweredRailRandomizeQuasiConnectivity,
			poweredRailTickPriorityRisingEdge,
			poweredRailTickPriorityFallingEdge);
		registerSettings(manager, repeaterCategory,
			repeaterDelayRisingEdge,
			repeaterDelayFallingEdge,
			repeaterLazyRisingEdge,
			repeaterLazyFallingEdge,
			repeaterMicroTickMode,
			repeaterSignal,
			repeaterSignalDirect,
			repeaterTickPriorityRisingEdge,
			repeaterTickPriorityFallingEdge,
			repeaterTickPriorityPrioritized);
	}

	private void registerSettings(GSSettingManager manager, GSSettingCategory category, GSSetting<?>... settings) {
		categories.add(category);

		for (GSSetting<?> setting : settings) {
			manager.registerSetting(category, setting);
		}
	}

	private static class Constants {

		public static final int DELAY_MAX = 1 << 10;
		public static final int SIGNAL_MIN = 0;
		public static final int SIGNAL_MAX = 1 << 10;

	}
}
