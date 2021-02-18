package redstonetweaks.setting.preset;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.TickPriority;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.listeners.IPresetListener;
import redstonetweaks.setting.settings.ServerConfig;
import redstonetweaks.setting.settings.Settings;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.AbstractNeighborUpdate;
import redstonetweaks.world.common.UpdateOrder;
import redstonetweaks.world.common.WorldTickOptions;

public class Presets {
	
	private static final Map<Integer, Preset> ALL = new HashMap<>();
	private static final Map<String, Preset> ACTIVE = new LinkedHashMap<>();
	
	private static final Set<IPresetListener> LISTENERS = new HashSet<>();
	
	public static boolean register(Preset preset) {
		int id = preset.getId();
		
		if (ALL.containsKey(id) && ALL.get(id) != preset) {
			RedstoneTweaks.LOGGER.warn(String.format("Preset %s could not be registered, as a preset with id %d has already been registered.", preset.getName(), id));
			
			return false;
		}
		
		if (ALL.putIfAbsent(id, preset) == null) {
			RedstoneTweaks.LOGGER.info(String.format("Registered \'%s\' preset with id %d", preset.getName(), preset.getId()));
		}
		if (ACTIVE.putIfAbsent(preset.getName(), preset) == null) {
			presetAdded(preset);
		}
		
		return true;
	}
	
	public static void remove(Preset preset) {
		if (ACTIVE.remove(preset.getName(), preset)) {
			presetRemoved(preset);
		}
	}
	
	private static void removeAll() {
		ALL.values().forEach((preset) -> remove(preset));
	}

	public static boolean isNameValid(String name) {
		return name != null && !name.isEmpty();
	}
	
	public static boolean isNameAvailable(String name) {
		return !ACTIVE.containsKey(name);
	}
	
	public static Preset defaultPreset() {
		return fromId(0);
	}
	
	public static Preset fromId(int id) {
		return ALL.get(id);
	}
	
	public static Preset fromName(String name) {
		return ACTIVE.get(name);
	}
	
	public static Collection<Preset> getAllPresets() {
		return Collections.unmodifiableCollection(ALL.values());
	}
	
	public static Collection<Preset> getActivePresets() {
		return Collections.unmodifiableCollection(ACTIVE.values());
	}
	
	public static boolean isActive(Preset preset) {
		return ACTIVE.containsValue(preset);
	}
	
	// Only used on the client for creating new presets
	public static Preset create(String name, String description, Preset.Mode mode) {
		return new Preset(-Preset.nextId(), null, true, name, description, mode);
	}
	
	public static Preset fromIdOrCreate(int id, String name, String description, Preset.Mode mode) {
		Preset preset = fromId(id);
		
		return preset == null ? new Preset(id, null, true, name, description, mode) : preset;
	}
	
	public static PresetEditor newPreset(boolean fromSettings) {
		PresetEditor editor = editPreset(create("", "", Preset.Mode.SET));
		
		if (fromSettings) {
			for (ISetting setting : Settings.getSettings()) {
				if (!setting.isDefault()) {
					editor.addSetting(setting, true);
				}
			}
		}
		
		return editor;
	}
	
	public static PresetEditor duplicatePreset(Preset preset) {
		PresetEditor editor = editPreset(create(String.format("%s - copy", preset.getName()), preset.getDescription(), preset.getMode()));
		
		for (ISetting setting : Settings.getSettings()) {
			if (setting.hasPreset(preset)) {
				editor.addSetting(setting);
				editor.copyPresetValue(setting, preset);
			}
		}
		
		return editor;
	}
	
	public static PresetEditor editPreset(Preset preset) {
		return new PresetEditor(preset);
	}
	
	// Use on the server only
	public static void init() {
		Preset.resetIdCounter();
		
		Default.init();
		Bedrock.init();
		Debugging.init();
		Heaven.init();
		Hell.init();
		PistonMadness.init();
	}
	
	public static void reset() {
		removeAll();
		cleanUp();
	}

	public static void cleanUp() {
		ALL.values().removeIf((preset) -> {
			if (isActive(preset)) {
				return false;
			}
			
			preset.remove();
			
			return true;
		});
	}
	
	public static void addListener(IPresetListener listener) {
		LISTENERS.add(listener);
	}
	
	public static void removeListener(IPresetListener listener) {
		LISTENERS.remove(listener);
	}
	
	public static void clearListeners() {
		LISTENERS.clear();
	}
	
	public static void presetChanged(PresetEditor editor) {
		Preset preset = editor.getPreset();
		
		ACTIVE.values().remove(preset);
		register(preset);
		
		if (isActive(preset)) {
			LISTENERS.forEach((listener) -> listener.presetChanged(editor));
		}
	}
	
	public static void presetRemoved(Preset preset) {
		LISTENERS.forEach((listener) -> listener.presetRemoved(preset));
	}
	
	public static void presetAdded(Preset preset) {
		LISTENERS.forEach((listener) -> listener.presetAdded(preset));
	}
	
	private static class Default {
		
		private static void init() {
			Preset DEFAULT = new Preset(false, "Default", "The default values of all settings.", Preset.Mode.SET);
			
			Presets.register(DEFAULT);
			
			Tweaks.Global.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH));
			Tweaks.Global.COMPARATOR_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH));
			Tweaks.Global.SHAPE_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP));
			Tweaks.Global.CHAINSTONE.setPresetValue(DEFAULT, false);
			Tweaks.Global.DELAY_MULTIPLIER.setPresetValue(DEFAULT, 1);
			Tweaks.Global.DO_BLOCK_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DO_SHAPE_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DO_COMPARATOR_UPDATES.setPresetValue(DEFAULT, true);
			Tweaks.Global.DOUBLE_RETRACTION.setPresetValue(DEFAULT, false);
			Tweaks.Global.INSTANT_BLOCK_EVENTS.setPresetValue(DEFAULT, false);
			Tweaks.Global.MERGE_SLABS.setPresetValue(DEFAULT, false);
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(DEFAULT, false);
			Tweaks.Global.MOVABLE_MOVING_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.Global.POWER_MAX.setPresetValue(DEFAULT, 15);
			Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.setPresetValue(DEFAULT, false);
			Tweaks.Global.RANDOMIZE_DELAYS.setPresetValue(DEFAULT, false);
			Tweaks.Global.RANDOMIZE_TICK_PRIORITIES.setPresetValue(DEFAULT, false);
			Tweaks.Global.SHOW_NEIGHBOR_UPDATES.setPresetValue(DEFAULT, false);
			Tweaks.Global.SPONTANEOUS_EXPLOSIONS.setPresetValue(DEFAULT, false);
			Tweaks.Global.WORLD_TICK_OPTIONS.setPresetValue(DEFAULT, new WorldTickOptions());
			
			Tweaks.BugFixes.MC54711.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC120986.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC136566.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC137127.setPresetValue(DEFAULT, false);
			Tweaks.BugFixes.MC172213.setPresetValue(DEFAULT, false);
			
			Tweaks.ActivatorRail.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.ActivatorRail.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.ActivatorRail.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.POWER_LIMIT.setPresetValue(DEFAULT, 9);
			Tweaks.ActivatorRail.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.ActivatorRail.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.ActivatorRail.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.ActivatorRail.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Bamboo.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Bamboo.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Barrier.IS_MOVABLE.setPresetValue(DEFAULT, false);
			
			Tweaks.BubbleColumn.DELAY.setPresetValue(DEFAULT, 5);
			Tweaks.BubbleColumn.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Cactus.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Cactus.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.ChorusPlant.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.ChorusPlant.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.CommandBlock.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.CommandBlock.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.CommandBlock.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.CommandBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Comparator.ADDITION_MODE.setPresetValue(DEFAULT, false);
			Tweaks.Comparator.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Comparator.DELAY.setPresetValue(DEFAULT, 2);
			Tweaks.Comparator.MICRO_TICK_MODE.setPresetValue(DEFAULT, false);
			Tweaks.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.setPresetValue(DEFAULT, true);
			Tweaks.Comparator.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Comparator.TICK_PRIORITY_FACING_DIODE.setPresetValue(DEFAULT, TickPriority.HIGH);
			
			Tweaks.Composter.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.Composter.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Coral.DELAY_MIN.setPresetValue(DEFAULT, 60);
			Tweaks.Coral.DELAY_MAX.setPresetValue(DEFAULT, 100);
			Tweaks.Coral.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.CoralBlock.DELAY_MIN.setPresetValue(DEFAULT, 60);
			Tweaks.CoralBlock.DELAY_MAX.setPresetValue(DEFAULT, 100);
			Tweaks.CoralBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.DaylightDetector.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP));
			Tweaks.DaylightDetector.EMITS_STRONG_POWER.setPresetValue(DEFAULT, false);
			
			Tweaks.DetectorRail.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.DetectorRail.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.DetectorRail.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.DetectorRail.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Dispenser.DELAY.setPresetValue(DEFAULT, 4);
			Tweaks.Dispenser.LAZY.setPresetValue(DEFAULT, true);
			Tweaks.Dispenser.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.Dispenser.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Dispenser.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.DragonEgg.DELAY.setPresetValue(DEFAULT, 5);
			
			Tweaks.Dropper.DELAY.setPresetValue(DEFAULT, 4);
			Tweaks.Dropper.LAZY.setPresetValue(DEFAULT, true);
			Tweaks.Dropper.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.Dropper.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Dropper.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Farmland.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Farmland.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Fire.DELAY_MIN.setPresetValue(DEFAULT, 10);
			Tweaks.Fire.DELAY_MAX.setPresetValue(DEFAULT, 30);
			Tweaks.Fire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.FrostedIce.DELAY_MIN.setPresetValue(DEFAULT, 20);
			Tweaks.FrostedIce.DELAY_MAX.setPresetValue(DEFAULT, 40);
			Tweaks.FrostedIce.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.HayBale.DIRECTIONALLY_MOVABLE.setPresetValue(DEFAULT, false);
			
			Tweaks.GrassPath.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.GrassPath.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.GravityBlock.DELAY.setPresetValue(DEFAULT, 2);
			Tweaks.GravityBlock.SUSPENDED_BY_STICKY_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.GravityBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.HeavyWeightedPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.HeavyWeightedPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.HeavyWeightedPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 10);
			Tweaks.HeavyWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.HeavyWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.HeavyWeightedPressurePlate.WEIGHT.setPresetValue(DEFAULT, 150);
			
			Tweaks.Hopper.COOLDOWN_DEFAULT.setPresetValue(DEFAULT, 8);
			Tweaks.Hopper.COOLDOWN_PRIORITY.setPresetValue(DEFAULT, 7);
			Tweaks.Hopper.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Hopper.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Hopper.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.Hopper.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Hopper.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Hopper.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lava.DELAY_DEFAULT.setPresetValue(DEFAULT, 30);
			Tweaks.Lava.DELAY_NETHER.setPresetValue(DEFAULT, 10);
			Tweaks.Lava.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Leaves.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Leaves.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lectern.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lectern.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Lectern.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Lectern.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Lectern.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Lectern.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Lever.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.Lever.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lever.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.Lever.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Lever.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Lever.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Lever.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.LightWeightedPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.LightWeightedPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.LightWeightedPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 10);
			Tweaks.LightWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.LightWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.LightWeightedPressurePlate.WEIGHT.setPresetValue(DEFAULT, 15);
			
			Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.setPresetValue(DEFAULT, false);
			
			Tweaks.MagmaBlock.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.MagmaBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.NormalPiston.CAN_MOVE_SELF.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.NormalPiston.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.NormalPiston.HEAD_UPDATES_ON_EXTENSION.setPresetValue(DEFAULT, true);
			Tweaks.NormalPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(DEFAULT, true);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(DEFAULT, true);
			Tweaks.NormalPiston.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.LOOSE_HEAD.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.PUSH_LIMIT.setPresetValue(DEFAULT, 12);
			Tweaks.NormalPiston.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.NormalPiston.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.SPEED_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.NormalPiston.SPEED_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.NormalPiston.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.NormalPiston.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.NormalPiston.UPDATE_SELF.setPresetValue(DEFAULT, false);
			
			Tweaks.NoteBlock.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.NoteBlock.LAZY.setPresetValue(DEFAULT, false);
			Tweaks.NoteBlock.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.NoteBlock.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.NoteBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Observer.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Observer.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Observer.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Observer.DISABLE.setPresetValue(DEFAULT, false);
			Tweaks.Observer.OBSERVE_BLOCK_UPDATES.setPresetValue(DEFAULT, false);
			Tweaks.Observer.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Observer.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Observer.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.PoweredRail.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.PoweredRail.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.PoweredRail.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.POWER_LIMIT.setPresetValue(DEFAULT, 9);
			Tweaks.PoweredRail.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.PoweredRail.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.PoweredRail.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.PoweredRail.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Rail.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.Rail.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.Rail.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.Rail.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedSand.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN));
			Tweaks.RedSand.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.RedSand.POWER_WEAK.setPresetValue(DEFAULT, 0);
			Tweaks.RedSand.POWER_STRONG.setPresetValue(DEFAULT, 0);
			
			Tweaks.RedstoneBlock.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN));
			Tweaks.RedstoneBlock.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneBlock.POWER_STRONG.setPresetValue(DEFAULT, 0);
			
			Tweaks.RedstoneLamp.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneLamp.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 4);
			Tweaks.RedstoneLamp.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.RedstoneLamp.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneLamp.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneLamp.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneOre.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN));
			Tweaks.RedstoneOre.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneOre.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.POWER_WEAK.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.POWER_STRONG.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneOre.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneTorch.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneTorch.BURNOUT_COUNT.setPresetValue(DEFAULT, 8);
			Tweaks.RedstoneTorch.BURNOUT_TIMER.setPresetValue(DEFAULT, 60);
			Tweaks.RedstoneTorch.DELAY_BURNOUT.setPresetValue(DEFAULT, 160);
			Tweaks.RedstoneTorch.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.RedstoneTorch.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.RedstoneTorch.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneTorch.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.RedstoneTorch.SOFT_INVERSION.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneTorch.TICK_PRIORITY_BURNOUT.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneTorch.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.RedstoneTorch.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.RedstoneWire.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.LOCATIONAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
			Tweaks.RedstoneWire.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.RedstoneWire.INVERT_FLOW_ON_GLASS.setPresetValue(DEFAULT, false);
			Tweaks.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.setPresetValue(DEFAULT, true);
			Tweaks.RedstoneWire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Repeater.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
			Tweaks.Repeater.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Repeater.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.Repeater.LAZY_RISING_EDGE.setPresetValue(DEFAULT, true);
			Tweaks.Repeater.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.Repeater.MICRO_TICK_MODE.setPresetValue(DEFAULT, false);
			Tweaks.Repeater.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.Repeater.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.Repeater.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.HIGH);
			Tweaks.Repeater.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.VERY_HIGH);
			Tweaks.Repeater.TICK_PRIORITY_FACING_DIODE.setPresetValue(DEFAULT, TickPriority.EXTREMELY_HIGH);
			
			Tweaks.Scaffolding.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Scaffolding.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.SoulSand.DELAY.setPresetValue(DEFAULT, 20);
			Tweaks.SoulSand.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Stairs.FULL_FACES_ARE_SOLID.setPresetValue(DEFAULT, false);
			
			Tweaks.StickyPiston.CAN_MOVE_SELF.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.CONNECTS_TO_WIRE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.DO_BLOCK_DROPPING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.FAST_BLOCK_DROPPING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.SUPER_BLOCK_DROPPING.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StickyPiston.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StickyPiston.HEAD_UPDATES_ON_EXTENSION.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.HEAD_UPDATES_WHEN_PULLING.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(DEFAULT, true);
			Tweaks.StickyPiston.LAZY_RISING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.LAZY_FALLING_EDGE.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.LOOSE_HEAD.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.PUSH_LIMIT.setPresetValue(DEFAULT, 12);
			Tweaks.StickyPiston.QC.setPresetValue(DEFAULT, new Boolean[] {false, true, false, false, false, false});
			Tweaks.StickyPiston.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.SPEED_RISING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.StickyPiston.SPEED_FALLING_EDGE.setPresetValue(DEFAULT, 2);
			Tweaks.StickyPiston.SUPER_STICKY.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(DEFAULT, false);
			Tweaks.StickyPiston.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StickyPiston.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StickyPiston.UPDATE_SELF.setPresetValue(DEFAULT, false);
			
			Tweaks.StoneButton.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.StoneButton.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StoneButton.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.StoneButton.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.StoneButton.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.StoneButton.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StoneButton.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.StonePressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.StonePressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.StonePressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.StonePressurePlate.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.StonePressurePlate.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.StonePressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.StonePressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.SugarCane.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.SugarCane.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TargetBlock.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN));
			Tweaks.TargetBlock.DELAY_DEFAULT.setPresetValue(DEFAULT, 8);
			Tweaks.TargetBlock.DELAY_PERSISTENT_PROJECTILE.setPresetValue(DEFAULT, 20);
			Tweaks.TargetBlock.EMITS_STRONG_POWER.setPresetValue(DEFAULT, false);
			Tweaks.TargetBlock.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TNT.DELAY.setPresetValue(DEFAULT, 0);
			Tweaks.TNT.FUSE_TIME.setPresetValue(DEFAULT, 80);
			Tweaks.TNT.LAZY.setPresetValue(DEFAULT, false);
			Tweaks.TNT.QC.setPresetValue(DEFAULT, new Boolean[] {false, false, false, false, false, false});
			Tweaks.TNT.RANDOMIZE_QC.setPresetValue(DEFAULT, false);
			Tweaks.TNT.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Tripwire.DELAY.setPresetValue(DEFAULT, 10);
			Tweaks.Tripwire.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.TripwireHook.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.TripwireHook.DELAY.setPresetValue(DEFAULT, 10);
			Tweaks.TripwireHook.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.TripwireHook.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.TripwireHook.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Vines.DELAY.setPresetValue(DEFAULT, 1);
			Tweaks.Vines.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.Water.DELAY.setPresetValue(DEFAULT, 5);
			Tweaks.Water.TICK_PRIORITY.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.WhiteConcretePowder.IS_SOLID.setPresetValue(DEFAULT, true);
			
			Tweaks.WoodenButton.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
			Tweaks.WoodenButton.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.WoodenButton.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 30);
			Tweaks.WoodenButton.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenButton.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenButton.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.WoodenButton.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			Tweaks.WoodenPressurePlate.BLOCK_UPDATE_ORDER.setPresetValue(DEFAULT, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
			Tweaks.WoodenPressurePlate.DELAY_RISING_EDGE.setPresetValue(DEFAULT, 0);
			Tweaks.WoodenPressurePlate.DELAY_FALLING_EDGE.setPresetValue(DEFAULT, 20);
			Tweaks.WoodenPressurePlate.POWER_WEAK.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenPressurePlate.POWER_STRONG.setPresetValue(DEFAULT, 15);
			Tweaks.WoodenPressurePlate.TICK_PRIORITY_RISING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			Tweaks.WoodenPressurePlate.TICK_PRIORITY_FALLING_EDGE.setPresetValue(DEFAULT, TickPriority.NORMAL);
			
			
			ServerConfig.Permissions.EDIT_SETTINGS.setPresetValue(DEFAULT, true);
			ServerConfig.Permissions.EDIT_PRESETS.setPresetValue(DEFAULT, true);
			ServerConfig.Permissions.TICK_COMMAND.setPresetValue(DEFAULT, true);
			
			
			for (ISetting setting : Settings.getSettings()) {
				if (!setting.hasPreset(DEFAULT)) {
					RedstoneTweaks.LOGGER.warn(String.format("%s with id %s does not have a default value!", setting.getClass(), setting.getId()));
				}
			}
		}
	}
	
	private static class Bedrock {
		
		private static void init() {
			Preset BEDROCK = new Preset(false, "Bedrock", "Features or behaviors that are present in the Bedrock Edition of Minecraft.", Preset.Mode.SET);
			
			Presets.register(BEDROCK);
			
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(BEDROCK, true);
			Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.setPresetValue(BEDROCK, true);
			
			Tweaks.Dispenser.QC.setPresetValue(BEDROCK, new Boolean[] {false, false, false, false, false ,false});
			
			Tweaks.Dropper.QC.setPresetValue(BEDROCK, new Boolean[] {false, false, false, false, false ,false});
			
			Tweaks.NormalPiston.CONNECTS_TO_WIRE.setPresetValue(BEDROCK, true);
			Tweaks.NormalPiston.DELAY_RISING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.NormalPiston.DELAY_FALLING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(BEDROCK, true);
			Tweaks.NormalPiston.LAZY_RISING_EDGE.setPresetValue(BEDROCK, true);
			Tweaks.NormalPiston.QC.setPresetValue(BEDROCK, new Boolean[] {false, false, false, false, false, false});
			Tweaks.NormalPiston.SPEED_RISING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.NormalPiston.SPEED_FALLING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(BEDROCK, true);
			Tweaks.NormalPiston.UPDATE_SELF.setPresetValue(BEDROCK, true);
			
			Tweaks.Observer.DELAY_RISING_EDGE.setPresetValue(BEDROCK, 6);
			
			Tweaks.RedstoneTorch.SOFT_INVERSION.setPresetValue(BEDROCK, true);
			Tweaks.RedstoneTorch.TICK_PRIORITY_FALLING_EDGE.setPresetValue(BEDROCK, TickPriority.HIGH);
			
			Tweaks.StickyPiston.CONNECTS_TO_WIRE.setPresetValue(BEDROCK, true);
			Tweaks.StickyPiston.DELAY_RISING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.StickyPiston.DELAY_FALLING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.StickyPiston.DO_BLOCK_DROPPING.setPresetValue(BEDROCK, false);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(BEDROCK, true);
			Tweaks.StickyPiston.LAZY_RISING_EDGE.setPresetValue(BEDROCK, true);
			Tweaks.StickyPiston.QC.setPresetValue(BEDROCK, new Boolean[] {false, false, false, false, false, false});
			Tweaks.StickyPiston.SPEED_RISING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.StickyPiston.SPEED_FALLING_EDGE.setPresetValue(BEDROCK, 2);
			Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(BEDROCK, true);
			Tweaks.StickyPiston.UPDATE_SELF.setPresetValue(BEDROCK, true);
		}
	}
	
	private static class Debugging {
		
		private static void init() {
			Preset DEBUGGING = new Preset(false, "Debugging", "Debugging tools.", Preset.Mode.SET);
			
			Presets.register(DEBUGGING);
			
			Tweaks.Global.SHOW_NEIGHBOR_UPDATES.setPresetValue(DEBUGGING, true);
			Tweaks.Global.WORLD_TICK_OPTIONS.setPresetValue(DEBUGGING, new WorldTickOptions(WorldTickOptions.Mode.STEP_BY_STEP, WorldTickOptions.DimensionFilter.ACTIVE, 1));
		}
	}
	
	private static class Heaven {
		
		private static void init() {
			Preset HEAVEN = new Preset(false, "Heaven", "Bug fixes, quality of life changes and cool new features to make your redstoning experience a bliss.", Preset.Mode.SET);
			
			Presets.register(HEAVEN);
			
			Tweaks.Global.BLOCK_UPDATE_ORDER.setPresetValue(HEAVEN, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP));
			Tweaks.Global.CHAINSTONE.setPresetValue(HEAVEN, true);
			Tweaks.Global.DOUBLE_RETRACTION.setPresetValue(HEAVEN, true);
			Tweaks.Global.MERGE_SLABS.setPresetValue(HEAVEN, true);
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(HEAVEN, true);
			
			Tweaks.BugFixes.MC54711.setPresetValue(HEAVEN, true);
			Tweaks.BugFixes.MC120986.setPresetValue(HEAVEN, true);
			Tweaks.BugFixes.MC136566.setPresetValue(HEAVEN, true);
			Tweaks.BugFixes.MC137127.setPresetValue(HEAVEN, true);
			Tweaks.BugFixes.MC172213.setPresetValue(HEAVEN, true);
			
			Tweaks.Comparator.BLOCK_UPDATE_ORDER.setPresetValue(HEAVEN, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.LEFT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.RIGHT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.UP));
			
			Tweaks.HayBale.DIRECTIONALLY_MOVABLE.setPresetValue(HEAVEN, true);
			
			Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.setPresetValue(HEAVEN, true);
			
			Tweaks.NormalPiston.PUSH_LIMIT.setPresetValue(HEAVEN, 69);
			Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(HEAVEN, true);
			
			Tweaks.RedSand.CONNECTS_TO_WIRE.setPresetValue(HEAVEN, true);
			Tweaks.RedSand.POWER_WEAK.setPresetValue(HEAVEN, 15);
			
			Tweaks.RedstoneOre.CONNECTS_TO_WIRE.setPresetValue(HEAVEN, true);
			Tweaks.RedstoneOre.POWER_WEAK.setPresetValue(HEAVEN, 1);
			
			Tweaks.RedstoneTorch.BLOCK_UPDATE_ORDER.setPresetValue(HEAVEN, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN));
			Tweaks.RedstoneTorch.SOFT_INVERSION.setPresetValue(HEAVEN, true);
			
			Tweaks.RedstoneWire.BLOCK_UPDATE_ORDER.setPresetValue(HEAVEN, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.WEST, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.WEST, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.WEST, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.WEST, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.WEST, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.DOWN, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.DOWN, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.NORTH, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.UP, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.UP, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.UP, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SOUTH, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.DOWN, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.EAST, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.EAST, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.EAST, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.EAST, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.EAST, RelativePos.EAST).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.WEST).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.NORTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.UP).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.SOUTH).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.EAST));
			Tweaks.RedstoneWire.INVERT_FLOW_ON_GLASS.setPresetValue(HEAVEN, true);
			Tweaks.RedstoneWire.SLABS_ALLOW_UP_CONNECTION.setPresetValue(HEAVEN, false);
			
			Tweaks.Repeater.BLOCK_UPDATE_ORDER.setPresetValue(HEAVEN, new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.SEQUENTIAL).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.FRONT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.LEFT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.RIGHT).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.DOWN).
					add(AbstractNeighborUpdate.Mode.SINGLE_UPDATE, RelativePos.FRONT, RelativePos.UP));
			
			Tweaks.Stairs.FULL_FACES_ARE_SOLID.setPresetValue(HEAVEN, true);
			
			Tweaks.StickyPiston.PUSH_LIMIT.setPresetValue(HEAVEN, 69);
			Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(HEAVEN, true);
			
			Tweaks.WhiteConcretePowder.IS_SOLID.setPresetValue(HEAVEN, false);
		}
	}
	
	private static class Hell {
		
		private static void init() {
			Preset HELL = new Preset(false, "Hell", "The worst redstoning experience imaginable. Apply at your own risk.", Preset.Mode.SET);
			
			Presets.register(HELL);
			
			Tweaks.Global.BLOCK_UPDATE_ORDER.setPresetValue(HELL, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.RANDOM, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP));
			Tweaks.Global.COMPARATOR_UPDATE_ORDER.setPresetValue(HELL, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.RANDOM, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH));
			Tweaks.Global.SHAPE_UPDATE_ORDER.setPresetValue(HELL, new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.RANDOM, AbstractNeighborUpdate.Mode.SINGLE_UPDATE, true).
					add(RelativePos.SELF, RelativePos.WEST).
					add(RelativePos.SELF, RelativePos.EAST).
					add(RelativePos.SELF, RelativePos.NORTH).
					add(RelativePos.SELF, RelativePos.SOUTH).
					add(RelativePos.SELF, RelativePos.DOWN).
					add(RelativePos.SELF, RelativePos.UP));
			Tweaks.Global.POWER_MAX.setPresetValue(HELL, 50);
			Tweaks.Global.RANDOMIZE_BLOCK_EVENTS.setPresetValue(HELL, true);
			Tweaks.Global.RANDOMIZE_DELAYS.setPresetValue(HELL, true);
			Tweaks.Global.RANDOMIZE_TICK_PRIORITIES.setPresetValue(HELL, true);
			
			Tweaks.ActivatorRail.DELAY_RISING_EDGE.setPresetValue(HELL, 1);
			Tweaks.ActivatorRail.POWER_LIMIT.setPresetValue(HELL, 7);
			Tweaks.ActivatorRail.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.ActivatorRail.RANDOMIZE_QC.setPresetValue(HELL, true);
			Tweaks.ActivatorRail.TICK_PRIORITY_RISING_EDGE.setPresetValue(HELL, TickPriority.LOW);
			
			Tweaks.CommandBlock.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.CommandBlock.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT.setPresetValue(HELL, false);
			
			Tweaks.DetectorRail.POWER_STRONG.setPresetValue(HELL, 0);
			
			Tweaks.Dispenser.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.Dispenser.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.Dropper.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.Dropper.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.HeavyWeightedPressurePlate.WEIGHT.setPresetValue(HELL, 15);
			
			Tweaks.Hopper.COOLDOWN_DEFAULT.setPresetValue(HELL, 7);
			Tweaks.Hopper.COOLDOWN_PRIORITY.setPresetValue(HELL, 8);
			Tweaks.Hopper.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.Hopper.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.Lectern.POWER_WEAK.setPresetValue(HELL, 0);
			
			Tweaks.HeavyWeightedPressurePlate.WEIGHT.setPresetValue(HELL, 150);
			
			Tweaks.NormalPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(HELL, true);
			Tweaks.NormalPiston.DELAY_RISING_EDGE.setPresetValue(HELL, 2);
			Tweaks.NormalPiston.DELAY_FALLING_EDGE.setPresetValue(HELL, 2);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(HELL, true);
			Tweaks.NormalPiston.LOOSE_HEAD.setPresetValue(HELL, true);
			Tweaks.NormalPiston.PUSH_LIMIT.setPresetValue(HELL, 7);
			Tweaks.NormalPiston.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.NormalPiston.RANDOMIZE_QC.setPresetValue(HELL, true);
			Tweaks.NormalPiston.SPEED_RISING_EDGE.setPresetValue(HELL, 2);
			Tweaks.NormalPiston.SPEED_FALLING_EDGE.setPresetValue(HELL, 3);
			
			Tweaks.NoteBlock.DELAY.setPresetValue(HELL, 1);
			Tweaks.NoteBlock.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.NoteBlock.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.Observer.OBSERVE_BLOCK_UPDATES.setPresetValue(HELL, true);
			
			Tweaks.PoweredRail.DELAY_FALLING_EDGE.setPresetValue(HELL, 1);
			Tweaks.PoweredRail.POWER_LIMIT.setPresetValue(HELL, 7);
			Tweaks.PoweredRail.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.PoweredRail.RANDOMIZE_QC.setPresetValue(HELL, true);
			Tweaks.PoweredRail.TICK_PRIORITY_FALLING_EDGE.setPresetValue(HELL, TickPriority.LOW);
			
			Tweaks.Rail.DELAY.setPresetValue(HELL, 1);
			Tweaks.Rail.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.Rail.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.RedstoneBlock.POWER_WEAK.setPresetValue(HELL, 0);
			Tweaks.RedstoneBlock.POWER_STRONG.setPresetValue(HELL, 15);
			
			Tweaks.RedstoneLamp.DELAY_RISING_EDGE.setPresetValue(HELL, 4);
			Tweaks.RedstoneLamp.DELAY_FALLING_EDGE.setPresetValue(HELL, 0);
			Tweaks.RedstoneLamp.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.RedstoneLamp.RANDOMIZE_QC.setPresetValue(HELL, true);
			
			Tweaks.RedstoneTorch.BURNOUT_COUNT.setPresetValue(HELL, 4);
			Tweaks.RedstoneTorch.BURNOUT_TIMER.setPresetValue(HELL, 50);
			
			Tweaks.StickyPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(HELL, true);
			Tweaks.StickyPiston.DELAY_RISING_EDGE.setPresetValue(HELL, 2);
			Tweaks.StickyPiston.DELAY_FALLING_EDGE.setPresetValue(HELL, 2);
			Tweaks.StickyPiston.DO_BLOCK_DROPPING.setPresetValue(HELL, false);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.setPresetValue(HELL, true);
			Tweaks.StickyPiston.LOOSE_HEAD.setPresetValue(HELL, true);
			Tweaks.StickyPiston.PUSH_LIMIT.setPresetValue(HELL, 7);
			Tweaks.StickyPiston.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.StickyPiston.RANDOMIZE_QC.setPresetValue(HELL, true);
			Tweaks.StickyPiston.SPEED_RISING_EDGE.setPresetValue(HELL, 2);
			Tweaks.StickyPiston.SPEED_FALLING_EDGE.setPresetValue(HELL, 3);
			
			Tweaks.TNT.DELAY.setPresetValue(HELL, 5);
			Tweaks.TNT.FUSE_TIME.setPresetValue(HELL, 10);
			Tweaks.TNT.QC.setPresetValue(HELL, new Boolean[] {true, true, true, true, true, true});
			Tweaks.TNT.RANDOMIZE_QC.setPresetValue(HELL, true);
		}
	}
	
	private static class PistonMadness {
		
		private static void init() {
			Preset PISTON_MADNESS = new Preset(false, "Piston Madness", "Pistons as you've never seen them before! Sometimes in a good way, sometimes in a weird way...", Preset.Mode.SET);
			
			Presets.register(PISTON_MADNESS);
			
			Tweaks.Global.CHAINSTONE.setPresetValue(PISTON_MADNESS, true);
			Tweaks.Global.DOUBLE_RETRACTION.setPresetValue(PISTON_MADNESS, true);
			Tweaks.Global.MERGE_SLABS.setPresetValue(PISTON_MADNESS, true);
			Tweaks.Global.MOVABLE_BLOCK_ENTITIES.setPresetValue(PISTON_MADNESS, true);
			Tweaks.Global.MOVABLE_MOVING_BLOCKS.setPresetValue(PISTON_MADNESS, true);
			
			Tweaks.Barrier.IS_MOVABLE.setPresetValue(PISTON_MADNESS, true);
			
			Tweaks.HayBale.DIRECTIONALLY_MOVABLE.setPresetValue(PISTON_MADNESS, true);
			
			Tweaks.NormalPiston.CAN_MOVE_SELF.setPresetValue(PISTON_MADNESS, true);
			Tweaks.NormalPiston.CONNECTS_TO_WIRE.setPresetValue(PISTON_MADNESS, true);
			Tweaks.NormalPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(PISTON_MADNESS, false);
			Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(PISTON_MADNESS, false);
			Tweaks.NormalPiston.LOOSE_HEAD.setPresetValue(PISTON_MADNESS, true);
			Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(PISTON_MADNESS, true);
			Tweaks.NormalPiston.PUSH_LIMIT.setPresetValue(PISTON_MADNESS, 100);
			Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(PISTON_MADNESS, true);
			Tweaks.NormalPiston.UPDATE_SELF.setPresetValue(PISTON_MADNESS, true);
			
			Tweaks.StickyPiston.CAN_MOVE_SELF.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.CONNECTS_TO_WIRE.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.SUPER_BLOCK_DROPPING.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.HEAD_UPDATES_WHEN_PULLING.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.IGNORE_POWER_FROM_FRONT.setPresetValue(PISTON_MADNESS, false);
			Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.setPresetValue(PISTON_MADNESS, false);
			Tweaks.StickyPiston.LOOSE_HEAD.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.PUSH_LIMIT.setPresetValue(PISTON_MADNESS, 100);
			Tweaks.StickyPiston.SUPER_STICKY.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.setPresetValue(PISTON_MADNESS, true);
			Tweaks.StickyPiston.UPDATE_SELF.setPresetValue(PISTON_MADNESS, true);
		}
	}
}

