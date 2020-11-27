package redstonetweaks.setting;

import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;

public class Tweaks {
	
	private static final String CATEGORY_NAME = "Tweaks";
	public static final SettingsCategory TWEAKS = new SettingsCategory(CATEGORY_NAME);
	
	public static class Global {
		
		private static final String PACK_NAME = "Global";
		private static final SettingsPack GLOBAL = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which the world emits block updates to the neighbors of a block.");
		public static final UpdateOrderSetting COMPARATOR_UPDATE_ORDER = new UpdateOrderSetting("comparatorUpdateOrder", "The order in which the world emits comparator updates to the neighbors of a block.");
		public static final UpdateOrderSetting SHAPE_UPDATE_ORDER = new UpdateOrderSetting("shapeUpdateOrder", "The order in which the world emits shape updates to the neighbors of a block.");
		public static final BooleanSetting CHAINSTONE = new BooleanSetting("chainstone", "Inspired by the carpet mod rule of the same name, this setting makes connected chain blocks stick to each other and any blocks they anchored to. But be careful! A chain will only move as a whole if it is anchored at both ends.");
		public static final IntegerSetting DELAY_MULTIPLIER = new IntegerSetting("delayMultiplier", "The delay of all scheduled ticks will be multiplied by this value. When set to 0 all scheduled ticks will be executed instantaneously.", 0, 127);
		public static final BooleanSetting DO_BLOCK_UPDATES = new BooleanSetting("doBlockUpdates", "Allow worlds to dispatch block updates.");
		public static final BooleanSetting DO_SHAPE_UPDATES = new BooleanSetting("doShapeUpdates", "Allow worlds to dispatch shape updates.");
		public static final BooleanSetting DO_COMPARATOR_UPDATES = new BooleanSetting("doComparatorUpdates", "Allow worlds to dispatch comparator updates.");
		public static final BooleanSetting DOUBLE_RETRACTION = new BooleanSetting("doubleRetraction", "A re-implementation of behavior that was present in 1.3-1.8, known as \"Jeb retraction\" or \"instant double retraction\". It creates a very narrow window where unpowered pistons can be moved.");
		public static final BooleanSetting INSTANT_BLOCK_EVENTS = new BooleanSetting("instantBlockEvents", "Execute block events at the moment they are scheduled.");
		public static final BooleanSetting MERGE_SLABS = new BooleanSetting("mergeSlabs", "Allow half slabs of the same type to be pushed together and merge into a double slab block. Additionally, sticky surfaces can only move half slabs when making physical contact with the slab. This allows half slabs to be split when one half is pulled but not the other.");
		public static final BooleanSetting MOVABLE_BLOCK_ENTITIES = new BooleanSetting("movableBlockEntities", "Allow blocks with block entities to be moved by pistons.");
		public static final BooleanSetting MOVABLE_MOVING_BLOCKS = new BooleanSetting("movableMovingBlocks", "Allow moving blocks to be moved by pistons.");
		public static final IntegerSetting POWER_MAX = new IntegerSetting("maxPower", "The maximum power output of analogue components like redstone wire, comparators, weighted pressure plates, etc.", 0, Settings.Common.MAX_POWER);
		public static final BooleanSetting RANDOMIZE_BLOCK_EVENTS = new BooleanSetting("randomizeBlockEvents", "Randomize the order in which block events are processed.");
		public static final BooleanSetting RANDOMIZE_DELAYS = new BooleanSetting("randomizeDelays", "Randomize the delays of all block and fluid ticks that are scheduled.");
		public static final BooleanSetting RANDOMIZE_TICK_PRIORITIES = new BooleanSetting("randomizeTickPriorities", "Randomize the tick priorities of all block and fluid ticks that are scheduled.");
		public static final BooleanSetting SHOW_NEIGHBOR_UPDATES = new BooleanSetting("showNeighborUpdates", "When used in combination with showProcessingOrder, neighbor updates become scheduled events. The world tick will be paused until all neighbor updates have been executed. Colored boxes are drawn at the location of each neighbor update. The white box is the notifier position, a yellow box is a block update, a blue box a shape update and a red box a comparator update.");
		public static final IntegerSetting SHOW_PROCESSING_ORDER = new IntegerSetting("showProcessingOrder", "When enabled, the world tick will be broken down and each of its phases being executed at the given interval of server ticks. Some phases, like those of scheduled ticks, block events and block entities, will break down even further and execute one their actions per interval. Information about the current tick, current world and current phase will be displayed in the top left of the screen.", 0, 1023);
	}
	
	public static class BugFixes {
		
		private static final String PACK_NAME = "Bug Fixes";
		private static final SettingsPack BUG_FIXES = new SettingsPack(PACK_NAME);
		
		public static final BugFixSetting MC54711 = new BugFixSetting("MC-54711", "A hacky fix for the chain bug. This patch only changes behavior of quick off-pulses in repeaters and comparators.");
		public static final BugFixSetting MC120986 = new BugFixSetting("MC-120986", "While not nearly a complete fix for the bug described in the report, this patch does fix comparators not being updated when pistons move blocks with a comparator output.");
		public static final BugFixSetting MC136566 = new BugFixSetting("MC-136566", "Fixes blocks not being updated when a moved active observer materializes.");
		public static final BugFixSetting MC137127 = new BugFixSetting("MC-137127", "Fixes observers not being updated when a moved active observer materializes.");
		public static final BugFixSetting MC172213 = new BugFixSetting("MC-172213", "A fix for the so-called \"input bug\", which causes redstone components to lose 1 tick of delay if activated by a player input. To fix this world time is incremented after all dimensions have been ticked.");
	}
	
	public static class ActivatorRail {
		
		private static final String PACK_NAME = "Activator Rail";
		private static final SettingsPack ACTIVATOR_RAIL = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting("powerLimit", "The maximum distance power can flow through rails.", 1, 1023);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Bamboo {
		
		private static final String PACK_NAME = "Bamboo";
		private static final SettingsPack BAMBOO = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Barrier {
		
		private static final String PACK_NAME = "Barrier";
		private static final SettingsPack BARRIER = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting IS_MOVABLE = new BooleanSetting("isMovable", "When enabled, barriers can be moved by pistons.");
	}
	
	public static class BubbleColumn {
		
		private static final String PACK_NAME = "Bubble Column";
		private static final SettingsPack BUBBLE_COLUMN = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay before a bubble column is created.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Cactus {
		
		private static final String PACK_NAME = "Cactus";
		private static final SettingsPack CACTUS = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class ChorusPlant {
		
		private static final String PACK_NAME = "Chorus Plant";
		private static final SettingsPack CHORUS_PLANT = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class CommandBlock {
		
		private static final String PACK_NAME = "Command Block";
		private static final SettingsPack COMMAND_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 1, Settings.Common.MAX_DELAY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Comparator {
		
		private static final String PACK_NAME = "Comparator";
		private static final SettingsPack COMPARATOR = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting ADDITION_MODE = new BooleanSetting("additionMode", "When enabled, the comparator's subtract mode turns into \"addition mode\". The output will be the sum of the back input and the highest side input.");
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which the comparator updates its neighbors when its power output changes.");
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before changing power output.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting REDSTONE_BLOCKS_VALID_SIDE_INPUT = new BooleanSetting("redstoneBlocksValidSideInput", "Count redstone blocks as valid side inputs.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting("tickPriorityFacingDiode", Settings.Common.DESC_TICK_PRIORITY_FACING_DIODE);
	}
	
	public static class Composter {
		
		private static final String PACK_NAME = "Composter";
		private static final SettingsPack COMPOSTER = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "The delay in ticks before transitioning from level 7 to level 8.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Coral {
		
		private static final String PACK_NAME = "Coral";
		private static final SettingsPack CORAL = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class CoralBlock {
		
		private static final String PACK_NAME = "Coral Block";
		private static final SettingsPack CORAL_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class DetectorRail {
		
		private static final String PACK_NAME = "Detector Rail";
		private static final SettingsPack DETECTOR_RAIL = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before attempting to depower.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Dispenser {
		
		private static final String PACK_NAME = "Dispenser";
		private static final SettingsPack DISPENSER = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class DragonEgg {
		
		private static final String PACK_NAME = "Dragon Egg";
		private static final SettingsPack DRAGON_EGG = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before attempting to fall.", 0, Settings.Common.MAX_DELAY);
	}
	
	public static class Dropper {
		
		private static final String PACK_NAME = "Dropper";
		private static final SettingsPack DROPPER = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Farmland {
		
		private static final String PACK_NAME = "Farmland";
		private static final SettingsPack FARMLAND = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before turning into dirt.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Fire {
		
		private static final String PACK_NAME = "Fire";
		private static final SettingsPack FIRE = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay in ticks.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay in ticks.", 1, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class FrostedIce {
		
		private static final String PACK_NAME = "Frosted Ice";
		private static final SettingsPack FROSTED_ICE = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class GrassPath {
		
		private static final String PACK_NAME = "Grass Path";
		private static final SettingsPack GRASS_PATH = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before turning into dirt.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class GravityBlock {
		
		private static final String PACK_NAME = "Gravity Block";
		private static final SettingsPack GRAVITY_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay before attempting to fall.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class HeavyWeightedPressurePlate {
		
		private static final String PACK_NAME = "Heavy Weighted Pressure Plate";
		private static final SettingsPack HEAVY_WEIGHTED_PRESSURE_PLATE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are update when the pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final IntegerSetting WEIGHT = new IntegerSetting("weight", "The number of entities needed for the pressure plate to emit maximum power.", 1, 1023);
	}
	
	public static class Hopper {
		
		private static final String PACK_NAME = "Hopper";
		private static final SettingsPack HOPPER = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting COOLDOWN_DEFAULT = new IntegerSetting("cooldownDefault", "The default cooldown after transfering or receiving an item.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting COOLDOWN_PRIORITY = new IntegerSetting("cooldownPriority", "The cooldown if an item is received from a hopper that ticked at the same time or earlier.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Delay in ticks before being locked.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Delay in ticks before being unlocked", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Lava {
		
		private static final String PACK_NAME = "Lava";
		private static final SettingsPack LAVA = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting("delayDefault", "Delay in ticks in non-nether dimensions.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_NETHER = new IntegerSetting("delayNether", "Delay in ticks in the nether dimension.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Leaves {
		
		private static final String PACK_NAME = "Leaves";
		private static final SettingsPack LEAVES = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before a leaf block updates its distance to the nearest log.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Lectern {
		
		private static final String PACK_NAME = "Lectern";
		private static final SettingsPack LECTERN = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Lever {
		
		private static final String PACK_NAME = "Lever";
		private static final SettingsPack LEVER = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are updated when the lever is toggled.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class LightWeightedPressurePlate {
		
		private static final String PACK_NAME = "Light Weighted Pressure Plate";
		private static final SettingsPack LIGHT_WEIGHTED_PRESSURE_PLATE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are update when the pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final IntegerSetting WEIGHT = new IntegerSetting("weight", "The number of entities needed for the pressure plate to emit maximum power.", 1, 1023);
	}
	
	public static class MagentaGlazedTerracotta {
		
		private static final String PACK_NAME = "Magenta Glazed Terracotta";
		private static final SettingsPack MAGENTA_GLAZED_TERRACOTTA = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting IS_POWER_DIODE = new BooleanSetting("isPowerDiode", "When enabled, power can only flow in the direction of the arrow on the top side of the block. Additionally, a redstone wire block on top of the block can only flow in the direction of the arrow on the top side of the block.");
	}
	
	public static class MagmaBlock {
		
		private static final String PACK_NAME = "Magma Block";
		private static final SettingsPack MAGMA_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before a magma block updates water above it to create a bubble column.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class NormalPiston {
		
		private static final String PACK_NAME = "Normal Piston";
		private static final SettingsPack NORMAL_PISTON = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "When enabled, normal pistons connect to redstone wire.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Delay in ticks before extending.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Delay in ticks before retracting", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting("ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting("ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.");
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting MOVABLE_WHEN_EXTENDED = new BooleanSetting("movableWhenExtended", "Allow extended pistons to be moved.");
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting("pushLimit", "The maximum number of blocks a piston can move.", 0, 2048);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting("speedRisingEdge", "The duration of the extension in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting("speedFallingEdge", "The duration of the retraction in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting("supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face, without breaking when the piston extends or retracts.");
		public static final BooleanSetting HEAD_UPDATES_ON_EXTENSION = new BooleanSetting("headUpdatesOnExtension", "Emit block updates around the piston head when it starts extending.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final BooleanSetting UPDATE_SELF_WHILE_POWERED = new BooleanSetting("updateSelfWhilePowered", "If the piston is powered but cannot extend, it will update itself each tick until it can extend. This is achieved using scheduled ticks.");
	}
	
	public static class NoteBlock {
		
		private static final String PACK_NAME = "Note Block";
		private static final SettingsPack NOTE_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before playing a note.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Observer {
		
		private static final String PACK_NAME = "Observer";
		private static final SettingsPack OBSERVER = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are updated when an observer powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting DISABLE = new BooleanSetting("disable", "Disable observers.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class PoweredRail {
		
		private static final String PACK_NAME = "Powered Rail";
		private static final SettingsPack POWERED_RAIL = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting("powerLimit", "The maximum distance power can flow through rails.", 1, 1023);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class RedSand {
		
		private static final String PACK_NAME = "Red Sand";
		private static final SettingsPack RED_SAND = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which indirect neighbors of the block are updated if red sand emits a strong power greater than 0.");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "When enabled, red sand connects to redstone wire.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
	}
	
	public static class RedstoneBlock {
		
		private static final String PACK_NAME = "Redstone Block";
		private static final SettingsPack REDSTONE_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone blocks emit a strong power greater than 0.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
	}
	
	public static class RedstoneLamp {
		
		private static final String PACK_NAME = "Redstone Lamp";
		private static final SettingsPack REDSTONE_LAMP = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class RedstoneOre {
		
		private static final String PACK_NAME = "Redstone Ore";
		private static final SettingsPack REDSTONE_ORE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone ore emits a strong power greater than 0.");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "When enabled, redstone ore connects to redstone wire.");
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class RedstoneTorch {
		
		private static final String PACK_NAME = "Redstone Torch";
		private static final SettingsPack REDSTONE_TORCH = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are updated when a redstone torch powers on or off.");
		public static final IntegerSetting BURNOUT_COUNT = new IntegerSetting("burnoutCount", "The number of times a redstone torch must depower to burn out.", 0, 2048);
		public static final IntegerSetting BURNOUT_TIMER = new IntegerSetting("burnoutTimer", "The time in ticks during which a redstone torch must depower a set number of times to burn out.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_BURNOUT = new IntegerSetting("delayBurnout", "The amount of time for which a redstone torch will be burned out.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final BooleanSetting SOFT_INVERSION = new BooleanSetting("softInversion", "An implementation of behavior present in the Bedrock Edition known as \"soft inversion\". It causes a redstone torche attached to a piston to depower when that piston is powered.");
		public static final TickPrioritySetting TICK_PRIORITY_BURNOUT = new TickPrioritySetting("tickPriorityBurnout", "The tick priority of the tick scheduled when a redstone torch burns out.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class RedstoneWire {
		
		private static final String PACK_NAME = "Redstone Wire";
		private static final SettingsPack REDSTONE_WIRE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which redstone wire updates its neighbors after a state change.");
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting INVERT_FLOW_ON_GLASS = new BooleanSetting("invertFlowOnGlass", "When enabled, redstone wire power can flow down glass, but not up it.");
		public static final BooleanSetting SLABS_ALLOW_UP_CONNECTION = new BooleanSetting("slabsAllowUpConnection", "When enabled, redstone wire can visually and logically connect to other redstone wire on top of a neighboring slab block.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Repeater {
		
		private static final String PACK_NAME = "Repeater";
		private static final SettingsPack REPEATER = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighbors are updated when a repeater powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting("tickPriorityFacingDiode", Settings.Common.DESC_TICK_PRIORITY_FACING_DIODE);
	}
	
	public static class Scaffolding {
		
		private static final String PACK_NAME = "Scaffolding";
		private static final SettingsPack SCAFFOLDING = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before a scaffolding block updates its distance to the nearest supported scaffolding block.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class SoulSand {
		
		private static final String PACK_NAME = "Soul Sand";
		private static final SettingsPack SOUL_SAND = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before a soulsand block updates water above it to create a bubble column.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Stairs {
		
		private static final String PACK_NAME = "Stairs";
		private static final SettingsPack STAIRS = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting FULL_FACES_ARE_SOLID = new BooleanSetting("fullFacesAreSolid", "When enabled, all full faces of stair blocks act like full solid blocks, meaning they conduct power and cut off redstone wire.");
	}
	
	public static class StickyPiston {
		
		private static final String PACK_NAME = "Sticky Piston";
		private static final SettingsPack STICKY_PISTON = new SettingsPack(PACK_NAME);
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "When enabled, sticky pistons connect to redstone wire.");
		public static final BooleanSetting DO_BLOCK_DROPPING = new BooleanSetting("doBlockDropping", "When enabled, sticky pistons drop their block when given a short pulse (less than or equal to their speed).");
		public static final BooleanSetting FAST_BLOCK_DROPPING = new BooleanSetting("fastBlockDropping", "When enabled and doBlockDropping is also enabled, a sticky piston will instantly place the block it is dropping.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting("ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting("ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.");
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting MOVABLE_WHEN_EXTENDED = new BooleanSetting("movableWhenExtended", "Allow extended sticky pistons to be moved.");
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting("pushLimit", "The maximum number of blocks a piston can move.", 0, 2048);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting("speedRisingEdge", "The duration of the extension in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting("speedFallingEdge", "The duration of the retraction in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting SUPER_STICKY = new BooleanSetting("superSticky", "Make the face of sticky pistons stick to blocks when moved.");
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting("supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face without breaking when the piston extends or retracts.");
		public static final BooleanSetting HEAD_UPDATES_ON_EXTENSION = new BooleanSetting("headUpdatesOnExtension", "Emit block updates around the piston head when it starts extending.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final BooleanSetting UPDATE_SELF_WHILE_POWERED = new BooleanSetting("updateSelfWhilePowered", "If the piston is powered but cannot extend, it will update itself each tick until it can extend. This is achieved using scheduled ticks.");
	}
	
	public static class StoneButton {
		
		private static final String PACK_NAME = "Stone Button";
		private static final SettingsPack STONE_BUTTON = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class StonePressurePlate {
		
		private static final String PACK_NAME = "Stone Pressure Plate";
		private static final SettingsPack STONE_PRESSURE_PLATE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class SugarCane {
		
		private static final String PACK_NAME = "Sugar Cane";
		private static final SettingsPack SUGAR_CANE = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TargetBlock {
		
		private static final String PACK_NAME = "Target Block";
		private static final SettingsPack TARGET_BLOCK = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting("delayDefault", "The default delay in ticks before powering off.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_PERSISTENT_PROJECTILE = new IntegerSetting("delayPersistentProjectile", "The delay in ticks before powering off when hit by a persistent projectile, like an arrow or a trident.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TNT {
		
		private static final String PACK_NAME = "TNT";
		private static final SettingsPack TNT = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting FUSE_TIME = new IntegerSetting("fuseTime", "The delay in ticks before a TNT entity explodes.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting("quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Tripwire {
		
		private static final String PACK_NAME = "Tripwire";
		private static final SettingsPack TRIPWIRE = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before attempting to power off.", 1, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TripwireHook {
		
		private static final String PACK_NAME = "Tripwire Hook";
		private static final SettingsPack TRIPWIRE_HOOK = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighboring blocks are updated when a tripwire hook powers on or off.");
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before attempting to power off.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Vines {
		
		private static final String PACK_NAME = "Vines";
		private static final SettingsPack VINES = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Water {
		
		private static final String PACK_NAME = "Water";
		private static final SettingsPack WATER = new SettingsPack(PACK_NAME);
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay in ticks before attempting to flow.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class WoodenButton {
		
		private static final String PACK_NAME = "Wooden Button";
		private static final SettingsPack WOODEN_BUTTON = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles..");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class WoodenPressurePlate {
		
		private static final String PACK_NAME = "Wooden Pressure Plate";
		private static final SettingsPack WOODEN_PRESSURE_PLATE = new SettingsPack(PACK_NAME);
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting("blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers on or off.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static void init() {
		Settings.registerCategory(TWEAKS);
		
		Settings.registerPack(TWEAKS, Global.GLOBAL);
		Settings.register(TWEAKS, Global.GLOBAL, Global.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, Global.GLOBAL, Global.COMPARATOR_UPDATE_ORDER);
		Settings.register(TWEAKS, Global.GLOBAL, Global.SHAPE_UPDATE_ORDER);
		Settings.register(TWEAKS, Global.GLOBAL, Global.CHAINSTONE);
		Settings.register(TWEAKS, Global.GLOBAL, Global.DELAY_MULTIPLIER);
		Settings.register(TWEAKS, Global.GLOBAL, Global.DO_BLOCK_UPDATES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.DO_SHAPE_UPDATES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.DO_COMPARATOR_UPDATES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.DOUBLE_RETRACTION);
		Settings.register(TWEAKS, Global.GLOBAL, Global.INSTANT_BLOCK_EVENTS);
		Settings.register(TWEAKS, Global.GLOBAL, Global.MERGE_SLABS);
		Settings.register(TWEAKS, Global.GLOBAL, Global.MOVABLE_BLOCK_ENTITIES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.MOVABLE_MOVING_BLOCKS);
		Settings.register(TWEAKS, Global.GLOBAL, Global.POWER_MAX);
		Settings.register(TWEAKS, Global.GLOBAL, Global.RANDOMIZE_BLOCK_EVENTS);
		Settings.register(TWEAKS, Global.GLOBAL, Global.RANDOMIZE_DELAYS);
		Settings.register(TWEAKS, Global.GLOBAL, Global.RANDOMIZE_TICK_PRIORITIES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.SHOW_NEIGHBOR_UPDATES);
		Settings.register(TWEAKS, Global.GLOBAL, Global.SHOW_PROCESSING_ORDER);
		
		Settings.registerPack(TWEAKS, BugFixes.BUG_FIXES);
		Settings.register(TWEAKS, BugFixes.BUG_FIXES, BugFixes.MC54711);
		Settings.register(TWEAKS, BugFixes.BUG_FIXES, BugFixes.MC120986);
		Settings.register(TWEAKS, BugFixes.BUG_FIXES, BugFixes.MC136566);
		Settings.register(TWEAKS, BugFixes.BUG_FIXES, BugFixes.MC137127);
		Settings.register(TWEAKS, BugFixes.BUG_FIXES, BugFixes.MC172213);
		
		Settings.registerPack(TWEAKS, ActivatorRail.ACTIVATOR_RAIL);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.POWER_LIMIT);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.QC);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.RANDOMIZE_QC);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, ActivatorRail.ACTIVATOR_RAIL, ActivatorRail.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, Bamboo.BAMBOO);
		Settings.register(TWEAKS, Bamboo.BAMBOO, Bamboo.DELAY);
		Settings.register(TWEAKS, Bamboo.BAMBOO, Bamboo.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Barrier.BARRIER);
		Settings.register(TWEAKS, Barrier.BARRIER, Barrier.IS_MOVABLE);
		
		Settings.registerPack(TWEAKS, BubbleColumn.BUBBLE_COLUMN);
		Settings.register(TWEAKS, BubbleColumn.BUBBLE_COLUMN, BubbleColumn.DELAY);
		Settings.register(TWEAKS, BubbleColumn.BUBBLE_COLUMN, BubbleColumn.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Cactus.CACTUS);
		Settings.register(TWEAKS, Cactus.CACTUS, Cactus.DELAY);
		Settings.register(TWEAKS, Cactus.CACTUS, Cactus.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, ChorusPlant.CHORUS_PLANT);
		Settings.register(TWEAKS, ChorusPlant.CHORUS_PLANT, ChorusPlant.DELAY);
		Settings.register(TWEAKS, ChorusPlant.CHORUS_PLANT, ChorusPlant.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, CommandBlock.COMMAND_BLOCK);
		Settings.register(TWEAKS, CommandBlock.COMMAND_BLOCK, CommandBlock.DELAY);
		Settings.register(TWEAKS, CommandBlock.COMMAND_BLOCK, CommandBlock.QC);
		Settings.register(TWEAKS, CommandBlock.COMMAND_BLOCK, CommandBlock.RANDOMIZE_QC);
		Settings.register(TWEAKS, CommandBlock.COMMAND_BLOCK, CommandBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Comparator.COMPARATOR);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.ADDITION_MODE);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.DELAY);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.TICK_PRIORITY);
		Settings.register(TWEAKS, Comparator.COMPARATOR, Comparator.TICK_PRIORITY_FACING_DIODE);
		
		Settings.registerPack(TWEAKS, Composter.COMPOSTER);
		Settings.register(TWEAKS, Composter.COMPOSTER, Composter.DELAY);
		Settings.register(TWEAKS, Composter.COMPOSTER, Composter.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Coral.CORAL);
		Settings.register(TWEAKS, Coral.CORAL, Coral.DELAY_MIN);
		Settings.register(TWEAKS, Coral.CORAL, Coral.DELAY_MAX);
		Settings.register(TWEAKS, Coral.CORAL, Coral.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, CoralBlock.CORAL_BLOCK);
		Settings.register(TWEAKS, CoralBlock.CORAL_BLOCK, CoralBlock.DELAY_MIN);
		Settings.register(TWEAKS, CoralBlock.CORAL_BLOCK, CoralBlock.DELAY_MAX);
		Settings.register(TWEAKS, CoralBlock.CORAL_BLOCK, CoralBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, DetectorRail.DETECTOR_RAIL);
		Settings.register(TWEAKS, DetectorRail.DETECTOR_RAIL, DetectorRail.DELAY);
		Settings.register(TWEAKS, DetectorRail.DETECTOR_RAIL, DetectorRail.POWER_WEAK);
		Settings.register(TWEAKS, DetectorRail.DETECTOR_RAIL, DetectorRail.POWER_STRONG);
		Settings.register(TWEAKS, DetectorRail.DETECTOR_RAIL, DetectorRail.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Dispenser.DISPENSER);
		Settings.register(TWEAKS, Dispenser.DISPENSER, Dispenser.DELAY);
		Settings.register(TWEAKS, Dispenser.DISPENSER, Dispenser.LAZY);
		Settings.register(TWEAKS, Dispenser.DISPENSER, Dispenser.QC);
		Settings.register(TWEAKS, Dispenser.DISPENSER, Dispenser.RANDOMIZE_QC);
		Settings.register(TWEAKS, Dispenser.DISPENSER, Dispenser.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, DragonEgg.DRAGON_EGG);
		Settings.register(TWEAKS, DragonEgg.DRAGON_EGG, DragonEgg.DELAY);
		
		Settings.registerPack(TWEAKS, Dropper.DROPPER);
		Settings.register(TWEAKS, Dropper.DROPPER, Dropper.DELAY);
		Settings.register(TWEAKS, Dropper.DROPPER, Dropper.LAZY);
		Settings.register(TWEAKS, Dropper.DROPPER, Dropper.QC);
		Settings.register(TWEAKS, Dropper.DROPPER, Dropper.RANDOMIZE_QC);
		Settings.register(TWEAKS, Dropper.DROPPER, Dropper.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Farmland.FARMLAND);
		Settings.register(TWEAKS, Farmland.FARMLAND, Farmland.DELAY);
		Settings.register(TWEAKS, Farmland.FARMLAND, Farmland.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Fire.FIRE);
		Settings.register(TWEAKS, Fire.FIRE, Fire.DELAY_MIN);
		Settings.register(TWEAKS, Fire.FIRE, Fire.DELAY_MAX);
		Settings.register(TWEAKS, Fire.FIRE, Fire.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, FrostedIce.FROSTED_ICE);
		Settings.register(TWEAKS, FrostedIce.FROSTED_ICE, FrostedIce.DELAY_MIN);
		Settings.register(TWEAKS, FrostedIce.FROSTED_ICE, FrostedIce.DELAY_MAX);
		Settings.register(TWEAKS, FrostedIce.FROSTED_ICE, FrostedIce.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, GrassPath.GRASS_PATH);
		Settings.register(TWEAKS, GrassPath.GRASS_PATH, GrassPath.DELAY);
		Settings.register(TWEAKS, GrassPath.GRASS_PATH, GrassPath.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, GravityBlock.GRAVITY_BLOCK);
		Settings.register(TWEAKS, GravityBlock.GRAVITY_BLOCK, GravityBlock.DELAY);
		Settings.register(TWEAKS, GravityBlock.GRAVITY_BLOCK, GravityBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(TWEAKS, HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE, HeavyWeightedPressurePlate.WEIGHT);
		
		Settings.registerPack(TWEAKS, Hopper.HOPPER);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.COOLDOWN_DEFAULT);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.COOLDOWN_PRIORITY);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.QC);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.RANDOMIZE_QC);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, Hopper.HOPPER, Hopper.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, Lava.LAVA);
		Settings.register(TWEAKS, Lava.LAVA, Lava.DELAY_DEFAULT);
		Settings.register(TWEAKS, Lava.LAVA, Lava.DELAY_NETHER);
		Settings.register(TWEAKS, Lava.LAVA, Lava.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Leaves.LEAVES);
		Settings.register(TWEAKS, Leaves.LEAVES, Leaves.DELAY);
		Settings.register(TWEAKS, Leaves.LEAVES, Leaves.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Lectern.LECTERN);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.POWER_WEAK);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.POWER_STRONG);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, Lectern.LECTERN, Lectern.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, Lever.LEVER);
		Settings.register(TWEAKS, Lever.LEVER, Lever.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, Lever.LEVER, Lever.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, Lever.LEVER, Lever.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, Lever.LEVER, Lever.POWER_WEAK);
		Settings.register(TWEAKS, Lever.LEVER, Lever.POWER_STRONG);
		Settings.register(TWEAKS, Lever.LEVER, Lever.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, Lever.LEVER, Lever.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(TWEAKS, LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE, LightWeightedPressurePlate.WEIGHT);
		
		Settings.registerPack(TWEAKS, MagentaGlazedTerracotta.MAGENTA_GLAZED_TERRACOTTA);
		Settings.register(TWEAKS, MagentaGlazedTerracotta.MAGENTA_GLAZED_TERRACOTTA, MagentaGlazedTerracotta.IS_POWER_DIODE);
		
		Settings.registerPack(TWEAKS, MagmaBlock.MAGMA_BLOCK);
		Settings.register(TWEAKS, MagmaBlock.MAGMA_BLOCK, MagmaBlock.DELAY);
		Settings.register(TWEAKS, MagmaBlock.MAGMA_BLOCK, MagmaBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, NormalPiston.NORMAL_PISTON);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.CONNECTS_TO_WIRE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.MOVABLE_WHEN_EXTENDED);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.PUSH_LIMIT);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.QC);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.RANDOMIZE_QC);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.SPEED_RISING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.SPEED_FALLING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.SUPPORTS_BRITTLE_BLOCKS);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.HEAD_UPDATES_ON_EXTENSION);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(TWEAKS, NormalPiston.NORMAL_PISTON, NormalPiston.UPDATE_SELF_WHILE_POWERED);
		
		Settings.registerPack(TWEAKS, NoteBlock.NOTE_BLOCK);
		Settings.register(TWEAKS, NoteBlock.NOTE_BLOCK, NoteBlock.DELAY);
		Settings.register(TWEAKS, NoteBlock.NOTE_BLOCK, NoteBlock.LAZY);
		Settings.register(TWEAKS, NoteBlock.NOTE_BLOCK, NoteBlock.QC);
		Settings.register(TWEAKS, NoteBlock.NOTE_BLOCK, NoteBlock.RANDOMIZE_QC);
		Settings.register(TWEAKS, NoteBlock.NOTE_BLOCK, NoteBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Observer.OBSERVER);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.DISABLE);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.POWER_WEAK);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.POWER_STRONG);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, Observer.OBSERVER, Observer.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, PoweredRail.POWERED_RAIL);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.POWER_LIMIT);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.QC);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.RANDOMIZE_QC);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, PoweredRail.POWERED_RAIL, PoweredRail.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, RedSand.RED_SAND);
		Settings.register(TWEAKS, RedSand.RED_SAND, RedSand.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, RedSand.RED_SAND, RedSand.CONNECTS_TO_WIRE);
		Settings.register(TWEAKS, RedSand.RED_SAND, RedSand.POWER_WEAK);
		Settings.register(TWEAKS, RedSand.RED_SAND, RedSand.POWER_STRONG);
		
		Settings.registerPack(TWEAKS, RedstoneBlock.REDSTONE_BLOCK);
		Settings.register(TWEAKS, RedstoneBlock.REDSTONE_BLOCK, RedstoneBlock.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, RedstoneBlock.REDSTONE_BLOCK, RedstoneBlock.POWER_WEAK);
		Settings.register(TWEAKS, RedstoneBlock.REDSTONE_BLOCK, RedstoneBlock.POWER_STRONG);
		
		Settings.registerPack(TWEAKS, RedstoneLamp.REDSTONE_LAMP);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.QC);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.RANDOMIZE_QC);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneLamp.REDSTONE_LAMP, RedstoneLamp.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, RedstoneOre.REDSTONE_ORE);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.CONNECTS_TO_WIRE);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.DELAY);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.POWER_WEAK);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.POWER_STRONG);
		Settings.register(TWEAKS, RedstoneOre.REDSTONE_ORE, RedstoneOre.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, RedstoneTorch.REDSTONE_TORCH);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.BURNOUT_COUNT);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.BURNOUT_TIMER);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.DELAY_BURNOUT);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.POWER_WEAK);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.POWER_STRONG);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.SOFT_INVERSION);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.TICK_PRIORITY_BURNOUT);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, RedstoneTorch.REDSTONE_TORCH, RedstoneTorch.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, RedstoneWire.REDSTONE_WIRE);
		Settings.register(TWEAKS, RedstoneWire.REDSTONE_WIRE, RedstoneWire.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, RedstoneWire.REDSTONE_WIRE, RedstoneWire.DELAY);
		Settings.register(TWEAKS, RedstoneWire.REDSTONE_WIRE, RedstoneWire.INVERT_FLOW_ON_GLASS);
		Settings.register(TWEAKS, RedstoneWire.REDSTONE_WIRE, RedstoneWire.SLABS_ALLOW_UP_CONNECTION);
		Settings.register(TWEAKS, RedstoneWire.REDSTONE_WIRE, RedstoneWire.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Repeater.REPEATER);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.POWER_WEAK);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.POWER_STRONG);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(TWEAKS, Repeater.REPEATER, Repeater.TICK_PRIORITY_FACING_DIODE);
		
		Settings.registerPack(TWEAKS, Scaffolding.SCAFFOLDING);
		Settings.register(TWEAKS, Scaffolding.SCAFFOLDING, Scaffolding.DELAY);
		Settings.register(TWEAKS, Scaffolding.SCAFFOLDING, Scaffolding.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, SoulSand.SOUL_SAND);
		Settings.register(TWEAKS, SoulSand.SOUL_SAND, SoulSand.DELAY);
		Settings.register(TWEAKS, SoulSand.SOUL_SAND, SoulSand.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Stairs.STAIRS);
		Settings.register(TWEAKS, Stairs.STAIRS, Stairs.FULL_FACES_ARE_SOLID);
		
		Settings.registerPack(TWEAKS, StickyPiston.STICKY_PISTON);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.CONNECTS_TO_WIRE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.DO_BLOCK_DROPPING);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.FAST_BLOCK_DROPPING);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.LAZY_RISING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.LAZY_FALLING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.MOVABLE_WHEN_EXTENDED);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.PUSH_LIMIT);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.QC);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.RANDOMIZE_QC);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.SPEED_RISING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.SPEED_FALLING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.SUPER_STICKY);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.SUPPORTS_BRITTLE_BLOCKS);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.HEAD_UPDATES_ON_EXTENSION);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(TWEAKS, StickyPiston.STICKY_PISTON, StickyPiston.UPDATE_SELF_WHILE_POWERED);
		
		Settings.registerPack(TWEAKS, StoneButton.STONE_BUTTON);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.POWER_WEAK);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.POWER_STRONG);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, StoneButton.STONE_BUTTON, StoneButton.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.POWER_WEAK);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.POWER_STRONG);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, StonePressurePlate.STONE_PRESSURE_PLATE, StonePressurePlate.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, SugarCane.SUGAR_CANE);
		Settings.register(TWEAKS, SugarCane.SUGAR_CANE, SugarCane.DELAY);
		Settings.register(TWEAKS, SugarCane.SUGAR_CANE, SugarCane.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, TargetBlock.TARGET_BLOCK);
		Settings.register(TWEAKS, TargetBlock.TARGET_BLOCK, TargetBlock.DELAY_DEFAULT);
		Settings.register(TWEAKS, TargetBlock.TARGET_BLOCK, TargetBlock.DELAY_PERSISTENT_PROJECTILE);
		Settings.register(TWEAKS, TargetBlock.TARGET_BLOCK, TargetBlock.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, TNT.TNT);
		Settings.register(TWEAKS, TNT.TNT, TNT.DELAY);
		Settings.register(TWEAKS, TNT.TNT, TNT.FUSE_TIME);
		Settings.register(TWEAKS, TNT.TNT, TNT.LAZY);
		Settings.register(TWEAKS, TNT.TNT, TNT.QC);
		Settings.register(TWEAKS, TNT.TNT, TNT.RANDOMIZE_QC);
		Settings.register(TWEAKS, TNT.TNT, TNT.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Tripwire.TRIPWIRE);
		Settings.register(TWEAKS, Tripwire.TRIPWIRE, Tripwire.DELAY);
		Settings.register(TWEAKS, Tripwire.TRIPWIRE, Tripwire.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, TripwireHook.TRIPWIRE_HOOK);
		Settings.register(TWEAKS, TripwireHook.TRIPWIRE_HOOK, TripwireHook.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, TripwireHook.TRIPWIRE_HOOK, TripwireHook.DELAY);
		Settings.register(TWEAKS, TripwireHook.TRIPWIRE_HOOK, TripwireHook.POWER_WEAK);
		Settings.register(TWEAKS, TripwireHook.TRIPWIRE_HOOK, TripwireHook.POWER_STRONG);
		Settings.register(TWEAKS, TripwireHook.TRIPWIRE_HOOK, TripwireHook.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Vines.VINES);
		Settings.register(TWEAKS, Vines.VINES, Vines.DELAY);
		Settings.register(TWEAKS, Vines.VINES, Vines.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, Water.WATER);
		Settings.register(TWEAKS, Water.WATER, Water.DELAY);
		Settings.register(TWEAKS, Water.WATER, Water.TICK_PRIORITY);
		
		Settings.registerPack(TWEAKS, WoodenButton.WOODEN_BUTTON);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.POWER_WEAK);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.POWER_STRONG);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, WoodenButton.WOODEN_BUTTON, WoodenButton.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.registerPack(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.DELAY_RISING_EDGE);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.POWER_WEAK);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.POWER_STRONG);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(TWEAKS, WoodenPressurePlate.WOODEN_PRESSURE_PLATE, WoodenPressurePlate.TICK_PRIORITY_FALLING_EDGE);
	}
}
