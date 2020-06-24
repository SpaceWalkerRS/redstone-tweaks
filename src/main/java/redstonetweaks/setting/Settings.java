package redstonetweaks.setting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {
	
	// Settings changed with the /delay command
	public static Setting<Integer> bubbleColumnDelay;
	public static Setting<Integer> comparatorDelay;
	public static Setting<Integer> detectorRailDelay;
	public static Setting<Integer> dispenserDelay;
	public static Setting<Integer> dropperDelay;
	public static Setting<Integer> gravityBlockDelay;
	public static Setting<Integer> heavyWeightedPressurePlateDelay;
	public static Setting<Integer> heavyWeightedPressurePlateOnDelay;
	public static Setting<Integer> hopperDelay;
	public static Setting<Integer> lavaDefaultDelay;
	public static Setting<Integer> lavaNetherDelay;
	public static Setting<Integer> leavesDelay;
	public static Setting<Integer> leverOffDelay;
	public static Setting<Integer> leverOnDelay;
	public static Setting<Integer> lightWeightedPressurePlateDelay;
	public static Setting<Integer> lightWeightedPressurePlateOnDelay;
	public static Setting<Integer> observerDelay;
	public static Setting<Integer> pistonDelay;
	public static Setting<Integer> pistonActivationDelay;
	public static Setting<Integer> redstoneLampDelay;
	public static Setting<Integer> redstoneTorchDelay;
	public static Setting<Integer> redstoneTorchBurnoutDelay;
	public static Setting<Integer> redstoneTorchBurnoutTimerDelay;
	public static Setting<Integer> repeaterDelay;
	public static Setting<Integer> scaffoldingDelay;
	public static Setting<Integer> stoneButtonDelay;
	public static Setting<Integer> stoneButtonOnDelay;
	public static Setting<Integer> stonePressurePlateDelay;
	public static Setting<Integer> stonePressurePlateOnDelay;
	public static Setting<Integer> tntDelay;
	public static Setting<Integer> tripwireDelay;
	public static Setting<Integer> tripwireHookDelay;
	public static Setting<Integer> waterDelay;
	public static Setting<Integer> woodenButtonDelay;
	public static Setting<Integer> woodenButtonOnDelay;
	public static Setting<Integer> woodenPressurePlateDelay;
	public static Setting<Integer> woodenPressurePlateOnDelay;
	
	// The delayMultiplier setting is changed with the /delaymultiplier command
	public static Setting<Integer> delayMultiplier;
	
	// Settings changed with the /signal command
	public static Setting<Integer> detectorRailSignal;
	public static Setting<Integer> leverSignal;
	public static Setting<Integer> observerSignal;
	public static Setting<Integer> redstoneBlockSignal;
	public static Setting<Integer> redstoneTorchSignal;
	public static Setting<Integer> repeaterSignal;
	public static Setting<Integer> stoneButtonSignal;
	public static Setting<Integer> stonePressurePlateSignal;
	public static Setting<Integer> tripwireHookSignal;
	public static Setting<Integer> woodenButtonSignal;
	public static Setting<Integer> woodenPressurePlateSignal;
	
	// Settings changed with the /bugfix command
	public static Setting<Boolean> MC136566;
	public static Setting<Boolean> MC137127;
	public static Setting<Boolean> MC189954;
	
	// Settings changed with the /quasiconnectivity command
	public static Setting<Boolean> quasiConnectivityDown;
	public static Setting<Boolean> quasiConnectivityEast;
	public static Setting<Boolean> quasiConnectivityNorth;
	public static Setting<Boolean> quasiConnectivitySouth;
	public static Setting<Boolean> quasiConnectivityUp;
	public static Setting<Boolean> quasiConnectivityWest;
	
	// Settings changed directly with the /tweak command
	public static Setting<Boolean> extendingPistonsIgnoreUpdates;
	public static Setting<Boolean> fastBlockDropping;
	public static Setting<Boolean> forceUpdatePoweredPistons;
	public static Setting<Integer> heavyWeightedPressurePlateWeight;
	public static Setting<Integer> lightWeightedPressurePlateWeight;
	public static Setting<Boolean> pistonsCheckPoweredOnce;
	public static Setting<Integer> poweredRailLimit;
	public static Setting<Integer> pushLimit;
	public static Setting<Boolean> randomizeQuasiConnectivity;
	public static Setting<Boolean> redstoneBlocksEmitDirectSignal;
	public static Setting<Boolean> softInversion;
	
	// Maps containing all settings
	// The orderedSettings map contains maps containing settings sorted by group
	private static Map<String, Setting<?>> settings = new LinkedHashMap<>();
	private static Map<String, Map<String, Setting<?>>> orderedSettings = new LinkedHashMap<>();
	
	public static Map<String, Setting<?>> register(String settingGroup, Map<String, Setting<?>> settingsMap) {
		orderedSettings.put(settingGroup, settingsMap);
		return settingsMap;
	}
	
	public static <T> void register(Setting<T> setting) {
		settings.put(setting.getName(), setting);
	}
	
	public static <T> void register(String settingGroup, Setting<T> setting) {
		register(setting);
		orderedSettings.get(settingGroup).put(setting.getName(), setting);
	}
	
	public static void registerSettings() {
		// Registering the maps that hold the settings accessed by a specific subcommand
		register("delay", new LinkedHashMap<>());
		register("signal", new LinkedHashMap<>());
		register("bugfix", new LinkedHashMap<>());
		register("quasiconnectivity", new LinkedHashMap<>());
		register("tweak", new LinkedHashMap<>());
		
		register("delay", bubbleColumnDelay = new IntegerSetting("bubbleColumnDelay", "bubble_column", 5, 1, 127));
		register("delay", comparatorDelay = new IntegerSetting("comparatorDelay", "comparator", 2, 1, 127));
		register("delay", detectorRailDelay = new IntegerSetting("detectorRailDelay", "detector_rail", 20, 1, 127));
		register("delay", dispenserDelay = new IntegerSetting("dispenserDelay", "dispenser", 4, 1, 127));
		register("delay", dropperDelay = new IntegerSetting("dropperDelay", "dropper", 4, 1, 127));
		register("delay", gravityBlockDelay = new IntegerSetting("gravityBlockDelay", "gravity_block", 2, 1, 127));
		register("delay", heavyWeightedPressurePlateDelay = new IntegerSetting("heavyWeightedPressurePlateDelay", "heavy_weighted_pressure_plate", 10, 1, 127));
		register("delay", heavyWeightedPressurePlateOnDelay = new IntegerSetting("heavyWeightedPressurePlateOnDelay", "heavy_weighted_pressure_plate_on", 0, 0, 127));
		register("delay", hopperDelay = new IntegerSetting("hopperDelay", "hopper", 1, 1, 127));
		register("delay", lavaDefaultDelay = new IntegerSetting("lavaDefaultDelay", "lava_default", 30, 1, 127));
		register("delay", lavaNetherDelay = new IntegerSetting("lavaNetherDelay", "lava_nether", 10, 1, 127));
		register("delay", leavesDelay = new IntegerSetting("leavesDelay", "leaves", 1, 1, 127));
		register("delay", leverOffDelay = new IntegerSetting("leverOffDelay", "lever_off", 0, 0, 127));
		register("delay", leverOnDelay = new IntegerSetting("leverOnDelay", "lever_on", 0, 0, 127));
		register("delay", lightWeightedPressurePlateDelay = new IntegerSetting("lightWeightedPressurePlateDelay", "light_weighted_pressure_plate", 10, 1, 127));
		register("delay", lightWeightedPressurePlateOnDelay = new IntegerSetting("lightWeightedPressurePlateOnDelay", "light_weighted_pressure_plate_on", 0, 0, 127));
		register("delay", observerDelay = new IntegerSetting("observerDelay", "observer", 2, 1, 127));
		register("delay", pistonDelay = new IntegerSetting("pistonDelay", "piston", 2, 0, 127));
		register("delay", pistonActivationDelay = new IntegerSetting("pistonActivationDelay", "piston_activation", 0, 0, 127));
		register("delay", redstoneLampDelay = new IntegerSetting("redstoneLampDelay", "redstone_lamp", 4, 1, 127));
		register("delay", redstoneTorchDelay = new IntegerSetting("redstoneTorchDelay", "redstone_torch", 2, 1, 127));
		register("delay", redstoneTorchBurnoutDelay = new IntegerSetting("redstoneTorchBurnoutDelay", "redstone_torch_burnout", 160, 1, 2047));
		register("delay", redstoneTorchBurnoutTimerDelay = new IntegerSetting("redstoneTorchBurnoutTimerDelay", "redstone_torch_burnout_timer", 60, 1, 1023));
		register("delay", repeaterDelay = new IntegerSetting("repeaterDelay", "repeater", 2, 1, 127));
		register("delay", scaffoldingDelay = new IntegerSetting("scaffoldingDelay", "scaffolding", 1, 1, 127));
		register("delay", stoneButtonDelay = new IntegerSetting("stoneButtonDelay", "stone_button", 20, 1, 127));
		register("delay", stoneButtonOnDelay = new IntegerSetting("stoneButtonOnDelay", "stone_button_on", 0, 0, 127));
		register("delay", stonePressurePlateDelay = new IntegerSetting("stonePressurePlateDelay", "stone_pressure_plate", 20, 1, 127));
		register("delay", stonePressurePlateOnDelay = new IntegerSetting("stonePressurePlateOnDelay", "stone_pressure_plate_on", 0, 0, 127));
		register("delay", tntDelay = new IntegerSetting("tntDelay", "tnt", 80, 1, 127));
		register("delay", tripwireDelay = new IntegerSetting("tripwireDelay", "tripwire", 10, 1, 127));
		register("delay", tripwireHookDelay = new IntegerSetting("tripwireHookDelay", "tripwire_hook", 10, 1, 127));
		register("delay", waterDelay = new IntegerSetting("waterDelay", "water", 5, 1, 127));
		register("delay", woodenButtonDelay = new IntegerSetting("woodenButtonDelay", "wooden_button", 30, 1, 127));
		register("delay", woodenButtonOnDelay = new IntegerSetting("woodenButtonOnDelay", "wooden_button_on", 0, 0, 127));
		register("delay", woodenPressurePlateDelay = new IntegerSetting("woodenPressurePlateDelay", "wooden_pressure_plate", 20, 1, 127));
		register("delay", woodenPressurePlateOnDelay = new IntegerSetting("woodenPressurePlateOnDelay", "wooden_pressure_plate_on", 0, 0, 127));
		
		register(delayMultiplier = new IntegerSetting("delay_multiplier", 1, 1, 127));
		
		register("signal", detectorRailSignal = new IntegerSetting("detectorRailSignal", "detector_rail", 15, 0, 127));
		register("signal", leverSignal = new IntegerSetting("leverSignal", "lever", 15, 0, 127));
		register("signal", observerSignal = new IntegerSetting("observerSignal", "observer", 15, 0, 127));
		register("signal", redstoneBlockSignal = new IntegerSetting("redstoneBlockSignal", "redstone_block", 15, 0, 127));
		register("signal", redstoneTorchSignal = new IntegerSetting("redstoneTorchSignal", "redstone_torch", 15, 0, 127));
		register("signal", repeaterSignal = new IntegerSetting("repeaterSignal", "repeater", 15, 0, 127));
		register("signal", stoneButtonSignal = new IntegerSetting("stoneButtonSignal", "stone_button", 15, 0, 127));
		register("signal", stonePressurePlateSignal = new IntegerSetting("stonePressurePlateSignal", "stone_pressure_plate", 15, 0, 127));
		register("signal", tripwireHookSignal = new IntegerSetting("tripwireHookSignal", "tripwire_hook", 15, 0, 127));
		register("signal", woodenButtonSignal = new IntegerSetting("woodenTuttonSignal", "wooden_button", 15, 0, 127));
		register("signal", woodenPressurePlateSignal = new IntegerSetting("woodenPressurePlateSignal", "wooden_pressure_plate", 15, 0, 127));
		
		register("bugfix", MC136566 = new BooleanSetting("MC-136566", false));
		register("bugfix", MC137127 = new BooleanSetting("MC-137127", false));
		register("bugfix", MC189954 = new BooleanSetting("MC-189954", false));
		
		register("quasiconnectivity", quasiConnectivityDown = new BooleanSetting("quasiConnectivityDown", "down", false));
		register("quasiconnectivity", quasiConnectivityEast = new BooleanSetting("quasiConnectivityEast", "east", false));
		register("quasiconnectivity", quasiConnectivityNorth = new BooleanSetting("quasiConnectivityNorth", "north", false));
		register("quasiconnectivity", quasiConnectivitySouth = new BooleanSetting("quasiConnectivitySouth", "south", false));
		register("quasiconnectivity", quasiConnectivityUp = new BooleanSetting("quasiConnectivityUp", "up", true));
		register("quasiconnectivity", quasiConnectivityWest = new BooleanSetting("quasiConnectivityWest", "west", false));
		
		register("tweak", extendingPistonsIgnoreUpdates = new BooleanSetting("extendingPistonsIgnoreUpdates", false));
		register("tweak", fastBlockDropping = new BooleanSetting("fastBlockDropping", true));
		register("tweak", forceUpdatePoweredPistons = new BooleanSetting("forceUpdatePoweredPistons", false));
		register("tweak", heavyWeightedPressurePlateWeight = new IntegerSetting("heavyWeightedPressurePlateWeight", 150, 1, 1023));
		register("tweak", lightWeightedPressurePlateWeight = new IntegerSetting("lightWeightedPressurePlateWeight", 15, 1, 1023));
		register("tweak", pistonsCheckPoweredOnce = new BooleanSetting("pistonsCheckPoweredOnce", false));
		register("tweak", softInversion = new BooleanSetting("pistonsPowerRedstoneTorches", false));
		register("tweak", poweredRailLimit = new IntegerSetting("poweredRailLimit", 9, 1, 127));
		register("tweak", pushLimit = new IntegerSetting("pushLimit", 12, 0, 127));
		register("tweak", randomizeQuasiConnectivity = new BooleanSetting("randomizeQuasiConnectivity", false));
		register("tweak", redstoneBlocksEmitDirectSignal = new BooleanSetting("redstoneBlocksEmitDirectSignal", false));
	}
	
	public static ArrayList<Setting<?>> getSettings() {
		ArrayList<Setting<?>> settingsArray = new ArrayList<>();
		for (Setting<?> setting : settings.values()) {
			settingsArray.add(setting);
		}
		return settingsArray;
	}
	
	public static Map<String, Map<String, Setting<?>>> getOrderedSettings() {
		return orderedSettings;
	}
	
	public static ArrayList<Setting<?>> getSettings(String settingGroup) {
		ArrayList<Setting<?>> settingsArray = new ArrayList<>();
		for (Setting<?> setting : orderedSettings.get(settingGroup).values()) {
			settingsArray.add(setting);
		}
		return settingsArray;
	}
	
	public static Setting<?> getSettingFromName(String name) {
		return settings.get(name);
	}
}
