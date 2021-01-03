package redstonetweaks.setting;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.types.ISetting;

public class Settings {
	
	public static final Map<String, ISetting> ALL = new HashMap<>();
	public static final Map<String, SettingsCategory> CATEGORIES = new LinkedHashMap<>();
	
	public static void register(SettingsCategory category) {
		if (CATEGORIES.putIfAbsent(category.getName(), category) != null) {
			RedstoneTweaks.LOGGER.warn("SettingsCategory " + category.getName() + " could not be registered, as a category with that name already exists");
		}
	}
	
	public static void register(SettingsPack pack) {
		if (pack.getCategory().getPacks().putIfAbsent(pack.getName(), pack) != null) {
			RedstoneTweaks.LOGGER.warn("SettingsPack " + pack.getName() + " could not be registered, as a pack with that name already exists in SettingsCategory " + pack.getCategory().getName());
		}
	}
	
	public static void register(ISetting setting) {
		if (setting.getPack().getSettings().putIfAbsent(setting.getName(), setting) != null) {
			RedstoneTweaks.LOGGER.warn(setting.getClass() + " " + setting.getName() + " could not be registered, as a setting with that name already exists in SettingsPack " + setting.getPack().getName() + " in SettingsCategory " + setting.getPack().getCategory().getName());
		} else
		if (ALL.putIfAbsent(setting.getId(), setting) != null) {
			RedstoneTweaks.LOGGER.warn(setting.getClass() + " " + setting.getName() + " could not be registered, as a setting with that name already exists");
		}
	}
	
	public static SettingsCategory getCategoryFromName(String name) {
		return CATEGORIES.get(name);
	}
	
	public static ISetting getSettingFromId(String id) {
		return ALL.get(id);
	}
	
	public static void resetAll() {
		ALL.forEach((name, setting) -> setting.reset());
	}
	
	public static void enableAll() {
		ALL.forEach((name, setting) -> setting.setEnabled(true));
	}
	
	public static void disableAll() {
		ALL.forEach((name, setting) -> setting.setEnabled(false));
	}
	
	public static void lockAll() {
		ALL.forEach((name, setting) -> setting.setLocked(true));
	}
	
	public static void unlockAll() {
		ALL.forEach((name, setting) -> setting.setLocked(false));
	}
	
	public static void applyPreset(Preset preset) {
		ALL.forEach((name, setting) -> setting.applyPreset(preset));
	}
	
	public static void removePreset(Preset preset) {
		ALL.forEach((name, setting) -> setting.removePreset(preset));
	}
	
	public static void toDefault() {
		disableAll();
		unlockAll();
		resetAll();
	}
	
	public static void init() {
		Tweaks.init();
		ServerConfig.init();
	}
	
	public static class Common {
		
		// Descriptions that are common between several settings
		public static final String DESC_DELAY_ACTIVATING = "Delay in ticks before activating.";
		public static final String DESC_DELAY_BREAKING = "Delay in ticks before breaking.";
		public static final String DESC_DELAY_RISING_EDGE = "Delay in ticks before powering on.";
		public static final String DESC_DELAY_FALLING_EDGE = "Delay in ticks before powering off.";
		public static final String DESC_LAZY = "When enabled, the block is \"lazy\". Whenever it is ticked it will activate without checking for received power.";
		public static final String DESC_LAZY_RISING_EDGE = "When enabled, the block is \"lazy\" on the rising edge. Whenever it is ticked, if it is unpowered, it will power on without checking for received power.";
		public static final String DESC_LAZY_FALLING_EDGE = "When enabled, the block is \"lazy\" on the falling edge. Whenever it is ticked, if it is powered, it will power off without checking for received power.";
		public static final String DESC_POWER_WEAK = "Weak power output.";
		public static final String DESC_POWER_STRONG = "Strong power output.";
		public static final String DESC_QC = "A list of all directions in which quasi connectivity for this block is enabled. If quasi connectivity is enabled in a direction then the block checks for power to its neighbor in that direction.";
		public static final String DESC_RANDOMIZE_QC = "When enabled, quasi connectivity works randomly in all directions where it is enabled.";
		public static final String DESC_TICK_PRIORITY = "The tick priority of any ticks scheduled by this block.";
		public static final String DESC_TICK_PRIORITY_FACING_DIODE = "The tick priority when facing another diode that is not facing it.";
		public static final String DESC_TICK_PRIORITY_RISING_EDGE = "The tick priority of ticks scheduled for the rising edge.";
		public static final String DESC_TICK_PRIORITY_FALLING_EDGE = "The tick priority of ticks scheduled for the falling edge.";
		
		// Values that are common between several settings
		public static final int MAX_DELAY = 1023;
		public static final int MAX_POWER = 1023;
	}
}
