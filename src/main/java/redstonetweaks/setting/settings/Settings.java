package redstonetweaks.setting.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.listeners.ISettingListener;
import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.types.ISetting;

public class Settings {
	
	private static final Map<String, ISetting> ALL = new HashMap<>();
	private static final Map<String, SettingsPack> PACKS = new HashMap<>();
	private static final Map<String, SettingsCategory> CATEGORIES = new LinkedHashMap<>();
	
	private static final Set<ISettingListener> LISTENERS = new HashSet<>();
	
	public static void register(SettingsCategory category) {
		if (CATEGORIES.putIfAbsent(category.getName(), category) != null) {
			RedstoneTweaks.LOGGER.warn(String.format("SettingsCategory %s could not be registered, as a category with that name already exists.", category.getName()));
		} else {
			RedstoneTweaks.LOGGER.info(String.format("Initializing \'%s\' settings", category.getName()));
		}
	}
	
	public static void register(SettingsPack pack) {
		if (PACKS.putIfAbsent(pack.getId(), pack) != null || !pack.getCategory().addPack(pack)) {
			RedstoneTweaks.LOGGER.warn(String.format("SettingsPack %s could not be registered, as a pack with id %s already exists.", pack.getName(), pack.getId()));
		}
	}
	
	public static void register(ISetting setting) {
		if (ALL.putIfAbsent(setting.getId(), setting) != null || !setting.getPack().addSetting(setting)) {
			RedstoneTweaks.LOGGER.warn(String.format("%s %s could not be registered, as a setting with id %s already exists.", setting.getClass(), setting.getName(), setting.getId()));
		}
	}
	
	public static Collection<SettingsCategory> getCategories() {
		return Collections.unmodifiableCollection(CATEGORIES.values());
	}
	
	public static Collection<SettingsPack> getPacks() {
		return Collections.unmodifiableCollection(PACKS.values());
	}
	
	public static Collection<ISetting> getSettings() {
		return Collections.unmodifiableCollection(ALL.values());
	}
	
	public static SettingsCategory getCategoryFromName(String name) {
		return CATEGORIES.get(name);
	}
	
	public static SettingsPack getPackFromId(String id) {
		return PACKS.get(id);
	}
	
	public static ISetting getSettingFromId(String id) {
		return ALL.get(id);
	}
	
	public static void init() {
		Tweaks.init();
		ServerConfig.init();
		
		RedstoneTweaks.LOGGER.info(String.format("Initialized %d Redstone Tweaks settings", ALL.size()));
	}

	public static void toDefault() {
		disableAll();
		unlockAll();
		resetAll();
	}
	
	public static void resetAll() {
		ALL.values().forEach((setting) -> setting.reset());
	}
	
	public static void enableAll() {
		ALL.values().forEach((setting) -> setting.setEnabled(true));
	}
	
	public static void disableAll() {
		ALL.values().forEach((setting) -> setting.setEnabled(false));
	}
	
	public static void lockAll() {
		ALL.values().forEach((setting) -> setting.setLocked(true));
	}
	
	public static void unlockAll() {
		ALL.values().forEach((setting) -> setting.setLocked(false));
	}
	
	public static void applyPreset(Preset preset) {
		ALL.values().forEach((setting) -> setting.applyPreset(preset));
	}
	
	public static void removePreset(Preset preset) {
		ALL.values().forEach((setting) -> setting.removePreset(preset));
	}
	
	public static void clearPresets() {
		ALL.values().forEach((setting) -> setting.clearPresets());
	}
	
	public static void addListener(ISettingListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(ISettingListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void clearListeners() {
		LISTENERS.clear();
	}
	
	public static void categoryLockedChanged(SettingsCategory category) {
		LISTENERS.forEach((listener) -> listener.categoryLockedChanged(category));
	}
	
	public static void packLockedChanged(SettingsPack pack) {
		LISTENERS.forEach((listener) -> listener.packLockedChanged(pack));
	}
	
	public static void settingLockedChanged(ISetting setting) {
		LISTENERS.forEach((listener) -> listener.settingLockedChanged(setting));
	}
	
	public static void settingValueChanged(ISetting setting) {
		LISTENERS.forEach((listener) -> listener.settingValueChanged(setting));
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
		public static final String DESC_MICRO_TICK_MODE = "When enabled, this block updates using block events instead of scheduled ticks.";
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
