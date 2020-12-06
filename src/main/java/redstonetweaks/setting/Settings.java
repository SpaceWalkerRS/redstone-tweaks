package redstonetweaks.setting;

import java.util.ArrayList;
import java.util.List;

import redstonetweaks.setting.preset.Preset;
import redstonetweaks.setting.preset.Presets;
import redstonetweaks.setting.types.ISetting;

public class Settings {
	
	public static final List<ISetting> ALL = new ArrayList<>();
	public static final List<SettingsCategory> CATEGORIES = new ArrayList<>();
	
	public static void registerCategory(SettingsCategory category) {
		CATEGORIES.add(category);
	}
	
	public static void registerPack(SettingsCategory category, SettingsPack pack) {
		category.getSettingsPacks().add(pack);
	}
	
	public static void register(SettingsCategory category, SettingsPack pack, ISetting setting) {
		setting.setId(category.getName() + '/' + pack.getName() + '/' + setting.getName());
		
		ALL.add(setting);
		category.getSettings().add(setting);
		pack.getSettings().add(setting);
	}
	
	public static SettingsCategory getCategoryFromName(String name) {
		for (SettingsCategory category : CATEGORIES) {
			if (category.getName().equals(name)) {
				return category;
			}
		}
		return null;
	}
	
	public static ISetting getSettingFromId(String id) {
		for (ISetting setting : ALL) {
			if (setting.getId().equals(id)) {
				return setting;
			}
		}
		return null;
	}
	
	public static void resetAll() {
		ALL.forEach((setting) -> setting.reset());
	}
	
	public static void enableAll() {
		ALL.forEach((setting) -> setting.setEnabled(true));
	}
	
	public static void disableAll() {
		ALL.forEach((setting) -> setting.setEnabled(false));
	}
	
	public static void lockAll() {
		ALL.forEach((setting) -> setting.setLocked(true));
	}
	
	public static void unlockAll() {
		ALL.forEach((setting) -> setting.setLocked(false));
	}
	
	public static void applyPreset(Preset preset) {
		ALL.forEach((setting) -> setting.applyPreset(preset));
	}
	
	public static void removePreset(Preset preset) {
		ALL.forEach((setting) -> setting.removePreset(preset));
	}
	
	public static void toDefault() {
		disableAll();
		unlockAll();
		resetAll();
	}
	
	public static void init() {
		Tweaks.init();
		ServerConfig.init();
		
		Presets.init();
		
		toDefault();
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
