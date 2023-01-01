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

import net.minecraft.core.Direction;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.RedstoneTweaksMod;
import redstone.tweaks.g4mespeed.setting.types.CapacitorBehaviorSetting;
import redstone.tweaks.g4mespeed.setting.types.QuasiConnectivitySetting;
import redstone.tweaks.g4mespeed.setting.types.TickPrioritySetting;
import redstone.tweaks.world.level.block.CapacitorBehavior;
import redstone.tweaks.world.level.block.QuasiConnectivity;

public class RedstoneTweaksModule implements GSIModule {

	private static final boolean SHOW_IN_G4MESPEED_GUI = RedstoneTweaksMod.DEBUG;

	public final Set<GSSettingCategory> categories = new LinkedHashSet<>();

	public final GSSettingCategory globalCategory = new GSSettingCategory("global");
	public final GSBooleanSetting globalMovableBlockEntities = new GSBooleanSetting("movableBlockEntities", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting globalMovableMovingBlocks = new GSBooleanSetting("movableMovingBlocks", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting globalSignalMax = new GSIntegerSetting("signalMax", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory activatorRailCategory = new GSSettingCategory("activatorRail");
	public final GSIntegerSetting activatorRailDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting activatorRailDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting activatorRailLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting activatorRailLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting activatorRailPowerLimit = new GSIntegerSetting("powerLimit", 9, 1, 1 << 10, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting activatorRailQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting activatorRailRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting activatorRailTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting activatorRailTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory anvilCategory = new GSSettingCategory("anvil");
	public final GSBooleanSetting anvilCrushConcrete = new GSBooleanSetting("crushConcrete", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting anvilCrushWool = new GSBooleanSetting("crushWool", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory bambooCategory = new GSSettingCategory("bamboo");
	public final GSIntegerSetting bambooDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bambooTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory barrierCategory = new GSSettingCategory("barrier");
	public final GSBooleanSetting barrierMovable = new GSBooleanSetting("movable", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory bigDripleafCategory = new GSSettingCategory("bigDripleaf");
	public final GSIntegerSetting bigDripleafDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting bigDripleafDelayUnstable = new GSIntegerSetting("delayUnstable", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting bigDripleafDelayPartial = new GSIntegerSetting("delayPartial", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting bigDripleafDelayFull = new GSIntegerSetting("delayFull", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting bigDripleafQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting bigDripleafRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bigDripleafTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bigDripleafTickPriorityUnstable = new TickPrioritySetting("tickPriorityUnstable", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bigDripleafTickPriorityPartial = new TickPrioritySetting("tickPriorityPartial", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bigDripleafTickPriorityFull = new TickPrioritySetting("tickPriorityFull", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory bubbleColumnCategory = new GSSettingCategory("bubbleColumn");
	public final GSIntegerSetting bubbleColumnDelay = new GSIntegerSetting("delay", 5, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting bubbleColumnTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory cactusCategory = new GSSettingCategory("cactus");
	public final GSIntegerSetting cactusDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting cactusNou = new GSBooleanSetting("nou", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting cactusTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory cauldronCategory = new GSSettingCategory("cauldron");
	public final GSIntegerSetting cauldronDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting cauldronTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory chorusPlantCategory = new GSSettingCategory("chorusPlant");
	public final GSIntegerSetting chorusPlantDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting chorusPlantTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory commandBlockCategory = new GSSettingCategory("commandBlock");
	public final GSIntegerSetting commandBlockDelay = new GSIntegerSetting("delay", 1, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting commandBlockQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting commandBlockRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting commandBlockTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory comparatorCategory = new GSSettingCategory("comparator");
	public final GSBooleanSetting comparatorAdditionMode = new GSBooleanSetting("additionMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting comparatorDelay = new GSIntegerSetting("delay", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting comparatorMicrotickMode = new GSBooleanSetting("microtickMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting comparatorRedstoneBlockAlternateInput = new GSBooleanSetting("redstoneBlockAlternateInput", true, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting comparatorTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting comparatorTickPriorityPrioritized = new TickPrioritySetting("tickPriorityPrioritized", TickPriority.HIGH, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory composterCategory = new GSSettingCategory("composter");
	public final GSIntegerSetting composterDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting composterTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory coralCategory = new GSSettingCategory("coral");
	public final GSIntegerSetting coralDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting coralDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting coralTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory daylightDetectorCategory = new GSSettingCategory("daylightDetector");
	public final GSBooleanSetting daylightDetectorEmitDirectSignal = new GSBooleanSetting("emitDirectSignal", true, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory detectorRailCategory = new GSSettingCategory("detectorRail");
	public final GSIntegerSetting detectorRailDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting detectorRailSignal = new GSIntegerSetting("signal", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting detectorRailSignalDirect = new GSIntegerSetting("signalDirect", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting detectorRailTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory dirtPathCategory = new GSSettingCategory("grassPath");
	public final GSIntegerSetting dirtPathDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting dirtPathTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory dispenserCategory = new GSSettingCategory("dispenser");
	public final GSIntegerSetting dispenserDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting dispenserLazy = new GSBooleanSetting("lazy", true, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting dispenserQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting dispenserRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting dispenserTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory dragonEggCategory = new GSSettingCategory("dragonEgg");
	public final GSIntegerSetting dragonEggDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory dropperCategory = new GSSettingCategory("dropper");
	public final GSIntegerSetting dropperDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting dropperLazy = new GSBooleanSetting("lazy", true, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting dropperQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting dropperRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting dropperTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory fallingBlockCategory = new GSSettingCategory("fallingBlock");
	public final GSIntegerSetting fallingBlockDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting fallingBlockSuspendedByStickyBlocks = new GSBooleanSetting("suspendedByStickyBlocks", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting fallingBlockTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory farmlandCategory = new GSSettingCategory("farmland");
	public final GSIntegerSetting farmlandDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting farmlandTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory fireCategory = new GSSettingCategory("fire");
	public final GSIntegerSetting fireDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting fireDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting fireTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory frogspawnCategory = new GSSettingCategory("frogspawn");
	public final GSIntegerSetting frogspawnDelayHatchMin = new GSIntegerSetting("delayHatchMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting frogspawnDelayHatchMax = new GSIntegerSetting("delayHatchMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting frogspawnTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory frostedIceCategory = new GSSettingCategory("frostedIce");
	public final GSIntegerSetting frostedIceDelayMin = new GSIntegerSetting("delayMin", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting frostedIceDelayMax = new GSIntegerSetting("delayMax", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting frostedIceTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory hayCategory = new GSSettingCategory("hay");
	public final GSBooleanSetting hayBlockMisalignedPistonMove = new GSBooleanSetting("blockMisalignedPistonMove", true, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory heavyWeightedPressurePlateCategory = new GSSettingCategory("heavyWeightedPressurePlate");
	public final GSIntegerSetting heavyWeightedPressurePlateDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting heavyWeightedPressurePlateDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting heavyWeightedPressurePlateTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting heavyWeightedPressurePlateTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting heavyWeightedPressurePlateWeight = new GSIntegerSetting("weight", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory hopperCategory = new GSSettingCategory("hopper");
	public final GSIntegerSetting hopperCooldown = new GSIntegerSetting("cooldown", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting hopperCooldownPrioritized = new GSIntegerSetting("cooldownPrioritized", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting hopperDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting hopperDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting hopperLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting hopperLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting hopperQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting hopperRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting hopperTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting hopperTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory lavaCategory = new GSSettingCategory("lava");
	public final GSIntegerSetting lavaDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lavaDelayNether = new GSIntegerSetting("delayNether", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lavaTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory leavesCategory = new GSSettingCategory("leaves");
	public final GSIntegerSetting leavesDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting leavesTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory lecternCategory = new GSSettingCategory("lectern");
	public final GSIntegerSetting lecternDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lecternDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lecternSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lecternSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lecternTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lecternTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory leverCategory = new GSSettingCategory("lever");
	public final GSIntegerSetting leverDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting leverDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting leverSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting leverSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting leverTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting leverTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory lightningRodCategory = new GSSettingCategory("lightningRod");
	public final GSIntegerSetting lightningRodDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lightningRodDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lightningRodSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lightningRodSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lightningRodTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lightningRodTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory lightWeightedPressurePlateCategory = new GSSettingCategory("lightWeightedPressurePlate");
	public final GSIntegerSetting lightWeightedPressurePlateDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lightWeightedPressurePlateDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lightWeightedPressurePlateTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting lightWeightedPressurePlateTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting lightWeightedPressurePlateWeight = new GSIntegerSetting("weight", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory magentaGlazedTerracottaCategory = new GSSettingCategory("magentaGlazedTerracotta");
	public final GSBooleanSetting magentaGlazedTerracottaSignalDiode = new GSBooleanSetting("signalDiode", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory magmaCategory = new GSSettingCategory("magma");
	public final GSIntegerSetting magmaDelay = new GSIntegerSetting("delay", 1, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting magmaTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory normalPistonCategory = new GSSettingCategory("normalPiston");
	public final GSBooleanSetting normalPistonCanMoveSelf = new GSBooleanSetting("canMoveSelf", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonConnectToWire = new GSBooleanSetting("connectToWire", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting normalPistonDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting normalPistonDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonHeadUpdatesNeighborsOnExtension = new GSBooleanSetting("headUpdatesNeighborsOnExtension", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonIgnorePowerFromFront = new GSBooleanSetting("ignorePowerFromFront", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonIgnoreUpdatesWhileExtending = new GSBooleanSetting("ignoreUpdatesWhileExtending", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonIgnoreUpdatesWhileRetracting = new GSBooleanSetting("ignoreUpdatesWhileRetracting", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonLooseHead = new GSBooleanSetting("looseHead", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonMovableWhenExtended = new GSBooleanSetting("movableWhenExtended", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting normalPistonPushLimit = new GSIntegerSetting("pushLimit", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting normalPistonQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", new QuasiConnectivity().setRange(Direction.UP, 1), SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting normalPistonSpeedRisingEdge = new GSIntegerSetting("speedRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting normalPistonSpeedFallingEdge = new GSIntegerSetting("speedFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting normalPistonTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting normalPistonTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting normalPistonUpdateSelf = new GSBooleanSetting("updateSelf", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory noteBlockCategory = new GSSettingCategory("noteBlock");
	public final GSIntegerSetting noteBlockDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting noteBlockLazy = new GSBooleanSetting("lazy", true, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting noteBlockQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting noteBlockRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting noteBlockTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory observerCategory = new GSSettingCategory("observer");
	public final GSIntegerSetting observerDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting observerDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting observerDisable = new GSBooleanSetting("disable", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting observerMicrotickMode = new GSBooleanSetting("microtickMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting observerObserveBlockUpdates = new GSBooleanSetting("observeBlockUpdates", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting observerSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting observerSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting observerTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting observerTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory pointedDripstoneCategory = new GSSettingCategory("pointedDripstone");
	public final GSIntegerSetting pointedDripstoneDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting pointedDripstoneDelayBelow = new GSIntegerSetting("delayBelow", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting pointedDripstoneTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting pointedDripstoneTickPriorityBelow = new TickPrioritySetting("tickPriorityBelow", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory poweredRailCategory = new GSSettingCategory("poweredRail");
	public final GSIntegerSetting poweredRailDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting poweredRailDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 0, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting poweredRailLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting poweredRailLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting poweredRailPowerLimit = new GSIntegerSetting("powerLimit", 9, 1, 1 << 10, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting poweredRailQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting poweredRailRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting poweredRailTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting poweredRailTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory railCategory = new GSSettingCategory("rail");
	public final GSIntegerSetting railDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting railQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting railRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting railTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redSandCategory = new GSSettingCategory("redSand");
	public final GSBooleanSetting redSandConnectToWire = new GSBooleanSetting("connectToWire", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redSandSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redSandSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redstoneBlockCategory = new GSSettingCategory("redstoneBlock");
	public final GSIntegerSetting redstoneBlockSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneBlockSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redstoneLampCategory = new GSSettingCategory("redstoneLamp");
	public final GSIntegerSetting redstoneLampDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneLampDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneLampLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneLampLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting redstoneLampQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneLampRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneLampTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.HIGH, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneLampTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.VERY_HIGH, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redstoneOreCategory = new GSSettingCategory("redstoneOre");
	public final CapacitorBehaviorSetting redstoneOreCapacitorBehavior = new CapacitorBehaviorSetting("capacitorBehavior", new CapacitorBehavior(), SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneOreConnectToWire = new GSBooleanSetting("connectToWire", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneOreDelay = new GSIntegerSetting("delay", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneOreSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneOreSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneOreTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redstoneTorchCategory = new GSSettingCategory("redstoneTorch");
	public final GSIntegerSetting redstoneTorchBurnoutCount = new GSIntegerSetting("burnoutCount", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchBurnoutTimer = new GSIntegerSetting("burnoutTimer", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchDelayBurnout = new GSIntegerSetting("delayBurnout", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneTorchLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneTorchLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneTorchMicrotickMode = new GSBooleanSetting("microtickMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting redstoneTorchSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneTorchSoftInversion = new GSBooleanSetting("softInversion", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneTorchTickPriorityBurnout = new TickPrioritySetting("tickPriorityBurnout", TickPriority.EXTREMELY_HIGH, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneTorchTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.HIGH, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneTorchTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.VERY_HIGH, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory redstoneWireCategory = new GSSettingCategory("redstoneWire");
	public final GSIntegerSetting redstoneWireDelay = new GSIntegerSetting("delay", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneWireInvertFlowOnGlass = new GSBooleanSetting("invertFlowOnGlass", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneWireMicrotickMode = new GSBooleanSetting("microtickMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting redstoneWireSlabsAllowUpConnection = new GSBooleanSetting("slabsAllowUpConnection", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting redstoneWireTickPriority = new TickPrioritySetting("tickPriority", TickPriority.EXTREMELY_HIGH, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory repeaterCategory = new GSSettingCategory("repeater");
	public final GSIntegerSetting repeaterDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting repeaterDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting repeaterLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting repeaterLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting repeaterMicrotickMode = new GSBooleanSetting("microtickMode", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting repeaterSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting repeaterSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting repeaterTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.HIGH, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting repeaterTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.VERY_HIGH, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting repeaterTickPriorityPrioritized = new TickPrioritySetting("tickPriorityPrioritized", TickPriority.EXTREMELY_HIGH, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory scaffoldingCategory = new GSSettingCategory("scaffolding");
	public final GSIntegerSetting scaffoldingDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting scaffoldingTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory sculkCatalystCategory = new GSSettingCategory("sculkCatalyst");
	public final GSIntegerSetting sculkCatalystDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting sculkCatalystTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory sculkSensorCategory = new GSSettingCategory("sculkSensor");
	public final GSIntegerSetting sculkSensorDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting sculkSensorTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory shulkerCategory = new GSSettingCategory("shulker");
	public final GSBooleanSetting shulkerConductRedstone = new GSBooleanSetting("conductRedstone", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting shulkerUpdateNeighborsWhenPeeking = new GSBooleanSetting("updateNeighborsWhenPeeking", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory shulkerBoxCategory = new GSSettingCategory("shulkerBox");
	public final GSBooleanSetting shulkerBoxUpdateNeighborsWhenPeeking = new GSBooleanSetting("updateNeighborsWhenPeeking", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory soulSandCategory = new GSSettingCategory("soulSand");
	public final GSIntegerSetting soulSandDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting soulSandTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory stairsCategory = new GSSettingCategory("stairs");
	public final GSBooleanSetting stairsConductRedstone = new GSBooleanSetting("conductRedstone", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory stickyPistonCategory = new GSSettingCategory("stickyPiston");
	public final GSBooleanSetting stickyPistonBlockDropping = new GSBooleanSetting("blockDropping", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonFastBlockDropping = new GSBooleanSetting("fastBlockDropping", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonSuperBlockDropping = new GSBooleanSetting("superBlockDropping", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonCanMoveSelf = new GSBooleanSetting("canMoveSelf", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonConnectToWire = new GSBooleanSetting("connectToWire", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonDoubleRetraction = new GSBooleanSetting("doubleRetraction", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonHeadUpdatesNeighborsOnExtension = new GSBooleanSetting("headUpdatesNeighborsOnExtension", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonHeadUpdatesNeighborsOnRetraction = new GSBooleanSetting("headUpdatesNeighborsOnRetraction", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonIgnorePowerFromFront = new GSBooleanSetting("ignorePowerFromFront", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonIgnoreUpdatesWhileExtending = new GSBooleanSetting("ignoreUpdatesWhileExtending", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonIgnoreUpdatesWhileRetracting = new GSBooleanSetting("ignoreUpdatesWhileRetracting", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonLazyRisingEdge = new GSBooleanSetting("lazyRisingEdge", true, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonLazyFallingEdge = new GSBooleanSetting("lazyFallingEdge", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonLooseHead = new GSBooleanSetting("looseHead", false, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonMovableWhenExtended = new GSBooleanSetting("movableWhenExtended", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonPushLimit = new GSIntegerSetting("pushLimit", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonPullLimit = new GSIntegerSetting("pullLimit", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting stickyPistonQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", new QuasiConnectivity().setRange(Direction.UP, 1), SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonSpeedRisingEdge = new GSIntegerSetting("speedRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stickyPistonSpeedFallingEdge = new GSIntegerSetting("speedFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonSuperSticky = new GSBooleanSetting("superSticky", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stickyPistonTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stickyPistonTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting stickyPistonUpdateSelf = new GSBooleanSetting("updateSelf", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory stoneButtonCategory = new GSSettingCategory("stoneButton");
	public final GSIntegerSetting stoneButtonDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stoneButtonDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stoneButtonSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stoneButtonSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stoneButtonTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stoneButtonTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory stonePressurePlateCategory = new GSSettingCategory("stonePressurePlate");
	public final GSIntegerSetting stonePressurePlateDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stonePressurePlateDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stonePressurePlateSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting stonePressurePlateSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stonePressurePlateTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting stonePressurePlateTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory sugarCaneCategory = new GSSettingCategory("sugarCane");
	public final GSIntegerSetting sugarCaneDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting sugarCaneTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory targetCategory = new GSSettingCategory("target");
	public final GSIntegerSetting targetDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting targetDelayArrow = new GSIntegerSetting("delayArrow", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting targetEmitDirectSignal = new GSBooleanSetting("emitDirectSignal", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting targetTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory tntCategory = new GSSettingCategory("tnt");
	public final GSIntegerSetting tntDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting tntFuseTime = new GSIntegerSetting("fuseTime", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting tntLazy = new GSBooleanSetting("lazy", false, SHOW_IN_G4MESPEED_GUI);
	public final QuasiConnectivitySetting tntQuasiConnectivity = new QuasiConnectivitySetting("quasiConnectivity", SHOW_IN_G4MESPEED_GUI);
	public final GSBooleanSetting tntRandomizeQuasiConnectivity = new GSBooleanSetting("randomizeQuasiConnectivity", false, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting tntTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory tripWireCategory = new GSSettingCategory("tripWire");
	public final GSIntegerSetting tripWireDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting tripWireTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory tripWireHookCategory = new GSSettingCategory("tripWireHook");
	public final GSIntegerSetting tripWireHookDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting tripWireHookSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting tripWireHookSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting tripWireHookTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory vinesCategory = new GSSettingCategory("vines");
	public final GSIntegerSetting vinesDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting vinesTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory waterCategory = new GSSettingCategory("water");
	public final GSIntegerSetting waterDelay = new GSIntegerSetting("delay", 20, 0, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting waterTickPriority = new TickPrioritySetting("tickPriority", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory whiteConcretePowderCategory = new GSSettingCategory("whiteConcretePowder");
	public final GSBooleanSetting whiteConcretePowderConductRedstone = new GSBooleanSetting("conductRedstone", false, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory woodenButtonCategory = new GSSettingCategory("woodenButton");
	public final GSIntegerSetting woodenButtonDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenButtonDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenButtonSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenButtonSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting woodenButtonTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting woodenButtonTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	public final GSSettingCategory woodenPressurePlateCategory = new GSSettingCategory("woodenPressurePlate");
	public final GSIntegerSetting woodenPressurePlateDelayRisingEdge = new GSIntegerSetting("delayRisingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenPressurePlateDelayFallingEdge = new GSIntegerSetting("delayFallingEdge", 2, 1, Constants.DELAY_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenPressurePlateSignal = new GSIntegerSetting("signal", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final GSIntegerSetting woodenPressurePlateSignalDirect = new GSIntegerSetting("signalDirect", Redstone.SIGNAL_MAX, Constants.SIGNAL_MIN, Constants.SIGNAL_MAX, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting woodenPressurePlateTickPriorityRisingEdge = new TickPrioritySetting("tickPriorityRisingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);
	public final TickPrioritySetting woodenPressurePlateTickPriorityFallingEdge = new TickPrioritySetting("tickPriorityFallingEdge", TickPriority.NORMAL, SHOW_IN_G4MESPEED_GUI);

	@Override
	public void init(GSIModuleManager manager) {

	}

	@Override
	public void registerServerSettings(GSSettingManager manager) {
		categories.clear();

		registerSettings(manager, globalCategory,
			globalMovableBlockEntities,
			globalMovableMovingBlocks,
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
		registerSettings(manager, bigDripleafCategory,
			bigDripleafDelay,
			bigDripleafDelayUnstable,
			bigDripleafDelayPartial,
			bigDripleafDelayFull,
			bigDripleafQuasiConnectivity,
			bigDripleafRandomizeQuasiConnectivity,
			bigDripleafTickPriority,
			bigDripleafTickPriorityUnstable,
			bigDripleafTickPriorityPartial,
			bigDripleafTickPriorityFull);
		registerSettings(manager, bubbleColumnCategory,
			bubbleColumnDelay,
			bubbleColumnTickPriority);
		registerSettings(manager, cactusCategory,
			cactusDelay,
			cactusNou,
			cactusTickPriority);
		registerSettings(manager, cauldronCategory,
			cauldronDelay,
			cauldronTickPriority);
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
			comparatorMicrotickMode,
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
		registerSettings(manager, frogspawnCategory,
			frogspawnDelayHatchMin,
			frogspawnDelayHatchMax,
			frogspawnTickPriority);
		registerSettings(manager, frostedIceCategory,
			frostedIceDelayMin,
			frostedIceDelayMax,
			frostedIceTickPriority);
		registerSettings(manager, hayCategory,
			hayBlockMisalignedPistonMove);
		registerSettings(manager, heavyWeightedPressurePlateCategory,
			heavyWeightedPressurePlateDelayRisingEdge,
			heavyWeightedPressurePlateDelayFallingEdge,
			heavyWeightedPressurePlateTickPriorityRisingEdge,
			heavyWeightedPressurePlateTickPriorityFallingEdge,
			heavyWeightedPressurePlateWeight);
		registerSettings(manager, hopperCategory,
			hopperCooldown,
			hopperCooldownPrioritized,
			hopperDelayRisingEdge,
			hopperDelayFallingEdge,
			hopperLazyRisingEdge,
			hopperLazyFallingEdge,
			hopperQuasiConnectivity,
			hopperRandomizeQuasiConnectivity,
			hopperTickPriorityRisingEdge,
			hopperTickPriorityFallingEdge);
		registerSettings(manager, lavaCategory,
			lavaDelay,
			lavaDelayNether,
			lavaTickPriority);
		registerSettings(manager, leavesCategory,
			leavesDelay,
			leavesTickPriority);
		registerSettings(manager, lecternCategory,
			lecternDelayRisingEdge,
			lecternDelayFallingEdge,
			lecternSignal,
			lecternSignalDirect,
			lecternTickPriorityRisingEdge,
			lecternTickPriorityFallingEdge);
		registerSettings(manager, leverCategory,
			leverDelayRisingEdge,
			leverDelayFallingEdge,
			leverSignal,
			leverSignalDirect,
			leverTickPriorityRisingEdge,
			leverTickPriorityFallingEdge);
		registerSettings(manager, lightningRodCategory,
			lightningRodDelayRisingEdge,
			lightningRodDelayFallingEdge,
			lightningRodSignal,
			lightningRodSignalDirect,
			lightningRodTickPriorityRisingEdge,
			lightningRodTickPriorityFallingEdge);
		registerSettings(manager, lightWeightedPressurePlateCategory,
			lightWeightedPressurePlateDelayRisingEdge,
			lightWeightedPressurePlateDelayFallingEdge,
			lightWeightedPressurePlateTickPriorityRisingEdge,
			lightWeightedPressurePlateTickPriorityFallingEdge,
			lightWeightedPressurePlateWeight);
		registerSettings(manager, magentaGlazedTerracottaCategory,
			magentaGlazedTerracottaSignalDiode);
		registerSettings(manager, magmaCategory,
			magmaDelay,
			magmaTickPriority);
		registerSettings(manager, normalPistonCategory,
			normalPistonCanMoveSelf,
			normalPistonConnectToWire,
			normalPistonDelayRisingEdge,
			normalPistonDelayFallingEdge,
			normalPistonHeadUpdatesNeighborsOnExtension,
			normalPistonIgnorePowerFromFront,
			normalPistonIgnoreUpdatesWhileExtending,
			normalPistonIgnoreUpdatesWhileRetracting,
			normalPistonLazyRisingEdge,
			normalPistonLazyFallingEdge,
			normalPistonLooseHead,
			normalPistonMovableWhenExtended,
			normalPistonPushLimit,
			normalPistonQuasiConnectivity,
			normalPistonRandomizeQuasiConnectivity,
			normalPistonSpeedRisingEdge,
			normalPistonSpeedFallingEdge,
			normalPistonTickPriorityRisingEdge,
			normalPistonTickPriorityFallingEdge,
			normalPistonUpdateSelf);
		registerSettings(manager, noteBlockCategory,
			noteBlockDelay,
			noteBlockLazy,
			noteBlockQuasiConnectivity,
			noteBlockRandomizeQuasiConnectivity,
			noteBlockTickPriority);
		registerSettings(manager, observerCategory,
			observerDelayRisingEdge,
			observerDelayFallingEdge,
			observerDisable,
			observerMicrotickMode,
			observerObserveBlockUpdates,
			observerSignal,
			observerSignalDirect,
			observerTickPriorityRisingEdge,
			observerTickPriorityFallingEdge);
		registerSettings(manager, pointedDripstoneCategory,
			pointedDripstoneDelay,
			pointedDripstoneDelayBelow,
			pointedDripstoneTickPriority,
			pointedDripstoneTickPriorityBelow);
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
		registerSettings(manager, railCategory,
			railDelay,
			railQuasiConnectivity,
			railRandomizeQuasiConnectivity,
			railTickPriority);
		registerSettings(manager, redSandCategory,
			redSandConnectToWire,
			redSandSignal,
			redSandSignalDirect);
		registerSettings(manager, redstoneBlockCategory,
			redstoneBlockSignal,
			redstoneBlockSignalDirect);
		registerSettings(manager, redstoneLampCategory,
			redstoneLampDelayRisingEdge,
			redstoneLampDelayFallingEdge,
			redstoneLampLazyRisingEdge,
			redstoneLampLazyFallingEdge,
			redstoneLampQuasiConnectivity,
			redstoneLampRandomizeQuasiConnectivity,
			redstoneLampTickPriorityRisingEdge,
			redstoneLampTickPriorityFallingEdge);
		registerSettings(manager, redstoneOreCategory,
			redstoneOreCapacitorBehavior,
			redstoneOreConnectToWire,
			redstoneOreDelay,
			redstoneOreSignal,
			redstoneOreSignalDirect,
			redstoneOreTickPriority);
		registerSettings(manager, redstoneTorchCategory,
			redstoneTorchBurnoutCount,
			redstoneTorchBurnoutTimer,
			redstoneTorchDelayBurnout,
			redstoneTorchDelayRisingEdge,
			redstoneTorchDelayFallingEdge,
			redstoneTorchLazyRisingEdge,
			redstoneTorchLazyFallingEdge,
			redstoneTorchMicrotickMode,
			redstoneTorchSignal,
			redstoneTorchSignalDirect,
			redstoneTorchSoftInversion,
			redstoneTorchTickPriorityBurnout,
			redstoneTorchTickPriorityRisingEdge,
			redstoneTorchTickPriorityFallingEdge);
		registerSettings(manager, redstoneWireCategory,
			redstoneWireDelay,
			redstoneWireInvertFlowOnGlass,
			redstoneWireMicrotickMode,
			redstoneWireSlabsAllowUpConnection,
			redstoneWireTickPriority);
		registerSettings(manager, repeaterCategory,
			repeaterDelayRisingEdge,
			repeaterDelayFallingEdge,
			repeaterLazyRisingEdge,
			repeaterLazyFallingEdge,
			repeaterMicrotickMode,
			repeaterSignal,
			repeaterSignalDirect,
			repeaterTickPriorityRisingEdge,
			repeaterTickPriorityFallingEdge,
			repeaterTickPriorityPrioritized);
		registerSettings(manager, scaffoldingCategory,
			scaffoldingDelay,
			scaffoldingTickPriority);
		registerSettings(manager, sculkCatalystCategory,
			sculkCatalystDelay,
			sculkCatalystTickPriority);
		registerSettings(manager, sculkSensorCategory,
			sculkSensorDelay,
			sculkSensorTickPriority);
		registerSettings(manager, shulkerCategory,
			shulkerConductRedstone,
			shulkerUpdateNeighborsWhenPeeking);
		registerSettings(manager, shulkerBoxCategory,
			shulkerBoxUpdateNeighborsWhenPeeking);
		registerSettings(manager, soulSandCategory,
			soulSandDelay,
			soulSandTickPriority);
		registerSettings(manager, stairsCategory,
			stairsConductRedstone);
		registerSettings(manager, stickyPistonCategory,
			stickyPistonBlockDropping,
			stickyPistonFastBlockDropping,
			stickyPistonSuperBlockDropping,
			stickyPistonCanMoveSelf,
			stickyPistonConnectToWire,
			stickyPistonDelayRisingEdge,
			stickyPistonDelayFallingEdge,
			stickyPistonDoubleRetraction,
			stickyPistonHeadUpdatesNeighborsOnExtension,
			stickyPistonHeadUpdatesNeighborsOnRetraction,
			stickyPistonIgnorePowerFromFront,
			stickyPistonIgnoreUpdatesWhileExtending,
			stickyPistonIgnoreUpdatesWhileRetracting,
			stickyPistonLazyRisingEdge,
			stickyPistonLazyFallingEdge,
			stickyPistonLooseHead,
			stickyPistonMovableWhenExtended,
			stickyPistonPushLimit,
			stickyPistonPullLimit,
			stickyPistonQuasiConnectivity,
			stickyPistonRandomizeQuasiConnectivity,
			stickyPistonSpeedRisingEdge,
			stickyPistonSpeedFallingEdge,
			stickyPistonSuperSticky,
			stickyPistonTickPriorityRisingEdge,
			stickyPistonTickPriorityFallingEdge,
			stickyPistonUpdateSelf);
		registerSettings(manager, stoneButtonCategory,
			stoneButtonDelayRisingEdge,
			stoneButtonDelayFallingEdge,
			stoneButtonSignal,
			stoneButtonSignalDirect,
			stoneButtonTickPriorityRisingEdge,
			stoneButtonTickPriorityFallingEdge);
		registerSettings(manager, stonePressurePlateCategory,
			stonePressurePlateDelayRisingEdge,
			stonePressurePlateDelayFallingEdge,
			stonePressurePlateSignal,
			stonePressurePlateSignalDirect,
			stonePressurePlateTickPriorityRisingEdge,
			stonePressurePlateTickPriorityFallingEdge);
		registerSettings(manager, sugarCaneCategory,
			sugarCaneDelay,
			sugarCaneTickPriority);
		registerSettings(manager, targetCategory,
			targetDelay,
			targetDelayArrow,
			targetEmitDirectSignal,
			targetTickPriority);
		registerSettings(manager, tntCategory,
			tntDelay,
			tntFuseTime,
			tntLazy,
			tntQuasiConnectivity,
			tntRandomizeQuasiConnectivity,
			tntTickPriority);
		registerSettings(manager, tripWireCategory,
			tripWireDelay,
			tripWireTickPriority);
		registerSettings(manager, tripWireHookCategory,
			tripWireHookDelay,
			tripWireHookSignal,
			tripWireHookSignalDirect,
			tripWireHookTickPriority);
		registerSettings(manager, vinesCategory,
			vinesDelay,
			vinesTickPriority);
		registerSettings(manager, waterCategory,
			waterDelay,
			waterTickPriority);
		registerSettings(manager, whiteConcretePowderCategory,
			whiteConcretePowderConductRedstone);
		registerSettings(manager, woodenButtonCategory,
			woodenButtonDelayRisingEdge,
			woodenButtonDelayFallingEdge,
			woodenButtonSignal,
			woodenButtonSignalDirect,
			woodenButtonTickPriorityRisingEdge,
			woodenButtonTickPriorityFallingEdge);
		registerSettings(manager, woodenPressurePlateCategory,
			woodenPressurePlateDelayRisingEdge,
			woodenPressurePlateDelayFallingEdge,
			woodenPressurePlateSignal,
			woodenPressurePlateSignalDirect,
			woodenPressurePlateTickPriorityRisingEdge,
			woodenPressurePlateTickPriorityFallingEdge);
	}

	private void registerSettings(GSSettingManager manager, GSSettingCategory category, GSSetting<?>... settings) {
		if (categories.contains(category)) {
			throw new IllegalStateException("Settings for category " + category.getName() + " have already been registered!");
		}

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
