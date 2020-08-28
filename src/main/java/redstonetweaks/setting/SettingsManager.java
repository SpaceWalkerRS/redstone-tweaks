package redstonetweaks.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;

public abstract class SettingsManager {
	
	public static final Map<String, Setting<?>> SETTINGS = new HashMap<>();
	public static final Map<String, SettingsPack> SETTINGS_PACKS = new HashMap<>();
	public static final Map<Setting<?>, ArrayList<SettingsPack>> SETTINGS_PACKS_BY_SETTING = new HashMap<>();
	public static final Map<Block, SettingsPack> BLOCK_TO_SETTINGS_PACK = new HashMap<>();
	public static final Map<Direction, Setting<BooleanProperty>> DIRECTION_TO_QC_SETTING = new HashMap<>();
	
	public static final Setting<BooleanProperty> ADDITION_MODE = register(new Setting<>("additionMode"));
	public static final Setting<IntegerProperty> BURNOUT_COUNT = register(new Setting<>("burnoutCount"));
	public static final Setting<IntegerProperty> BURNOUT_DELAY = register(new Setting<>("burnoutDelay"));
	public static final Setting<IntegerProperty> BURNOUT_TIMER = register(new Setting<>("burnoutTimer"));
	public static final Setting<BooleanProperty> CONNECTS_TO_WIRE = register(new Setting<>("connectsToWire"));
	public static final Setting<IntegerProperty> COOLDOWN = register(new Setting<>("cooldown"));
	public static final Setting<IntegerProperty> PRIORITY_COOLDOWN = register(new Setting<>("priorityCooldown"));
	public static final Setting<IntegerProperty> DELAY = register(new Setting<>("delay"));
	public static final Setting<IntegerProperty> NETHER_DELAY = register(new Setting<>("netherDelay"));
	public static final Setting<IntegerProperty> FALLING_DELAY = register(new Setting<>("fallingEdgeDelay"));
	public static final Setting<IntegerProperty> RISING_DELAY = register(new Setting<>("risingEdgeDelay"));
	public static final Setting<IntegerProperty> PERSISTENT_PROJECTILE_DELAY = register(new Setting<>("persistentProjectileDelay"));
	public static final Setting<BooleanProperty> RANDOMIZE_SCHEDULED_TICK_DELAYS = register(new Setting<>("randomizeScheduledTickDelays"));
	public static final Setting<IntegerProperty> DELAY_MULTIPLIER = register(new Setting<>("delayMultiplier"));
	public static final Setting<BooleanProperty> DISABLE = register(new Setting<>("disable"));
	public static final Setting<BooleanProperty> DIRECTIONAL_UPDATE_ORDER = register(new Setting<>("directionalUpdateOrder"));
	public static final Setting<BooleanProperty> DO_BLOCK_DROPPING = register(new Setting<>("doBlockDropping"));
	public static final Setting<BooleanProperty> DO_BLOCK_UPDATES = register(new Setting<>("doBlockUpdates"));
	public static final Setting<BooleanProperty> DO_STATE_UPDATES = register(new Setting<>("doStateUpdates"));
	public static final Setting<BooleanProperty> DO_COMPARATOR_UPDATES = register(new Setting<>("doComparatorUpdates"));
	public static final Setting<BooleanProperty> DOUBLE_RETRACTION = register(new Setting<>("doubleRetraction"));
	public static final Setting<BooleanProperty> FAST_BLOCK_DROPPING = register(new Setting<>("fastBlockDropping"));
	public static final Setting<BooleanProperty> FORCE_UPDATE_WHEN_POWERED = register(new Setting<>("forceUpdateWhenPowered"));
	public static final Setting<IntegerProperty> FUSE_TIME = register(new Setting<>("fuseTime"));
	public static final Setting<BooleanProperty> HEAD_UPDATES_ON_EXTENSION = register(new Setting<>("headUpdatesOnExtension"));
	public static final Setting<BooleanProperty> IGNORE_UPDATES_WHEN_EXTENDING = register(new Setting<>("ignoreUpdatesWhenExtending"));
	public static final Setting<BooleanProperty> IS_POWER_DIODE = register(new Setting<>("isPowerDiode"));
	public static final Setting<BooleanProperty> INVERT_FLOW_ON_GLASS = register(new Setting<>("invertFlowOnGlass"));
	public static final Setting<BooleanProperty> LAZY = register(new Setting<>("lazy"));
	public static final Setting<BooleanProperty> FALLING_LAZY = register(new Setting<>("fallingLazy"));
	public static final Setting<BooleanProperty> RISING_LAZY = register(new Setting<>("risingLazy"));
	public static final Setting<BooleanProperty> MC54711 = register(new Setting<>("MC-54711"));
	public static final Setting<BooleanProperty> MC120986 = register(new Setting<>("MC-120986"));
	public static final Setting<BooleanProperty> MC136566 = register(new Setting<>("MC-136566"));
	public static final Setting<BooleanProperty> MC137127 = register(new Setting<>("MC-137127"));
	public static final Setting<BooleanProperty> MC172213 = register(new Setting<>("MC-172213"));
	public static final Setting<IntegerProperty> POWER = register(new Setting<>("power"));
	public static final Setting<IntegerProperty> STRONG_POWER = register(new Setting<>("strongPower"));
	public static final Setting<IntegerProperty> WEAK_POWER = register(new Setting<>("weakPower"));
	public static final Setting<IntegerProperty> POWER_LIMIT = register(new Setting<>("powerLimit"));
	public static final Setting<IntegerProperty> PUSH_LIMIT = register(new Setting<>("pushLimit"));
	public static final Setting<BooleanProperty> QC_DOWN = register(new Setting<>("quasiConnectivityDown"));
	public static final Setting<BooleanProperty> QC_EAST = register(new Setting<>("quasiConnectivityEast"));
	public static final Setting<BooleanProperty> QC_NORTH = register(new Setting<>("quasiConnectivityNorth"));
	public static final Setting<BooleanProperty> QC_SOUTH = register(new Setting<>("quasiConnectivitySouth"));
	public static final Setting<BooleanProperty> QC_UP = register(new Setting<>("quasiConnectivityUp"));
	public static final Setting<BooleanProperty> QC_WEST = register(new Setting<>("quasiConnectivityWest"));
	public static final Setting<BooleanProperty> RANDOMIZE_QC = register(new Setting<>("randomizeQuasiConnectivity"));
	public static final Setting<BooleanProperty> RANDOMIZE_BLOCK_EVENTS = register(new Setting<>("randomizeBlockEvents"));
	public static final Setting<BooleanProperty> RANDOM_UPDATE_ORDER = register(new Setting<>("randomUpdateOrder"));
	public static final Setting<BooleanProperty> REDSTONE_BLOCKS_POWER_SIDES = register(new Setting<>("redstoneBlocksPowerSides"));
	public static final Setting<BooleanProperty> SHOW_NEIGHBOR_UPDATES = register(new Setting<>("showNeighborUpdates"));
	public static final Setting<IntegerProperty> SHOW_PROCESSING_ORDER = register(new Setting<>("showProcessingOrder"));
	public static final Setting<BooleanProperty> SLABS_ALLOW_UP_CONNECTION = register(new Setting<>("slabsAllowUpConnection"));
	public static final Setting<BooleanProperty> SOFT_INVERSION = register(new Setting<>("softInversion"));
	public static final Setting<IntegerProperty> SPEED = register(new Setting<>("speed"));
	public static final Setting<IntegerProperty> FALLING_SPEED = register(new Setting<>("fallingEdgeSpeed"));
	public static final Setting<IntegerProperty> RISING_SPEED = register(new Setting<>("risingEdgeSpeed"));
	public static final Setting<BooleanProperty> SUPPORTS_BRITTLE_BLOCKS = register(new Setting<>("supportsBrittleBlocks"));
	public static final Setting<TickPriorityProperty> TICK_PRIORITY = register(new Setting<>("tickPriority"));
	public static final Setting<TickPriorityProperty> BURNOUT_TICK_PRIORITY = register(new Setting<>("burnoutTickPriority"));
	public static final Setting<TickPriorityProperty> FACING_DIODE_TICK_PRIORITY = register(new Setting<>("facingDiodeTickPriority"));
	public static final Setting<TickPriorityProperty> FALLING_TICK_PRIORITY = register(new Setting<>("fallingEdgeTickPriority"));
	public static final Setting<TickPriorityProperty> RISING_TICK_PRIORITY = register(new Setting<>("risingEdgeTickPriority"));
	public static final Setting<TickPriorityProperty> PERSISTENT_PROJECTILE_TICK_PRIORITY = register(new Setting<>("persistentProjectileTickPriority"));
	public static final Setting<BooleanProperty> RANDOMIZE_TICK_PRIORITY = register(new Setting<>("randomizeTickPriority"));
	public static final Setting<BooleanProperty> RANDOMIZE_TICK_PRIORITIES = register(new Setting<>("randomizeTickPriorities"));
	public static final Setting<IntegerProperty> WEIGHT = register(new Setting<>("weight"));
	
	public static final Setting<BooleanProperty> EXPERIMENTAL_SET_BLOCK_STATE = register(new Setting<>("EXPERIMENTAL_SET_BLOCK_STATE"));
	
	public static final SettingsPack ACTIVATOR_RAIL = register(new SettingsPack("activator_rail"), Blocks.ACTIVATOR_RAIL);
	public static final SettingsPack BUBBLE_COLUMN = register(new SettingsPack("bubble_column"), Blocks.BUBBLE_COLUMN);
	public static final SettingsPack COMMAND_BLOCK = register(new SettingsPack("command_block"));
	public static final SettingsPack COMPARATOR = register(new SettingsPack("comparator"), Blocks.COMPARATOR);
	public static final SettingsPack DETECTOR_RAIL = register(new SettingsPack("detector_rail"), Blocks.DETECTOR_RAIL);
	public static final SettingsPack DISPENSER = register(new SettingsPack("dispenser"), Blocks.DISPENSER);
	public static final SettingsPack DROPPER = register(new SettingsPack("dropper"), Blocks.DROPPER);
	public static final SettingsPack GRAVITY_BLOCK = register(new SettingsPack("gravity_block"));
	public static final SettingsPack HEAVY_WEIGHTED_PRESSURE_PLATE = register(new SettingsPack("heavy_weighted_pressure_plate"), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
	public static final SettingsPack HOPPER = register(new SettingsPack("hopper"), Blocks.HOPPER);
	public static final SettingsPack LAVA = register(new SettingsPack("lava"), Blocks.LAVA);
	public static final SettingsPack LEAVES = register(new SettingsPack("leaves"));
	public static final SettingsPack LEVER = register(new SettingsPack("lever"), Blocks.LEVER);
	public static final SettingsPack LIGHT_WEIGHTED_PRESSURE_PLATE = register(new SettingsPack("light_weighted_pressure_plate"), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
	public static final SettingsPack MAGENTA_GLAZED_TERRACOTTA = register(new SettingsPack("magenta_glazed_terracotta"), Blocks.MAGENTA_GLAZED_TERRACOTTA);
	public static final SettingsPack MAGMA_BLOCK = register(new SettingsPack("magma_block"), Blocks.MAGMA_BLOCK);
	public static final SettingsPack NORMAL_PISTON = register(new SettingsPack("normal_piston"), Blocks.PISTON);
	public static final SettingsPack NOTE_BLOCK = register(new SettingsPack("note_block"), Blocks.NOTE_BLOCK);
	public static final SettingsPack OBSERVER = register(new SettingsPack("observer"), Blocks.OBSERVER);
	public static final SettingsPack POWERED_RAIL = register(new SettingsPack("powered_rail"), Blocks.POWERED_RAIL);
	public static final SettingsPack REDSTONE_BLOCK = register(new SettingsPack("redstone_block"), Blocks.REDSTONE_BLOCK);
	public static final SettingsPack REDSTONE_LAMP = register(new SettingsPack("redstone_lamp"), Blocks.REDSTONE_LAMP);
	public static final SettingsPack REDSTONE_TORCH = register(new SettingsPack("redstone_torch"), Blocks.REDSTONE_TORCH);
	public static final SettingsPack REDSTONE_WIRE = register(new SettingsPack("redstone_wire"), Blocks.REDSTONE_WIRE);
	public static final SettingsPack REPEATER = register(new SettingsPack("repeater"), Blocks.REPEATER);
	public static final SettingsPack SCAFFOLDING = register(new SettingsPack("scaffolding"), Blocks.SCAFFOLDING);
	public static final SettingsPack SOUL_SAND = register(new SettingsPack("soul_sand"), Blocks.SOUL_SAND);
	public static final SettingsPack STICKY_PISTON = register(new SettingsPack("sticky_piston"), Blocks.STICKY_PISTON);
	public static final SettingsPack STONE_BUTTON = register(new SettingsPack("stone_button"));
	public static final SettingsPack STONE_PRESSURE_PLATE = register(new SettingsPack("stone_pressure_plate"));
	public static final SettingsPack TARGET_BLOCK = register(new SettingsPack("target_block"), Blocks.TARGET);
	public static final SettingsPack TNT = register(new SettingsPack("tnt"), Blocks.TNT);
	public static final SettingsPack TRIPWIRE_HOOK = register(new SettingsPack("tripwire_hook"), Blocks.TRIPWIRE_HOOK);
	public static final SettingsPack TRIPWIRE = register(new SettingsPack("tripwire"), Blocks.TRIPWIRE);
	public static final SettingsPack WATER = register(new SettingsPack("water"), Blocks.WATER);
	public static final SettingsPack WOODEN_BUTTON = register(new SettingsPack("wooden_button"));
	public static final SettingsPack WOODEN_PRESSURE_PLATE = register(new SettingsPack("wooden_pressure_plate"));
	
	public static final SettingsPack BUG_FIXES = register(new SettingsPack("bug_fixes"));
	public static final SettingsPack GLOBAL = register(new SettingsPack("global"));
	
	public static <T extends Property<?>> Setting<T> register(Setting<T> setting) {
		SETTINGS.put(setting.getName(), setting);
		SETTINGS_PACKS_BY_SETTING.put(setting, new ArrayList<SettingsPack>());
		return setting;
	}
	
	public static SettingsPack register(SettingsPack pack) {
		SETTINGS_PACKS.put(pack.getName(), pack);
		return pack;
	}
	
	public static SettingsPack register(SettingsPack pack, Block block) {
		BLOCK_TO_SETTINGS_PACK.put(block, pack);
		return register(pack);
	}
	
	public static <T extends Property<?>> void register(SettingsPack oack, Setting<T> setting, T property) {
		oack.register(setting, property);
		SETTINGS_PACKS_BY_SETTING.get(setting).add(oack);
	}
	
	public abstract <T> void updateSetting(SettingsPack pack, Setting<? extends Property<T>> setting, T value);
	
	public <T> void resetSetting(SettingsPack pack, Setting<? extends Property<T>> setting) {
		updateSetting(pack, setting, pack.getDefault(setting));
	}
	
	static {
		DIRECTION_TO_QC_SETTING.put(Direction.DOWN, QC_DOWN);
		DIRECTION_TO_QC_SETTING.put(Direction.EAST, QC_EAST);
		DIRECTION_TO_QC_SETTING.put(Direction.NORTH, QC_NORTH);
		DIRECTION_TO_QC_SETTING.put(Direction.SOUTH, QC_SOUTH);
		DIRECTION_TO_QC_SETTING.put(Direction.WEST, QC_WEST);
		DIRECTION_TO_QC_SETTING.put(Direction.UP, QC_UP);
		
		register(ACTIVATOR_RAIL, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(ACTIVATOR_RAIL, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(ACTIVATOR_RAIL, FALLING_LAZY, new BooleanProperty(false));
		register(ACTIVATOR_RAIL, RISING_LAZY, new BooleanProperty(false));
		register(ACTIVATOR_RAIL, POWER_LIMIT, new IntegerProperty(9, 1, 127));
		register(ACTIVATOR_RAIL, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(ACTIVATOR_RAIL, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(BUBBLE_COLUMN, DELAY, new IntegerProperty(5, 1, 127));
		register(BUBBLE_COLUMN, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(COMMAND_BLOCK, DELAY, new IntegerProperty(1, 1, 127));
		register(COMMAND_BLOCK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(COMPARATOR, ADDITION_MODE, new BooleanProperty(false));
		register(COMPARATOR, DELAY, new IntegerProperty(2, 0, 127));
		register(COMPARATOR, REDSTONE_BLOCKS_POWER_SIDES, new BooleanProperty(true));
		register(COMPARATOR, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(COMPARATOR, FACING_DIODE_TICK_PRIORITY, new TickPriorityProperty(TickPriority.HIGH));
		
		register(DETECTOR_RAIL, DELAY, new IntegerProperty(20, 1, 127));
		register(DETECTOR_RAIL, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(DETECTOR_RAIL, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(DETECTOR_RAIL, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(DISPENSER, DELAY, new IntegerProperty(4, 0, 127));
		register(DISPENSER, LAZY, new BooleanProperty(true));
		register(DISPENSER, QC_DOWN, new BooleanProperty(false));
		register(DISPENSER, QC_EAST, new BooleanProperty(false));
		register(DISPENSER, QC_NORTH, new BooleanProperty(false));
		register(DISPENSER, QC_SOUTH, new BooleanProperty(false));
		register(DISPENSER, QC_UP, new BooleanProperty(true));
		register(DISPENSER, QC_WEST, new BooleanProperty(false));
		register(DISPENSER, RANDOMIZE_QC, new BooleanProperty(false));
		register(DISPENSER, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(DROPPER, DELAY, new IntegerProperty(4, 0, 127));
		register(DROPPER, LAZY, new BooleanProperty(true));
		register(DROPPER, QC_DOWN, new BooleanProperty(false));
		register(DROPPER, QC_EAST, new BooleanProperty(false));
		register(DROPPER, QC_NORTH, new BooleanProperty(false));
		register(DROPPER, QC_SOUTH, new BooleanProperty(false));
		register(DROPPER, QC_UP, new BooleanProperty(true));
		register(DROPPER, QC_WEST, new BooleanProperty(false));
		register(DROPPER, RANDOMIZE_QC, new BooleanProperty(false));
		register(DROPPER, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(GRAVITY_BLOCK, DELAY, new IntegerProperty(2, 0, 127));
		register(GRAVITY_BLOCK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(HEAVY_WEIGHTED_PRESSURE_PLATE, FALLING_DELAY, new IntegerProperty(10, 1, 127));
		register(HEAVY_WEIGHTED_PRESSURE_PLATE, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(HEAVY_WEIGHTED_PRESSURE_PLATE, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(HEAVY_WEIGHTED_PRESSURE_PLATE, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(HEAVY_WEIGHTED_PRESSURE_PLATE, WEIGHT, new IntegerProperty(150, 1, 1023));
		
		register(HOPPER, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(HOPPER, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(HOPPER, COOLDOWN, new IntegerProperty(8, 0, 127));
		register(HOPPER, PRIORITY_COOLDOWN, new IntegerProperty(7, 0, 127));
		register(HOPPER, FALLING_LAZY, new BooleanProperty(false));
		register(HOPPER, RISING_LAZY, new BooleanProperty(false));
		register(HOPPER, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(HOPPER, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(LAVA, DELAY, new IntegerProperty(30, 0, 127));
		register(LAVA, NETHER_DELAY, new IntegerProperty(10, 0, 127));
		register(LAVA, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(LEAVES, DELAY, new IntegerProperty(1, 0, 127));
		register(LEAVES, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(LEVER, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(LEVER, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(LEVER, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(LEVER, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(LEVER, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(LEVER, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(LIGHT_WEIGHTED_PRESSURE_PLATE, FALLING_DELAY, new IntegerProperty(10, 1, 127));
		register(LIGHT_WEIGHTED_PRESSURE_PLATE, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(LIGHT_WEIGHTED_PRESSURE_PLATE, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(LIGHT_WEIGHTED_PRESSURE_PLATE, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(LIGHT_WEIGHTED_PRESSURE_PLATE, WEIGHT, new IntegerProperty(15, 1, 1023));
		
		register(MAGENTA_GLAZED_TERRACOTTA, IS_POWER_DIODE, new BooleanProperty(false));
		
		register(MAGMA_BLOCK, DELAY, new IntegerProperty(20, 0, 127));
		register(MAGMA_BLOCK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(NORMAL_PISTON, CONNECTS_TO_WIRE, new BooleanProperty(false));
		register(NORMAL_PISTON, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(NORMAL_PISTON, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(NORMAL_PISTON, SUPPORTS_BRITTLE_BLOCKS, new BooleanProperty(false));
		register(NORMAL_PISTON, FORCE_UPDATE_WHEN_POWERED, new BooleanProperty(false));
		register(NORMAL_PISTON, HEAD_UPDATES_ON_EXTENSION, new BooleanProperty(true));
		register(NORMAL_PISTON, IGNORE_UPDATES_WHEN_EXTENDING, new BooleanProperty(false));
		register(NORMAL_PISTON, FALLING_LAZY, new BooleanProperty(false));
		register(NORMAL_PISTON, RISING_LAZY, new BooleanProperty(false));
		register(NORMAL_PISTON, QC_DOWN, new BooleanProperty(false));
		register(NORMAL_PISTON, QC_EAST, new BooleanProperty(false));
		register(NORMAL_PISTON, QC_NORTH, new BooleanProperty(false));
		register(NORMAL_PISTON, QC_SOUTH, new BooleanProperty(false));
		register(NORMAL_PISTON, QC_UP, new BooleanProperty(true));
		register(NORMAL_PISTON, QC_WEST, new BooleanProperty(false));
		register(NORMAL_PISTON, RANDOMIZE_QC, new BooleanProperty(false));
		register(NORMAL_PISTON, FALLING_SPEED, new IntegerProperty(2, 0, 127));
		register(NORMAL_PISTON, RISING_SPEED, new IntegerProperty(2, 0, 127));
		register(NORMAL_PISTON, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(NORMAL_PISTON, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(NORMAL_PISTON, PUSH_LIMIT, new IntegerProperty(12, 0, 127));
		
		register(NOTE_BLOCK, DELAY, new IntegerProperty(0, 0, 127));
		register(NOTE_BLOCK, LAZY, new BooleanProperty(false));
		register(NOTE_BLOCK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(OBSERVER, FALLING_DELAY, new IntegerProperty(2, 0, 127));
		register(OBSERVER, RISING_DELAY, new IntegerProperty(2, 0, 127));
		register(OBSERVER, DISABLE, new BooleanProperty(false));
		register(OBSERVER, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(OBSERVER, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(OBSERVER, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(OBSERVER, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(POWERED_RAIL, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(POWERED_RAIL, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(POWERED_RAIL, FALLING_LAZY, new BooleanProperty(false));
		register(POWERED_RAIL, RISING_LAZY, new BooleanProperty(false));
		register(POWERED_RAIL, POWER_LIMIT, new IntegerProperty(9, 1, 127));
		register(POWERED_RAIL, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(POWERED_RAIL, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(REDSTONE_BLOCK, STRONG_POWER, new IntegerProperty(0, 0, 15));
		register(REDSTONE_BLOCK, WEAK_POWER, new IntegerProperty(15, 0, 15));
		
		register(REDSTONE_LAMP, FALLING_DELAY, new IntegerProperty(4, 0, 127));
		register(REDSTONE_LAMP, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(REDSTONE_LAMP, FALLING_LAZY, new BooleanProperty(false));
		register(REDSTONE_LAMP, RISING_LAZY, new BooleanProperty(false));
		register(REDSTONE_LAMP, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(REDSTONE_LAMP, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(REDSTONE_TORCH, BURNOUT_COUNT, new IntegerProperty(8, 0, 127));
		register(REDSTONE_TORCH, BURNOUT_DELAY, new IntegerProperty(160, 0, 1023));
		register(REDSTONE_TORCH, BURNOUT_TIMER, new IntegerProperty(60, 0, 127));
		register(REDSTONE_TORCH, FALLING_DELAY, new IntegerProperty(2, 0, 127));
		register(REDSTONE_TORCH, RISING_DELAY, new IntegerProperty(2, 0, 127));
		register(REDSTONE_TORCH, FALLING_LAZY, new BooleanProperty(false));
		register(REDSTONE_TORCH, RISING_LAZY, new BooleanProperty(false));
		register(REDSTONE_TORCH, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(REDSTONE_TORCH, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(REDSTONE_TORCH, SOFT_INVERSION, new BooleanProperty(false));
		register(REDSTONE_TORCH, BURNOUT_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(REDSTONE_TORCH, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(REDSTONE_TORCH, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(REDSTONE_WIRE, DELAY, new IntegerProperty(0, 0, 127));
		register(REDSTONE_WIRE, DIRECTIONAL_UPDATE_ORDER, new BooleanProperty(false));
		register(REDSTONE_WIRE, INVERT_FLOW_ON_GLASS, new BooleanProperty(false));
		register(REDSTONE_WIRE, RANDOM_UPDATE_ORDER, new BooleanProperty(false));
		register(REDSTONE_WIRE, SLABS_ALLOW_UP_CONNECTION, new BooleanProperty(true));
		register(REDSTONE_WIRE, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(REPEATER, FALLING_DELAY, new IntegerProperty(2, 0, 127));
		register(REPEATER, RISING_DELAY, new IntegerProperty(2, 0, 127));
		register(REPEATER, FALLING_LAZY, new BooleanProperty(false));
		register(REPEATER, RISING_LAZY, new BooleanProperty(true));
		register(REPEATER, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(REPEATER, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(REPEATER, FACING_DIODE_TICK_PRIORITY, new TickPriorityProperty(TickPriority.EXTREMELY_HIGH));
		register(REPEATER, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.VERY_HIGH));
		register(REPEATER, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.HIGH));
		
		register(SCAFFOLDING, DELAY, new IntegerProperty(1, 0, 127));
		register(SCAFFOLDING, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(SOUL_SAND, DELAY, new IntegerProperty(20, 0, 127));
		register(SOUL_SAND, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(STICKY_PISTON, CONNECTS_TO_WIRE, new BooleanProperty(false));
		register(STICKY_PISTON, DO_BLOCK_DROPPING, new BooleanProperty(true));
		register(STICKY_PISTON, FALLING_DELAY, new IntegerProperty(0, 0, 127));
		register(STICKY_PISTON, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(STICKY_PISTON, FAST_BLOCK_DROPPING, new BooleanProperty(true));
		register(STICKY_PISTON, SUPPORTS_BRITTLE_BLOCKS, new BooleanProperty(false));
		register(STICKY_PISTON, FORCE_UPDATE_WHEN_POWERED, new BooleanProperty(false));
		register(STICKY_PISTON, HEAD_UPDATES_ON_EXTENSION, new BooleanProperty(true));
		register(STICKY_PISTON, IGNORE_UPDATES_WHEN_EXTENDING, new BooleanProperty(false));
		register(STICKY_PISTON, FALLING_LAZY, new BooleanProperty(false));
		register(STICKY_PISTON, RISING_LAZY, new BooleanProperty(false));
		register(STICKY_PISTON, QC_DOWN, new BooleanProperty(false));
		register(STICKY_PISTON, QC_EAST, new BooleanProperty(false));
		register(STICKY_PISTON, QC_NORTH, new BooleanProperty(false));
		register(STICKY_PISTON, QC_SOUTH, new BooleanProperty(false));
		register(STICKY_PISTON, QC_UP, new BooleanProperty(true));
		register(STICKY_PISTON, QC_WEST, new BooleanProperty(false));
		register(STICKY_PISTON, RANDOMIZE_QC, new BooleanProperty(false));
		register(STICKY_PISTON, FALLING_SPEED, new IntegerProperty(2, 0, 127));
		register(STICKY_PISTON, RISING_SPEED, new IntegerProperty(2, 0, 127));
		register(STICKY_PISTON, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(STICKY_PISTON, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(STICKY_PISTON, PUSH_LIMIT, new IntegerProperty(12, 0, 127));
		
		register(STONE_BUTTON, FALLING_DELAY, new IntegerProperty(20, 1, 127));
		register(STONE_BUTTON, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(STONE_BUTTON, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(STONE_BUTTON, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(STONE_BUTTON, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(STONE_BUTTON, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(STONE_PRESSURE_PLATE, FALLING_DELAY, new IntegerProperty(20, 1, 127));
		register(STONE_PRESSURE_PLATE, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(STONE_PRESSURE_PLATE, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(STONE_PRESSURE_PLATE, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(STONE_PRESSURE_PLATE, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(STONE_PRESSURE_PLATE, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(TARGET_BLOCK, DELAY, new IntegerProperty(8, 1, 127));
		register(TARGET_BLOCK, PERSISTENT_PROJECTILE_DELAY, new IntegerProperty(20, 1, 127));
		register(TARGET_BLOCK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(TARGET_BLOCK, PERSISTENT_PROJECTILE_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(TNT, DELAY, new IntegerProperty(0, 1, 127));
		register(TNT, FUSE_TIME, new IntegerProperty(80, 1, 1023));
		register(TNT, LAZY, new BooleanProperty(true));
		register(TNT, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(TRIPWIRE_HOOK, DELAY, new IntegerProperty(10, 1, 127));
		register(TRIPWIRE_HOOK, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(TRIPWIRE_HOOK, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(TRIPWIRE_HOOK, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(TRIPWIRE, DELAY, new IntegerProperty(10, 1, 127));
		register(TRIPWIRE, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(WATER, DELAY, new IntegerProperty(5, 0, 127));
		register(WATER, TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(WOODEN_BUTTON, FALLING_DELAY, new IntegerProperty(30, 1, 127));
		register(WOODEN_BUTTON, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(WOODEN_BUTTON, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(WOODEN_BUTTON, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(WOODEN_BUTTON, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(WOODEN_BUTTON, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(WOODEN_PRESSURE_PLATE, FALLING_DELAY, new IntegerProperty(20, 1, 127));
		register(WOODEN_PRESSURE_PLATE, RISING_DELAY, new IntegerProperty(0, 0, 127));
		register(WOODEN_PRESSURE_PLATE, STRONG_POWER, new IntegerProperty(15, 0, 15));
		register(WOODEN_PRESSURE_PLATE, WEAK_POWER, new IntegerProperty(15, 0, 15));
		register(WOODEN_PRESSURE_PLATE, FALLING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		register(WOODEN_PRESSURE_PLATE, RISING_TICK_PRIORITY, new TickPriorityProperty(TickPriority.NORMAL));
		
		register(BUG_FIXES, MC54711, new BooleanProperty(false));
		register(BUG_FIXES, MC120986, new BooleanProperty(false));
		register(BUG_FIXES, MC136566, new BooleanProperty(false));
		register(BUG_FIXES, MC137127, new BooleanProperty(false));
		register(BUG_FIXES, MC172213, new BooleanProperty(false));
		
		register(GLOBAL, DELAY_MULTIPLIER, new IntegerProperty(1, 1, 127));
		register(GLOBAL, DO_BLOCK_UPDATES, new BooleanProperty(true));
		register(GLOBAL, DO_STATE_UPDATES, new BooleanProperty(true));
		register(GLOBAL, DO_COMPARATOR_UPDATES, new BooleanProperty(true));
		register(GLOBAL, DOUBLE_RETRACTION, new BooleanProperty(false));
		register(GLOBAL, RANDOMIZE_BLOCK_EVENTS, new BooleanProperty(false));
		register(GLOBAL, RANDOMIZE_SCHEDULED_TICK_DELAYS, new BooleanProperty(false));
		register(GLOBAL, RANDOMIZE_TICK_PRIORITIES, new BooleanProperty(false));
		register(GLOBAL, SHOW_NEIGHBOR_UPDATES, new BooleanProperty(false));
		register(GLOBAL, SHOW_PROCESSING_ORDER, new IntegerProperty(0, 0, 127));
		
		register(GLOBAL, EXPERIMENTAL_SET_BLOCK_STATE, new BooleanProperty(false));
	}
}