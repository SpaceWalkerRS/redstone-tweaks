package redstonetweaks.setting;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Settings {
	
	// Settings changed with the /delay command
	public static Setting<?> bubbleColumnDelay;
	public static Setting<?> comparatorDelay;
	public static Setting<?> detectorRailDelay;
	public static Setting<?> dispenserDelay;
	public static Setting<?> dropperDelay;
	public static Setting<?> gravityBlockDelay;
	public static Setting<?> hopperDelay;
	public static Setting<?> lavaDefaultDelay;
	public static Setting<?> lavaNetherDelay;
	public static Setting<?> leavesDelay;
	public static Setting<?> leverOffDelay;
	public static Setting<?> leverOnDelay;
	public static Setting<?> observerDelay;
	public static Setting<?> pistonDelay;
	public static Setting<?> pistonActivationDelay;
	public static Setting<?> pressurePlateDelay;
	public static Setting<?> pressurePlateOnDelay;
	public static Setting<?> redstoneLampDelay;
	public static Setting<?> redstoneTorchDelay;
	public static Setting<?> redstoneTorchBurnoutDelay;
	public static Setting<?> redstoneTorchBurnoutTimerDelay;
	public static Setting<?> repeaterDelay;
	public static Setting<?> scaffoldingDelay;
	public static Setting<?> stoneButtonDelay;
	public static Setting<?> stoneButtonOnDelay;
	public static Setting<?> tntDelay;
	public static Setting<?> tripwireDelay;
	public static Setting<?> tripwireHookDelay;
	public static Setting<?> waterDelay;
	public static Setting<?> weightedPressurePlateDelay;
	public static Setting<?> weightedPressurePlateOnDelay;
	public static Setting<?> woodenButtonDelay;
	public static Setting<?> woodenButtonOnDelay;
	
	// Settings changed with the /bugfix command
	public static Setting<?> MC136566;
	public static Setting<?> MC137127;
	public static Setting<?> MC189954;
	
	// Settings changed with the /quasiconnectivity command
	public static Setting<?> quasiConnectivityDown;
	public static Setting<?> quasiConnectivityEast;
	public static Setting<?> quasiConnectivityNorth;
	public static Setting<?> quasiConnectivitySouth;
	public static Setting<?> quasiConnectivityUp;
	public static Setting<?> quasiConnectivityWest;
	
	// Miscellaneous settings. These are changed with the /tweak command
	public static Setting<?> extendingPistonsIgnoreUpdates;
	public static Setting<?> fastBlockDropping;
	public static Setting<?> forceUpdatePoweredPistons;
	public static Setting<?> pistonsCheckPoweredOnce;
	public static Setting<?> pistonsPowerRedstoneTorches;
	public static Setting<?> pushLimit;
	public static Setting<?> randomizeQuasiConnectivity;
	
	// The delayMultiplier setting is changed with the /delaymultiplier command
	public static Setting<?> delayMultiplier;
	
	// This map contains all settings
	private static Map<String, Setting<?>> settings = new LinkedHashMap<>();
	
	public static Setting<?> register(Setting<?> setting) {
		settings.put(setting.getName(), setting);
		return setting;
	}
	
	public static void registerSettings() {
		bubbleColumnDelay = register(new IntegerSetting("block_delay", "bubble_column", 5, 1, 127));
		comparatorDelay = register(new IntegerSetting("block_delay", "comparator", 2, 1, 127));
		detectorRailDelay = register(new IntegerSetting("block_delay", "detector_rail", 20, 1, 127));
		dispenserDelay = register(new IntegerSetting("block_delay", "dispenser", 4, 1, 127));
		dropperDelay = register(new IntegerSetting("block_delay", "dropper", 4, 1, 127));
		gravityBlockDelay = register(new IntegerSetting("block_delay", "gravity_block", 2, 1, 127));
		hopperDelay = register(new IntegerSetting("block_delay", "hopper", 1, 1, 127));
		lavaDefaultDelay = register(new IntegerSetting("block_delay", "lava_default", 30, 1, 127));
		lavaNetherDelay = register(new IntegerSetting("block_delay", "lava_nether", 10, 1, 127));
		leavesDelay = register(new IntegerSetting("block_delay", "leaves", 1, 1, 127));
		leverOffDelay = register(new IntegerSetting("block_delay", "lever_OFF", 0, 0, 127));
		leverOnDelay = register(new IntegerSetting("block_delay", "lever_ON", 0, 0, 127));
		observerDelay = register(new IntegerSetting("block_delay", "observer", 2, 1, 127));
		pistonDelay = register(new IntegerSetting("block_delay", "piston", 2, 0, 127));
		pistonActivationDelay = register(new IntegerSetting("block_delay", "piston_ACTIVATION", 0, 0, 127));
		pressurePlateDelay = register(new IntegerSetting("block_delay", "pressure_plate", 20, 1, 127));
		pressurePlateOnDelay = register(new IntegerSetting("block_delay", "pressure_plate_ON", 0, 0, 127));
		redstoneLampDelay = register(new IntegerSetting("block_delay", "redstone_lamp", 4, 1, 127));
		redstoneTorchDelay = register(new IntegerSetting("block_delay", "redstone_torch", 2, 1, 127));
		redstoneTorchBurnoutDelay = register(new IntegerSetting("block_delay", "redstone_torch_burnout", 160, 1, 2047));
		redstoneTorchBurnoutTimerDelay = register(new IntegerSetting("block_delay", "redstone_torch_burnout_timer", 60, 1, 1023));
		repeaterDelay = register(new IntegerSetting("block_delay", "repeater", 2, 1, 127));
		scaffoldingDelay = register(new IntegerSetting("block_delay", "scaffolding", 1, 1, 127));
		stoneButtonDelay = register(new IntegerSetting("block_delay", "stone_button", 20, 1, 127));
		stoneButtonOnDelay = register(new IntegerSetting("block_delay", "stone_button_ON", 0, 0, 127));
		tntDelay = register(new IntegerSetting("block_delay", "tnt", 80, 1, 127));
		tripwireDelay = register(new IntegerSetting("block_delay", "tripwire", 10, 1, 127));
		tripwireHookDelay = register(new IntegerSetting("block_delay", "tripwire_hook", 10, 1, 127));
		waterDelay = register(new IntegerSetting("block_delay", "water", 5, 1, 127));
		weightedPressurePlateDelay = register(new IntegerSetting("block_delay", "weighted_pressure_plate", 10, 1, 127));
		weightedPressurePlateOnDelay = register(new IntegerSetting("block_delay", "weighted_pressure_plate_ON", 0, 0, 127));
		woodenButtonDelay = register(new IntegerSetting("block_delay", "wooden_button", 30, 1, 127));
		woodenButtonOnDelay = register(new IntegerSetting("block_delay", "wooden_button_ON", 0, 0, 127));
		
		MC136566 = register(new BooleanSetting("bug_fix", "MC-136566", false));
		MC137127 = register(new BooleanSetting("bug_fix", "MC-137127", false));
		MC189954 = register(new BooleanSetting("bug_fix", "MC-189954", false));
		
		quasiConnectivityDown = register(new BooleanSetting("qc", "down", false));
		quasiConnectivityEast = register(new BooleanSetting("qc", "east", false));
		quasiConnectivityNorth = register(new BooleanSetting("qc", "north", false));
		quasiConnectivitySouth = register(new BooleanSetting("qc", "south", false));
		quasiConnectivityUp = register(new BooleanSetting("qc", "up", true));
		quasiConnectivityWest = register(new BooleanSetting("qc", "west", false));
		
		extendingPistonsIgnoreUpdates = register(new BooleanSetting("tweak", "extendingPistonsIgnoreUpdates", false));
		fastBlockDropping = register(new BooleanSetting("tweak", "fastBlockDropping", true));
		forceUpdatePoweredPistons = register(new BooleanSetting("tweak", "forceUpdatePoweredPistons", false));
		pistonsCheckPoweredOnce = register(new BooleanSetting("tweak", "pistonsCheckPoweredOnce", false));
		pistonsPowerRedstoneTorches = register(new BooleanSetting("tweak", "pistonsPowerRedstoneTorches", false));
		pushLimit = register(new IntegerSetting("tweak", "pushLimit", 12, 0, 127));
		randomizeQuasiConnectivity = register(new BooleanSetting("tweak", "randomizeQuasiConnectivity", false));
		
		delayMultiplier = register(new IntegerSetting("", "delay_multiplier", 1, 1, 127));
	}
	
	public static Collection<Setting<?>> getSettings() {
		return settings.values();
	}
	
	// Each setting has a category field which is used by the
	// command classes so they can get a list of
	// only the settings they need to access
	public static Collection<Setting<?>> getSettings(String settingCategory) {
		Map<String, Setting<?>> settingsOfCategory = new LinkedHashMap<>();
		for (Setting<?> setting : getSettings()) {
			if (setting.getCategory() == settingCategory) {
				settingsOfCategory.put(setting.getName(), setting);
			}
		}
		return settingsOfCategory.values();
	}
	
	public static Setting<?> getSettingFromName(String name) {
		return settings.get(name);
	}
	
	public static void loadSettings() {
		
	}
}
