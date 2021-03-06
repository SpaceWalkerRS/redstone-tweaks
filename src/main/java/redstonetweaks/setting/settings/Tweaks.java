package redstonetweaks.setting.settings;

import redstonetweaks.setting.SettingsCategory;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.CapacitorBehaviorSetting;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.setting.types.WorldTickOptionsSetting;

public class Tweaks {
	
	public static final SettingsCategory TWEAKS = new SettingsCategory("Tweaks");
	
	public static class Global {
		
		private static final SettingsPack GLOBAL = new SettingsPack(TWEAKS, "Global");
		
		public static final IntegerSetting BLOCK_EVENT_LIMIT = new IntegerSetting(GLOBAL, "blockEventLimit", "The maximum number of block events that can be executed each tick.", 0, Integer.MAX_VALUE);
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(GLOBAL, "blockUpdateOrder", "The order in which the world dispatches block updates to the neighbors of a block.");
		public static final UpdateOrderSetting COMPARATOR_UPDATE_ORDER = new UpdateOrderSetting(GLOBAL, "comparatorUpdateOrder", "The order in which the world dispatches comparator updates to the neighbors of a block.");
		public static final UpdateOrderSetting SHAPE_UPDATE_ORDER = new UpdateOrderSetting(GLOBAL, "shapeUpdateOrder", "The order in which the world dispatches shape updates to the neighbors of a block.");
		public static final BooleanSetting CHAINSTONE = new BooleanSetting(GLOBAL, "chainstone", "Inspired by the carpet mod rule of the same name, this setting makes connected chain blocks stick to each other and any blocks they are anchored to. But be careful! A chain structure will only move as a whole if it is anchored at both ends.");
		public static final IntegerSetting DELAY_MULTIPLIER = new IntegerSetting(GLOBAL, "delayMultiplier", "The delay of all scheduled ticks will be multiplied by this value. When set to 0 all scheduled ticks will be executed instantaneously.", 0, 127);
		public static final BooleanSetting DO_BLOCK_UPDATES = new BooleanSetting(GLOBAL, "doBlockUpdates", "Allow worlds to dispatch block updates.");
		public static final BooleanSetting DO_COMPARATOR_UPDATES = new BooleanSetting(GLOBAL, "doComparatorUpdates", "Allow worlds to dispatch comparator updates.");
		public static final BooleanSetting DO_SHAPE_UPDATES = new BooleanSetting(GLOBAL, "doShapeUpdates", "Allow worlds to dispatch shape updates.");
		public static final BooleanSetting DOUBLE_RETRACTION = new BooleanSetting(GLOBAL, "doubleRetraction", "A re-implementation of behavior that was present in 1.3-1.8, known as \"Jeb retraction\" or \"instant double retraction\". It creates a very narrow window where unpowered pistons can be moved. NOTE: doubleRetraction will not work if the looseHead settings are enabled.");
		public static final BooleanSetting INSTANT_BLOCK_EVENTS = new BooleanSetting(GLOBAL, "instantBlockEvents", "Execute block events instantaneously rather than queueing them.");
		public static final BooleanSetting MERGE_SLABS = new BooleanSetting(GLOBAL, "mergeSlabs", "Allow half slabs of the same type to be pushed together and merge into a double slab block. Additionally, sticky surfaces can only move half slabs when making physical contact with the slab. This allows double slabs to be split when one half is pulled but not the other. Note: this setting is most powerful when used in combination with \'movableMovingBlocks\'.");
		public static final BooleanSetting MOVABLE_BLOCK_ENTITIES = new BooleanSetting(GLOBAL, "movableBlockEntities", "Allow blocks with block entities to be moved by pistons.");
		public static final BooleanSetting MOVABLE_BRITTLE_BLOCKS = new BooleanSetting(GLOBAL, "movableBrittleBlocks", "Allow blocks that are usually destroyed by pistons to be moved.");
		public static final BooleanSetting MOVABLE_MOVING_BLOCKS = new BooleanSetting(GLOBAL, "movableMovingBlocks", "Allow moving blocks to be moved by pistons.");
		public static final IntegerSetting POWER_MAX = new IntegerSetting(GLOBAL, "maxPower", "The maximum power output of analogue components like redstone wire, comparators, weighted pressure plates, etc.", 0, Settings.Common.MAX_POWER);
		public static final BooleanSetting RANDOMIZE_BLOCK_EVENTS = new BooleanSetting(GLOBAL, "randomizeBlockEvents", "Randomize the order in which block events are processed.");
		public static final BooleanSetting RANDOMIZE_DELAYS = new BooleanSetting(GLOBAL, "randomizeDelays", "Randomize the delays of all block and fluid ticks that are scheduled.");
		public static final BooleanSetting RANDOMIZE_TICK_PRIORITIES = new BooleanSetting(GLOBAL, "randomizeTickPriorities", "Randomize the tick priorities of all block and fluid ticks that are scheduled.");
		public static final IntegerSetting SCHEDULED_TICK_LIMIT = new IntegerSetting(GLOBAL, "scheduledTickLimit", "The maximum number of scheduled ticks that can be executed each tick.", 0, Integer.MAX_VALUE);
		public static final BooleanSetting SHOW_NEIGHBOR_UPDATES = new BooleanSetting(GLOBAL, "showNeighborUpdates", "When used while worlds tick in \"Step by step\" mode, neighbor updates become scheduled events. The world tick will be paused until all neighbor updates have been executed. Colored boxes are drawn at the location of each neighbor update. The white box is the notifier position, a yellow box is a block update, a blue box a shape update and a red box a comparator update.");
		public static final BooleanSetting SPONTANEOUS_EXPLOSIONS = new BooleanSetting(GLOBAL, "spontaneousExplosions", "Allow redstone components to spontaneously explode if they are looked at the wrong way (in case of abuse with short pulses).");
		public static final BooleanSetting STICKY_CONNECTIONS = new BooleanSetting(GLOBAL, "stickyConnections", "Make blocks like fences and walls stick together when moved if they are connected.");
		public static final WorldTickOptionsSetting WORLD_TICK_OPTIONS = new WorldTickOptionsSetting(GLOBAL, "worldTickOptions", "Options for debugging purposes. In \"Step by step\" mode the world tick will be broken down and each of its phases executed at the given interval of server ticks. Some phases, like those of scheduled ticks, block events and block entities, will break down even further and execute one their actions per interval. Information about the current tick, current world and current phase will be displayed in the top left of the screen. A dimension filter can also be selected to control which dimensions will be affected by the \"Step by step\" mode.");
	}
	
	public static class BugFixes {
		
		private static final SettingsPack BUG_FIXES = new SettingsPack(TWEAKS, "Bug Fixes");
		
		public static final BugFixSetting MC54711 = new BugFixSetting(BUG_FIXES, "MC-54711", "A hacky fix for the chain bug. This patch only changes behavior of quick off-pulses in repeaters and comparators.");
		public static final BugFixSetting MC120986 = new BugFixSetting(BUG_FIXES, "MC-120986", "While not nearly a complete fix for the bug described in the report, this patch does fix comparators not being updated when pistons move blocks with a comparator output.");
		public static final BugFixSetting MC136566 = new BugFixSetting(BUG_FIXES, "MC-136566", "Fixes blocks not being updated when a moved active observer is placed.");
		public static final BugFixSetting MC137127 = new BugFixSetting(BUG_FIXES, "MC-137127", "Fixes observers not being updated when a moved active observer is placed.");
		public static final BugFixSetting MC172213 = new BugFixSetting(BUG_FIXES, "MC-172213", "A fix for the so-called \"input bug\", which causes redstone components to lose 1 tick of delay if activated by a player input. To fix this world time is incremented after all dimensions have been ticked.");
	}
	
	public static class PropertyOverrides {
		
		private static final SettingsPack PROPERTY_OVERRIDES = new SettingsPack(TWEAKS, "Property Overrides");
		
		public static final BooleanSetting CONCRETE_STRONG_POWER = new BooleanSetting(PROPERTY_OVERRIDES, "concreteStrongPower", "Redstone components placed on or attached to concrete blocks have a strong power output based on the color of the concrete (white = 0, orange = 1, etc.).");
		public static final BooleanSetting WOOL_WEAK_POWER = new BooleanSetting(PROPERTY_OVERRIDES, "woolWeakPower", "Redstone components placed on or attached to wool blocks have a weak power output based on the color of the wool (white = 0, orange = 1, etc.).");
		public static final BooleanSetting TERRACOTTA_DELAY = new BooleanSetting(PROPERTY_OVERRIDES, "terracottaDelay", "Redstone components placed on or attached to colored terracotta blocks have a delay based on the color of the terracotta (white = 0, orange = 1, etc.).");
		public static final BooleanSetting TERRACOTTA_MICRO_TICK_MODE = new BooleanSetting(PROPERTY_OVERRIDES, "terracottaMicroTickMode", "Redstone components placed on or attached to a terracotta block are forced into microtick mode.");
		public static final BooleanSetting WOOD_TICK_PRIORITY = new BooleanSetting(PROPERTY_OVERRIDES, "woodTickPriority", "Redstone components placed on or attached to wood or hyphae blocks have a tick priority based on the wood-/hyphaetype (oak = EXTREMELY_HIGH, spruce = VERY_HIGH, etc.).");
	}
	
	public static class ActivatorRail {
		
		private static final SettingsPack ACTIVATOR_RAIL = new SettingsPack(TWEAKS, "Activator Rail");
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ACTIVATOR_RAIL, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ACTIVATOR_RAIL, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ACTIVATOR_RAIL, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ACTIVATOR_RAIL, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting(ACTIVATOR_RAIL, "powerLimit", "The maximum distance power can flow through rails.", 1, 1023);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(ACTIVATOR_RAIL, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ACTIVATOR_RAIL, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ACTIVATOR_RAIL, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ACTIVATOR_RAIL, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Anvil {
		
		private static final SettingsPack ANVIL = new SettingsPack(TWEAKS, "Anvil");
		
		public static final BooleanSetting CRUSH_CONCRETE = new BooleanSetting(ANVIL, "crushConcrete", "Make falling anvils crush concrete into concrete powder.");
		public static final BooleanSetting CRUSH_WOOL = new BooleanSetting(ANVIL, "crushWool", "Make falling anvils flatten wool into carpets.");
	}
	
	public static class Bamboo {
		
		private static final SettingsPack BAMBOO = new SettingsPack(TWEAKS, "Bamboo");
		
		public static final IntegerSetting DELAY = new IntegerSetting(BAMBOO, "delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(BAMBOO, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Barrier {
		
		private static final SettingsPack BARRIER = new SettingsPack(TWEAKS, "Barrier");
		
		public static final BooleanSetting IS_MOVABLE = new BooleanSetting(BARRIER, "isMovable", "Allow pistons to move barriers.");
	}
	
	public static class BubbleColumn {
		
		private static final SettingsPack BUBBLE_COLUMN = new SettingsPack(TWEAKS, "Bubble Column");
		
		public static final IntegerSetting DELAY = new IntegerSetting(BUBBLE_COLUMN, "delay", "Delay before a bubble column is created.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(BUBBLE_COLUMN, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Cactus {
		
		private static final SettingsPack CACTUS = new SettingsPack(TWEAKS, "Cactus");
		
		public static final IntegerSetting DELAY = new IntegerSetting(CACTUS, "delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting NO_U = new BooleanSetting(CACTUS, "noU", "When blocks try to break the cactus, it says \"no u\".");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(CACTUS, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class ChorusPlant {
		
		private static final SettingsPack CHORUS_PLANT = new SettingsPack(TWEAKS, "Chorus Plant");
		
		public static final IntegerSetting DELAY = new IntegerSetting(CHORUS_PLANT, "delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(CHORUS_PLANT, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class CommandBlock {
		
		private static final SettingsPack COMMAND_BLOCK = new SettingsPack(TWEAKS, "Command Block");
		
		public static final IntegerSetting DELAY = new IntegerSetting(COMMAND_BLOCK, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 1, Settings.Common.MAX_DELAY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(COMMAND_BLOCK, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(COMMAND_BLOCK, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(COMMAND_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Comparator {
		
		private static final SettingsPack COMPARATOR = new SettingsPack(TWEAKS, "Comparator");
		
		public static final BooleanSetting ADDITION_MODE = new BooleanSetting(COMPARATOR, "additionMode", "When enabled, the comparator's subtract mode turns into \"addition mode\". The output will be the sum of the back input and the highest side input.");
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(COMPARATOR, "blockUpdateOrder", "The order in which the comparator updates its neighbors when its power output changes.");
		public static final IntegerSetting DELAY = new IntegerSetting(COMPARATOR, "delay", "Delay in ticks before changing power output.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting MICRO_TICK_MODE = new BooleanSetting(COMPARATOR, "microTickMode", Settings.Common.DESC_MICRO_TICK_MODE);
		public static final BooleanSetting REDSTONE_BLOCKS_VALID_SIDE_INPUT = new BooleanSetting(COMPARATOR, "redstoneBlocksValidSideInput", "Count redstone blocks as valid side inputs.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(COMPARATOR, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting(COMPARATOR, "tickPriorityFacingDiode", Settings.Common.DESC_TICK_PRIORITY_FACING_DIODE);
	}
	
	public static class Composter {
		
		private static final SettingsPack COMPOSTER = new SettingsPack(TWEAKS, "Composter");
		
		public static final IntegerSetting DELAY = new IntegerSetting(COMPOSTER, "delay", "The delay in ticks before transitioning from level 7 to level 8.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(COMPOSTER, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Coral {
		
		private static final SettingsPack CORAL = new SettingsPack(TWEAKS, "Coral");
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(CORAL, "delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(CORAL, "delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(CORAL, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class CoralBlock {
		
		private static final SettingsPack CORAL_BLOCK = new SettingsPack(TWEAKS, "Coral Block");
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(CORAL_BLOCK, "delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(CORAL_BLOCK, "delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(CORAL_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class DaylightDetector {
		
		private static final SettingsPack DAYLIGHT_DETECTOR = new SettingsPack(TWEAKS, "Daylight Detector");
		
		public static final BooleanSetting EMITS_STRONG_POWER = new BooleanSetting(DAYLIGHT_DETECTOR, "emitsStrongPower", "When enabled, daylight detectors strongly power the block below them.");
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(DAYLIGHT_DETECTOR, "blockUpdateOrder", "If the emitsStrongPower setting is enabled, this is the order in which daylight detectors update neighboring blocks when their power level changes.");
	}
	
	public static class DetectorRail {
		
		private static final SettingsPack DETECTOR_RAIL = new SettingsPack(TWEAKS, "Detector Rail");
		
		public static final IntegerSetting DELAY = new IntegerSetting(DETECTOR_RAIL, "delay", "Delay in ticks before attempting to depower.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(DETECTOR_RAIL, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(DETECTOR_RAIL, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(DETECTOR_RAIL, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Dispenser {
		
		private static final SettingsPack DISPENSER = new SettingsPack(TWEAKS, "Dispenser");
		
		public static final IntegerSetting DELAY = new IntegerSetting(DISPENSER, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(DISPENSER, "lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(DISPENSER, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(DISPENSER, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(DISPENSER, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class DragonEgg {
		
		private static final SettingsPack DRAGON_EGG = new SettingsPack(TWEAKS, "Dragon Egg");
		
		public static final IntegerSetting DELAY = new IntegerSetting(DRAGON_EGG, "delay", "Delay in ticks before attempting to fall.", 0, Settings.Common.MAX_DELAY);
	}
	
	public static class Dropper {
		
		private static final SettingsPack DROPPER = new SettingsPack(TWEAKS, "Dropper");
		
		public static final IntegerSetting DELAY = new IntegerSetting(DROPPER, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(DROPPER, "lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(DROPPER, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(DROPPER, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(DROPPER, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Farmland {
		
		private static final SettingsPack FARMLAND = new SettingsPack(TWEAKS, "Farmland");
		
		public static final IntegerSetting DELAY = new IntegerSetting(FARMLAND, "delay", "Delay in ticks before turning into dirt.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(FARMLAND, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Fire {
		
		private static final SettingsPack FIRE = new SettingsPack(TWEAKS, "Fire");
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(FIRE, "delayMin", "Minimum delay in ticks.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(FIRE, "delayMax", "Maximum delay in ticks.", 1, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(FIRE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class FrostedIce {
		
		private static final SettingsPack FROSTED_ICE = new SettingsPack(TWEAKS, "Frosted Ice");
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(FROSTED_ICE, "delayMin", "Minimum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(FROSTED_ICE, "delayMax", "Maximum delay in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(FROSTED_ICE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class GrassPath {
		
		private static final SettingsPack GRASS_PATH = new SettingsPack(TWEAKS, "Grass Path");
		
		public static final IntegerSetting DELAY = new IntegerSetting(GRASS_PATH, "delay", "Delay in ticks before turning into dirt.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(GRASS_PATH, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class GravityBlock {
		
		private static final SettingsPack GRAVITY_BLOCK = new SettingsPack(TWEAKS, "Gravity Block");
		
		public static final IntegerSetting DELAY = new IntegerSetting(GRAVITY_BLOCK, "delay", "Delay before attempting to fall.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting SUSPENDED_BY_STICKY_BLOCKS = new BooleanSetting(GRAVITY_BLOCK, "suspendedByStickyBlocks", "Allow gravity blocks to be suspended when next to sticky blocks.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(GRAVITY_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class HayBale {
		
		private static final SettingsPack HAY_BALE = new SettingsPack(TWEAKS, "Hay Bale");
		
		public static final BooleanSetting DIRECTIONALLY_MOVABLE = new BooleanSetting(HAY_BALE, "directionallyMovable", "When enabled, hay bales are only movable along the axis they are aligned with.");
	}
	
	public static class HeavyWeightedPressurePlate {
		
		private static final SettingsPack HEAVY_WEIGHTED_PRESSURE_PLATE = new SettingsPack(TWEAKS, "Heavy Weighted Pressure Plate");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "blockUpdateOrder", "The order in which neighbors are updated when the pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final IntegerSetting WEIGHT = new IntegerSetting(HEAVY_WEIGHTED_PRESSURE_PLATE, "weight", "The number of entities needed for the pressure plate to emit maximum power.", 1, 1023);
	}
	
	public static class Hopper {
		
		private static final SettingsPack HOPPER = new SettingsPack(TWEAKS, "Hopper");
		
		public static final IntegerSetting COOLDOWN_DEFAULT = new IntegerSetting(HOPPER, "cooldownDefault", "The default cooldown after transfering or receiving an item.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting COOLDOWN_PRIORITY = new IntegerSetting(HOPPER, "cooldownPriority", "The cooldown if an item is received from a hopper that ticked at the same time or earlier.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(HOPPER, "delayRisingEdge", "Delay in ticks before being locked.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(HOPPER, "delayFallingEdge", "Delay in ticks before being unlocked", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(HOPPER, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(HOPPER, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(HOPPER, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(HOPPER, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(HOPPER, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(HOPPER, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Lava {
		
		private static final SettingsPack LAVA = new SettingsPack(TWEAKS, "Lava");
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting(LAVA, "delayDefault", "Delay in ticks in non-nether dimensions.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_NETHER = new IntegerSetting(LAVA, "delayNether", "Delay in ticks in the nether dimension.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(LAVA, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Leaves {
		
		private static final SettingsPack LEAVES = new SettingsPack(TWEAKS, "Leaves");
		
		public static final IntegerSetting DELAY = new IntegerSetting(LEAVES, "delay", "Delay in ticks before a leaf block updates its distance to the nearest log.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(LEAVES, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Lectern {
		
		private static final SettingsPack LECTERN = new SettingsPack(TWEAKS, "Lectern");
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(LECTERN, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(LECTERN, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(LECTERN, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(LECTERN, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(LECTERN, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(LECTERN, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Lever {
		
		private static final SettingsPack LEVER = new SettingsPack(TWEAKS, "Lever");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(LEVER, "blockUpdateOrder", "The order in which neighbors are updated when the lever is toggled.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(LEVER, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(LEVER, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(LEVER, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(LEVER, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(LEVER, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(LEVER, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class LightWeightedPressurePlate {
		
		private static final SettingsPack LIGHT_WEIGHTED_PRESSURE_PLATE = new SettingsPack(TWEAKS, "Light Weighted Pressure Plate");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "blockUpdateOrder", "The order in which neighbors are updated when the pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final IntegerSetting WEIGHT = new IntegerSetting(LIGHT_WEIGHTED_PRESSURE_PLATE, "weight", "The number of entities needed for the pressure plate to emit maximum power.", 1, 1023);
	}
	
	public static class MagentaGlazedTerracotta {
		
		private static final SettingsPack MAGENTA_GLAZED_TERRACOTTA = new SettingsPack(TWEAKS, "Magenta Glazed Terracotta");
		
		public static final BooleanSetting IS_POWER_DIODE = new BooleanSetting(MAGENTA_GLAZED_TERRACOTTA, "isPowerDiode", "When enabled, power can only flow through the block in the direction of the arrow on the top side of the block. Additionally, a redstone wire block on top of the block can only power in the direction of the arrow on the top side of the block.");
	}
	
	public static class MagmaBlock {
		
		private static final SettingsPack MAGMA_BLOCK = new SettingsPack(TWEAKS, "Magma Block");
		
		public static final IntegerSetting DELAY = new IntegerSetting(MAGMA_BLOCK, "delay", "Delay in ticks before a magma block updates water above it to create a bubble column.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(MAGMA_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class NormalPiston {
		
		private static final SettingsPack NORMAL_PISTON = new SettingsPack(TWEAKS, "Normal Piston");
		
		public static final BooleanSetting CAN_MOVE_SELF = new BooleanSetting(NORMAL_PISTON, "canMoveSelf", "When enabled, normal pistons will try to push themselves backwards when trying to push an immovable structure.");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(NORMAL_PISTON, "connectsToWire", "When enabled, normal pistons connect to redstone wire.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(NORMAL_PISTON, "delayRisingEdge", "Delay in ticks before extending.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(NORMAL_PISTON, "delayFallingEdge", "Delay in ticks before retracting", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting HEAD_UPDATES_ON_EXTENSION = new BooleanSetting(NORMAL_PISTON, "headUpdatesOnExtension", "Dispatch block updates around the piston head when it starts extending.");
		public static final BooleanSetting IGNORE_POWER_FROM_FRONT = new BooleanSetting(NORMAL_PISTON, "ignorePowerFromFront", "Ignore power received through the piston face.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting(NORMAL_PISTON, "ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting(NORMAL_PISTON, "ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.");
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(NORMAL_PISTON, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(NORMAL_PISTON, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting LOOSE_HEAD = new BooleanSetting(NORMAL_PISTON, "looseHead", "Make the piston head's attachment to the base a little less secure.");
		public static final BooleanSetting MOVABLE_WHEN_EXTENDED = new BooleanSetting(NORMAL_PISTON, "movableWhenExtended", "Allow extended pistons to be moved.");
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting(NORMAL_PISTON, "pushLimit", "The maximum number of blocks a piston can push.", 0, 2048);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(NORMAL_PISTON, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(NORMAL_PISTON, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting(NORMAL_PISTON, "speedRisingEdge", "The duration of the extension in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting(NORMAL_PISTON, "speedFallingEdge", "The duration of the retraction in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting(NORMAL_PISTON, "supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face, without breaking when the piston extends or retracts.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(NORMAL_PISTON, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(NORMAL_PISTON, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final BooleanSetting UPDATE_SELF = new BooleanSetting(NORMAL_PISTON, "updateSelf", "If the piston is unable to extend, it will update itself each tick until it can. This is done using scheduled ticks.");
	}
	
	public static class NoteBlock {
		
		private static final SettingsPack NOTE_BLOCK = new SettingsPack(TWEAKS, "Note Block");
		
		public static final IntegerSetting DELAY = new IntegerSetting(NOTE_BLOCK, "delay", "Delay in ticks before playing a note.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(NOTE_BLOCK, "lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(NOTE_BLOCK, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(NOTE_BLOCK, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(NOTE_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Observer {
		
		private static final SettingsPack OBSERVER = new SettingsPack(TWEAKS, "Observer");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(OBSERVER, "blockUpdateOrder", "The order in which neighbors are updated when an observer powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(OBSERVER, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(OBSERVER, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting DISABLE = new BooleanSetting(OBSERVER, "disable", "Disable observers.");
		public static final BooleanSetting IS_SOLID = new BooleanSetting(OBSERVER, "isSolid", "When enabled, observers are solid blocks.");
		public static final BooleanSetting MICRO_TICK_MODE = new BooleanSetting(OBSERVER, "microTickMode", Settings.Common.DESC_MICRO_TICK_MODE);
		public static final BooleanSetting OBSERVE_BLOCK_UPDATES = new BooleanSetting(OBSERVER, "observeBlockUpdates", "When enabled, observers react to block updates instead of shape updates.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(OBSERVER, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(OBSERVER, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(OBSERVER, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(OBSERVER, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class PoweredRail {
		
		private static final SettingsPack POWERED_RAIL = new SettingsPack(TWEAKS, "Powered Rail");
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(POWERED_RAIL, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(POWERED_RAIL, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(POWERED_RAIL, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(POWERED_RAIL, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting(POWERED_RAIL, "powerLimit", "The maximum distance power can flow through rails.", 1, 1023);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(POWERED_RAIL, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(POWERED_RAIL, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(POWERED_RAIL, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(POWERED_RAIL, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class Rail {
		
		private static final SettingsPack RAIL = new SettingsPack(TWEAKS, "Rail");
		
		public static final IntegerSetting DELAY = new IntegerSetting(RAIL, "delay", "Delay before rails update their shape.", 0, Settings.Common.MAX_DELAY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(RAIL, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(RAIL, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(RAIL, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class RedSand {
		
		private static final SettingsPack RED_SAND = new SettingsPack(TWEAKS, "Red Sand");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(RED_SAND, "blockUpdateOrder", "The order in which indirect neighbors of the block are updated if red sand emits a strong power greater than 0.");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(RED_SAND, "connectsToWire", "When enabled, red sand connects to redstone wire.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(RED_SAND, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(RED_SAND, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
	}
	
	public static class RedstoneBlock {
		
		private static final SettingsPack REDSTONE_BLOCK = new SettingsPack(TWEAKS, "Redstone Block");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(REDSTONE_BLOCK, "blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone blocks emit a strong power greater than 0.");
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(REDSTONE_BLOCK, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(REDSTONE_BLOCK, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
	}
	
	public static class RedstoneLamp {
		
		private static final SettingsPack REDSTONE_LAMP = new SettingsPack(TWEAKS, "Redstone Lamp");
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(REDSTONE_LAMP, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(REDSTONE_LAMP, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(REDSTONE_LAMP, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(REDSTONE_LAMP, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(REDSTONE_LAMP, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(REDSTONE_LAMP, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(REDSTONE_LAMP, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(REDSTONE_LAMP, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class RedstoneOre {
		
		private static final SettingsPack REDSTONE_ORE = new SettingsPack(TWEAKS, "Redstone Ore");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(REDSTONE_ORE, "blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone ore emits a strong power greater than 0.");
		public static final CapacitorBehaviorSetting CAPACITOR_BEHAVIOR = new CapacitorBehaviorSetting(REDSTONE_ORE, "capacitorBehavior", "");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(REDSTONE_ORE, "connectsToWire", "When enabled, redstone ore connects to redstone wire.");
		public static final IntegerSetting DELAY = new IntegerSetting(REDSTONE_ORE, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(REDSTONE_ORE, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(REDSTONE_ORE, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(REDSTONE_ORE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class RedstoneTorch {
		
		private static final SettingsPack REDSTONE_TORCH = new SettingsPack(TWEAKS, "Redstone Torch");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(REDSTONE_TORCH, "blockUpdateOrder", "The order in which neighbors are updated when a redstone torch powers on or off.");
		public static final IntegerSetting BURNOUT_COUNT = new IntegerSetting(REDSTONE_TORCH, "burnoutCount", "The number of times a redstone torch must depower to burn out.", 0, 2048);
		public static final IntegerSetting BURNOUT_TIMER = new IntegerSetting(REDSTONE_TORCH, "burnoutTimer", "The time in ticks during which a redstone torch must depower a set number of times to burn out.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_BURNOUT = new IntegerSetting(REDSTONE_TORCH, "delayBurnout", "The amount of time for which a redstone torch will be burned out.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(REDSTONE_TORCH, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(REDSTONE_TORCH, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(REDSTONE_TORCH, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(REDSTONE_TORCH, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting MICRO_TICK_MODE = new BooleanSetting(REDSTONE_TORCH, "microTickMode", Settings.Common.DESC_MICRO_TICK_MODE);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(REDSTONE_TORCH, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(REDSTONE_TORCH, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final BooleanSetting SOFT_INVERSION = new BooleanSetting(REDSTONE_TORCH, "softInversion", "An implementation of behavior present in Bedrock Edition known as \"soft inversion\". It causes any redstone torch attached to a piston to depower when that piston is powered.");
		public static final TickPrioritySetting TICK_PRIORITY_BURNOUT = new TickPrioritySetting(REDSTONE_TORCH, "tickPriorityBurnout", "The tick priority of the tick scheduled when a redstone torch burns out.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(REDSTONE_TORCH, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(REDSTONE_TORCH, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class RedstoneWire {
		
		private static final SettingsPack REDSTONE_WIRE = new SettingsPack(TWEAKS, "Redstone Wire");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(REDSTONE_WIRE, "blockUpdateOrder", "The order in which redstone wire updates its neighbors after a state change.");
		public static final IntegerSetting DELAY = new IntegerSetting(REDSTONE_WIRE, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting INVERT_FLOW_ON_GLASS = new BooleanSetting(REDSTONE_WIRE, "invertFlowOnGlass", "When enabled, redstone wire power can flow down glass, but not up it.");
		public static final BooleanSetting MICRO_TICK_MODE = new BooleanSetting(REDSTONE_WIRE, "microTickMode", Settings.Common.DESC_MICRO_TICK_MODE);
		public static final BooleanSetting SLABS_ALLOW_UP_CONNECTION = new BooleanSetting(REDSTONE_WIRE, "slabsAllowUpConnection", "When enabled, redstone wire can visually and logically connect to other redstone wire on top of a neighboring slab block.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(REDSTONE_WIRE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Repeater {
		
		private static final SettingsPack REPEATER = new SettingsPack(TWEAKS, "Repeater");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(REPEATER, "blockUpdateOrder", "The order in which neighbors are updated when a repeater powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(REPEATER, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(REPEATER, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(REPEATER, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(REPEATER, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting MICRO_TICK_MODE = new BooleanSetting(REPEATER, "microTickMode", Settings.Common.DESC_MICRO_TICK_MODE);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(REPEATER, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(REPEATER, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(REPEATER, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(REPEATER, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting(REPEATER, "tickPriorityFacingDiode", Settings.Common.DESC_TICK_PRIORITY_FACING_DIODE);
	}
	
	public static class Scaffolding {
		
		private static final SettingsPack SCAFFOLDING = new SettingsPack(TWEAKS, "Scaffolding");
		
		public static final IntegerSetting DELAY = new IntegerSetting(SCAFFOLDING, "delay", "Delay in ticks before a scaffolding block updates its distance to the nearest supported scaffolding block.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(SCAFFOLDING, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Shulker {
		
		private static final SettingsPack SHULKER = new SettingsPack(TWEAKS, "Shulker");
		
		public static final BooleanSetting IS_SOLID = new BooleanSetting(SHULKER, "isSolid ", "When enabled, shulkers act like solid blocks.");
		public static final BooleanSetting UPDATE_NEIGHBORS_WHEN_PEEKING = new BooleanSetting(SHULKER, "updateNeighborsWhenPeeking", "If isSolid is enabled, update neighboring blocks when opening or closing.");
	}
	
	public static class ShulkerBox {
		
		private static final SettingsPack SHULKER_BOX = new SettingsPack(TWEAKS, "Shulker Box");
		
		public static final BooleanSetting UPDATE_NEIGHBORS_WHEN_PEEKING = new BooleanSetting(SHULKER_BOX, "updateNeighborsWhenPeeking", "Update neighboring blocks when opening or closing.");
	}
	
	public static class SoulSand {
		
		private static final SettingsPack SOUL_SAND = new SettingsPack(TWEAKS, "Soul Sand");
		
		public static final IntegerSetting DELAY = new IntegerSetting(SOUL_SAND, "delay", "Delay in ticks before a soulsand block updates water above it to create a bubble column.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(SOUL_SAND, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Stairs {
		
		private static final SettingsPack STAIRS = new SettingsPack(TWEAKS, "Stairs");
		
		public static final BooleanSetting FULL_FACES_ARE_SOLID = new BooleanSetting(STAIRS, "fullFacesAreSolid", "When enabled, all full faces of stair blocks act like full solid blocks, meaning they conduct power and cut off redstone wire.");
	}
	
	public static class StickyPiston {
		
		private static final SettingsPack STICKY_PISTON = new SettingsPack(TWEAKS, "Sticky Piston");
		
		public static final BooleanSetting CAN_MOVE_SELF = new BooleanSetting(STICKY_PISTON, "canMoveSelf", "When enabled, sticky pistons will try to push themselves backwards when trying to push an immovable structure or pull themselves forwards when trying to pull an immovable structure.");
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(STICKY_PISTON, "connectsToWire", "When enabled, sticky pistons connect to redstone wire.");
		public static final BooleanSetting DO_BLOCK_DROPPING = new BooleanSetting(STICKY_PISTON, "doBlockDropping", "When enabled, sticky pistons drop their block when given a short pulse (pulse length less than or equal to their speed).");
		public static final BooleanSetting FAST_BLOCK_DROPPING = new BooleanSetting(STICKY_PISTON, "fastBlockDropping", "When enabled and doBlockDropping is also enabled, sticky pistons will instantly place the block they are dropping.");
		public static final BooleanSetting SUPER_BLOCK_DROPPING = new BooleanSetting(STICKY_PISTON, "superBlockDropping", "When used in combination with doBlockDropping and fastBlockDropping, sticky pistons can drop not just the one block directly in front of them, but all the blocks they are moving.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(STICKY_PISTON, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(STICKY_PISTON, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting HEAD_UPDATES_ON_EXTENSION = new BooleanSetting(STICKY_PISTON, "headUpdatesOnExtension", "Dispatch block updates around the piston head when it starts extending.");
		public static final BooleanSetting HEAD_UPDATES_WHEN_PULLING = new BooleanSetting(STICKY_PISTON, "headUpdatesWhenPulling", "Dispatch block updates around the piston head when it tries pulling a block or structure.");
		public static final BooleanSetting IGNORE_POWER_FROM_FRONT = new BooleanSetting(STICKY_PISTON, "ignorePowerFromFront", "Ignore power received through the piston face.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting(STICKY_PISTON, "ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.");
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting(STICKY_PISTON, "ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.");
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(STICKY_PISTON, "lazyRisingEdge", Settings.Common.DESC_LAZY_RISING_EDGE);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(STICKY_PISTON, "lazyFallingEdge", Settings.Common.DESC_LAZY_FALLING_EDGE);
		public static final BooleanSetting LOOSE_HEAD = new BooleanSetting(STICKY_PISTON, "looseHead", "Make the piston head's attachment to the base a little less secure.");
		public static final BooleanSetting MOVABLE_WHEN_EXTENDED = new BooleanSetting(STICKY_PISTON, "movableWhenExtended", "Allow extended sticky pistons to be moved.");
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting(STICKY_PISTON, "pushLimit", "The maximum number of blocks a piston can push.", 0, 2048);
		public static final IntegerSetting PULL_LIMIT = new IntegerSetting(STICKY_PISTON, "pullLimit", "The maximum number of blocks a piston can pull.", 0, 2048);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(STICKY_PISTON, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(STICKY_PISTON, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting(STICKY_PISTON, "speedRisingEdge", "The duration of the extension in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting(STICKY_PISTON, "speedFallingEdge", "The duration of the retraction in ticks.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting SUPER_STICKY = new BooleanSetting(STICKY_PISTON, "superSticky", "Make the face of sticky pistons stick to blocks when moved.");
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting(STICKY_PISTON, "supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face without breaking when the piston extends or retracts.");
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(STICKY_PISTON, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(STICKY_PISTON, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
		public static final BooleanSetting UPDATE_SELF = new BooleanSetting(STICKY_PISTON, "updateSelf", "If the piston is unable to extend, it will update itself each tick until it can. This is done using scheduled ticks.");
	}
	
	public static class StoneButton {
		
		private static final SettingsPack STONE_BUTTON = new SettingsPack(TWEAKS, "Stone Button");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(STONE_BUTTON, "blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(STONE_BUTTON, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(STONE_BUTTON, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(STONE_BUTTON, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(STONE_BUTTON, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(STONE_BUTTON, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(STONE_BUTTON, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class StonePressurePlate {
		
		private static final SettingsPack STONE_PRESSURE_PLATE = new SettingsPack(TWEAKS, "Stone Pressure Plate");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(STONE_PRESSURE_PLATE, "blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers or depowers.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(STONE_PRESSURE_PLATE, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(STONE_PRESSURE_PLATE, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(STONE_PRESSURE_PLATE, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(STONE_PRESSURE_PLATE, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(STONE_PRESSURE_PLATE, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(STONE_PRESSURE_PLATE, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class SugarCane {
		
		private static final SettingsPack SUGAR_CANE = new SettingsPack(TWEAKS, "Sugar Cane");
		
		public static final IntegerSetting DELAY = new IntegerSetting(SUGAR_CANE, "delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(SUGAR_CANE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TargetBlock {
		
		private static final SettingsPack TARGET_BLOCK = new SettingsPack(TWEAKS, "Target Block");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(TARGET_BLOCK, "blockUpdateOrder", "When emitsStrongPower is enabled, this is the order in which neighboring blocks are updated when a target block powers on or off.");
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting(TARGET_BLOCK, "delayDefault", "The default delay in ticks before powering off.", 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_PERSISTENT_PROJECTILE = new IntegerSetting(TARGET_BLOCK, "delayPersistentProjectile", "The delay in ticks before powering off when hit by a persistent projectile, like an arrow or a trident.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting EMITS_STRONG_POWER = new BooleanSetting(TARGET_BLOCK, "emitsStrongPower", "Allow target blocks to strongly power blocks around them.");
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(TARGET_BLOCK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TNT {
		
		private static final SettingsPack TNT = new SettingsPack(TWEAKS, "TNT");
		
		public static final IntegerSetting DELAY = new IntegerSetting(TNT, "delay", Settings.Common.DESC_DELAY_ACTIVATING, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting FUSE_TIME = new IntegerSetting(TNT, "fuseTime", "The delay in ticks before a TNT entity explodes.", 0, Settings.Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(TNT, "lazy", Settings.Common.DESC_LAZY);
		public static final DirectionToBooleanSetting QC = new DirectionToBooleanSetting(TNT, "quasiConnectivity", Settings.Common.DESC_QC);
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(TNT, "randomizeQuasiConnectivity", Settings.Common.DESC_RANDOMIZE_QC);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(TNT, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Tripwire {
		
		private static final SettingsPack TRIPWIRE = new SettingsPack(TWEAKS, "Tripwire");
		
		public static final IntegerSetting DELAY = new IntegerSetting(TRIPWIRE, "delay", "Delay in ticks before attempting to power off.", 1, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(TRIPWIRE, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class TripwireHook {
		
		private static final SettingsPack TRIPWIRE_HOOK = new SettingsPack(TWEAKS, "Tripwire Hook");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(TRIPWIRE_HOOK, "blockUpdateOrder", "The order in which neighboring blocks are updated when a tripwire hook powers on or off.");
		public static final IntegerSetting DELAY = new IntegerSetting(TRIPWIRE_HOOK, "delay", "Delay in ticks before attempting to power off.", 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(TRIPWIRE_HOOK, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(TRIPWIRE_HOOK, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(TRIPWIRE_HOOK, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Vines {
		
		private static final SettingsPack VINES = new SettingsPack(TWEAKS, "Vines");
		
		public static final IntegerSetting DELAY = new IntegerSetting(VINES, "delay", Settings.Common.DESC_DELAY_BREAKING, 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(VINES, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class Water {
		
		private static final SettingsPack WATER = new SettingsPack(TWEAKS, "Water");
		
		public static final IntegerSetting DELAY = new IntegerSetting(WATER, "delay", "Delay in ticks before attempting to flow.", 0, Settings.Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(WATER, "tickPriority", Settings.Common.DESC_TICK_PRIORITY);
	}
	
	public static class WhiteConcretePowder {
		
		private static final SettingsPack WHITE_CONCRETE_POWDER = new SettingsPack(TWEAKS, "White Concrete Powder");
		
		public static final BooleanSetting IS_SOLID = new BooleanSetting(WHITE_CONCRETE_POWDER, "isSolid", "When enabled white concrete powder is solid and will conduct redstone power.");
	}
	
	public static class WoodenButton {
		
		private static final SettingsPack WOODEN_BUTTON = new SettingsPack(TWEAKS, "Wooden Button");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(WOODEN_BUTTON, "blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles..");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(WOODEN_BUTTON, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(WOODEN_BUTTON, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(WOODEN_BUTTON, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(WOODEN_BUTTON, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(WOODEN_BUTTON, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(WOODEN_BUTTON, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static class WoodenPressurePlate {
		
		private static final SettingsPack WOODEN_PRESSURE_PLATE = new SettingsPack(TWEAKS, "Wooden Pressure Plate");
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(WOODEN_PRESSURE_PLATE, "blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers on or off.");
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(WOODEN_PRESSURE_PLATE, "delayRisingEdge", Settings.Common.DESC_DELAY_RISING_EDGE, 0, Settings.Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(WOODEN_PRESSURE_PLATE, "delayFallingEdge", Settings.Common.DESC_DELAY_FALLING_EDGE, 1, Settings.Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(WOODEN_PRESSURE_PLATE, "weakPower", Settings.Common.DESC_POWER_WEAK, 0, Settings.Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(WOODEN_PRESSURE_PLATE, "strongPower", Settings.Common.DESC_POWER_STRONG, 0, Settings.Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(WOODEN_PRESSURE_PLATE, "tickPriorityRisingEdge", Settings.Common.DESC_TICK_PRIORITY_RISING_EDGE);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(WOODEN_PRESSURE_PLATE, "tickPriorityFallingEdge", Settings.Common.DESC_TICK_PRIORITY_FALLING_EDGE);
	}
	
	public static void init() {
		Settings.register(TWEAKS);
		
		Settings.register(Global.GLOBAL);
		Settings.register(Global.BLOCK_EVENT_LIMIT);
		Settings.register(Global.BLOCK_UPDATE_ORDER);
		Settings.register(Global.COMPARATOR_UPDATE_ORDER);
		Settings.register(Global.SHAPE_UPDATE_ORDER);
		Settings.register(Global.CHAINSTONE);
		Settings.register(Global.DELAY_MULTIPLIER);
		Settings.register(Global.DO_BLOCK_UPDATES);
		Settings.register(Global.DO_COMPARATOR_UPDATES);
		Settings.register(Global.DO_SHAPE_UPDATES);
		Settings.register(Global.DOUBLE_RETRACTION);
		Settings.register(Global.INSTANT_BLOCK_EVENTS);
		Settings.register(Global.MERGE_SLABS);
		Settings.register(Global.MOVABLE_BLOCK_ENTITIES);
		Settings.register(Global.MOVABLE_BRITTLE_BLOCKS);
		Settings.register(Global.MOVABLE_MOVING_BLOCKS);
		Settings.register(Global.POWER_MAX);
		Settings.register(Global.RANDOMIZE_BLOCK_EVENTS);
		Settings.register(Global.RANDOMIZE_DELAYS);
		Settings.register(Global.RANDOMIZE_TICK_PRIORITIES);
		Settings.register(Global.SCHEDULED_TICK_LIMIT);
		Settings.register(Global.SHOW_NEIGHBOR_UPDATES);
		Settings.register(Global.SPONTANEOUS_EXPLOSIONS);
		Settings.register(Global.STICKY_CONNECTIONS);
		Settings.register(Global.WORLD_TICK_OPTIONS);
		
		Settings.register(BugFixes.BUG_FIXES);
		Settings.register(BugFixes.MC54711);
		Settings.register(BugFixes.MC120986);
		Settings.register(BugFixes.MC136566);
		Settings.register(BugFixes.MC137127);
		Settings.register(BugFixes.MC172213);
		
		Settings.register(PropertyOverrides.PROPERTY_OVERRIDES);
		Settings.register(PropertyOverrides.CONCRETE_STRONG_POWER);
		Settings.register(PropertyOverrides.WOOL_WEAK_POWER);
		Settings.register(PropertyOverrides.TERRACOTTA_DELAY);
		Settings.register(PropertyOverrides.TERRACOTTA_MICRO_TICK_MODE);
		Settings.register(PropertyOverrides.WOOD_TICK_PRIORITY);
		
		Settings.register(Anvil.ANVIL);
		Settings.register(Anvil.CRUSH_CONCRETE);
		Settings.register(Anvil.CRUSH_WOOL);
		
		Settings.register(ActivatorRail.ACTIVATOR_RAIL);
		Settings.register(ActivatorRail.DELAY_RISING_EDGE);
		Settings.register(ActivatorRail.DELAY_FALLING_EDGE);
		Settings.register(ActivatorRail.LAZY_RISING_EDGE);
		Settings.register(ActivatorRail.LAZY_FALLING_EDGE);
		Settings.register(ActivatorRail.POWER_LIMIT);
		Settings.register(ActivatorRail.QC);
		Settings.register(ActivatorRail.RANDOMIZE_QC);
		Settings.register(ActivatorRail.TICK_PRIORITY_RISING_EDGE);
		Settings.register(ActivatorRail.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(Bamboo.BAMBOO);
		Settings.register(Bamboo.DELAY);
		Settings.register(Bamboo.TICK_PRIORITY);
		
		Settings.register(Barrier.BARRIER);
		Settings.register(Barrier.IS_MOVABLE);
		
		Settings.register(BubbleColumn.BUBBLE_COLUMN);
		Settings.register(BubbleColumn.DELAY);
		Settings.register(BubbleColumn.TICK_PRIORITY);
		
		Settings.register(Cactus.CACTUS);
		Settings.register(Cactus.DELAY);
		Settings.register(Cactus.NO_U);
		Settings.register(Cactus.TICK_PRIORITY);
		
		Settings.register(ChorusPlant.CHORUS_PLANT);
		Settings.register(ChorusPlant.DELAY);
		Settings.register(ChorusPlant.TICK_PRIORITY);
		
		Settings.register(CommandBlock.COMMAND_BLOCK);
		Settings.register(CommandBlock.DELAY);
		Settings.register(CommandBlock.QC);
		Settings.register(CommandBlock.RANDOMIZE_QC);
		Settings.register(CommandBlock.TICK_PRIORITY);
		
		Settings.register(Comparator.COMPARATOR);
		Settings.register(Comparator.ADDITION_MODE);
		Settings.register(Comparator.BLOCK_UPDATE_ORDER);
		Settings.register(Comparator.DELAY);
		Settings.register(Comparator.MICRO_TICK_MODE);
		Settings.register(Comparator.REDSTONE_BLOCKS_VALID_SIDE_INPUT);
		Settings.register(Comparator.TICK_PRIORITY);
		Settings.register(Comparator.TICK_PRIORITY_FACING_DIODE);
		
		Settings.register(Composter.COMPOSTER);
		Settings.register(Composter.DELAY);
		Settings.register(Composter.TICK_PRIORITY);
		
		Settings.register(Coral.CORAL);
		Settings.register(Coral.DELAY_MIN);
		Settings.register(Coral.DELAY_MAX);
		Settings.register(Coral.TICK_PRIORITY);
		
		Settings.register(CoralBlock.CORAL_BLOCK);
		Settings.register(CoralBlock.DELAY_MIN);
		Settings.register(CoralBlock.DELAY_MAX);
		Settings.register(CoralBlock.TICK_PRIORITY);
		
		Settings.register(DaylightDetector.DAYLIGHT_DETECTOR);
		Settings.register(DaylightDetector.BLOCK_UPDATE_ORDER);
		Settings.register(DaylightDetector.EMITS_STRONG_POWER);
		
		Settings.register(DetectorRail.DETECTOR_RAIL);
		Settings.register(DetectorRail.DELAY);
		Settings.register(DetectorRail.POWER_WEAK);
		Settings.register(DetectorRail.POWER_STRONG);
		Settings.register(DetectorRail.TICK_PRIORITY);
		
		Settings.register(Dispenser.DISPENSER);
		Settings.register(Dispenser.DELAY);
		Settings.register(Dispenser.LAZY);
		Settings.register(Dispenser.QC);
		Settings.register(Dispenser.RANDOMIZE_QC);
		Settings.register(Dispenser.TICK_PRIORITY);
		
		Settings.register(DragonEgg.DRAGON_EGG);
		Settings.register(DragonEgg.DELAY);
		
		Settings.register(Dropper.DROPPER);
		Settings.register(Dropper.DELAY);
		Settings.register(Dropper.LAZY);
		Settings.register(Dropper.QC);
		Settings.register(Dropper.RANDOMIZE_QC);
		Settings.register(Dropper.TICK_PRIORITY);
		
		Settings.register(Farmland.FARMLAND);
		Settings.register(Farmland.DELAY);
		Settings.register(Farmland.TICK_PRIORITY);
		
		Settings.register(Fire.FIRE);
		Settings.register(Fire.DELAY_MIN);
		Settings.register(Fire.DELAY_MAX);
		Settings.register(Fire.TICK_PRIORITY);
		
		Settings.register(FrostedIce.FROSTED_ICE);
		Settings.register(FrostedIce.DELAY_MIN);
		Settings.register(FrostedIce.DELAY_MAX);
		Settings.register(FrostedIce.TICK_PRIORITY);
		
		Settings.register(GrassPath.GRASS_PATH);
		Settings.register(GrassPath.DELAY);
		Settings.register(GrassPath.TICK_PRIORITY);
		
		Settings.register(GravityBlock.GRAVITY_BLOCK);
		Settings.register(GravityBlock.DELAY);
		Settings.register(GravityBlock.SUSPENDED_BY_STICKY_BLOCKS);
		Settings.register(GravityBlock.TICK_PRIORITY);
		
		Settings.register(HayBale.HAY_BALE);
		Settings.register(HayBale.DIRECTIONALLY_MOVABLE);
		
		Settings.register(HeavyWeightedPressurePlate.HEAVY_WEIGHTED_PRESSURE_PLATE);
		Settings.register(HeavyWeightedPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(HeavyWeightedPressurePlate.DELAY_RISING_EDGE);
		Settings.register(HeavyWeightedPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(HeavyWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(HeavyWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(HeavyWeightedPressurePlate.WEIGHT);
		
		Settings.register(Hopper.HOPPER);
		Settings.register(Hopper.COOLDOWN_DEFAULT);
		Settings.register(Hopper.COOLDOWN_PRIORITY);
		Settings.register(Hopper.DELAY_RISING_EDGE);
		Settings.register(Hopper.DELAY_FALLING_EDGE);
		Settings.register(Hopper.LAZY_RISING_EDGE);
		Settings.register(Hopper.LAZY_FALLING_EDGE);
		Settings.register(Hopper.QC);
		Settings.register(Hopper.RANDOMIZE_QC);
		Settings.register(Hopper.TICK_PRIORITY_RISING_EDGE);
		Settings.register(Hopper.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(Lava.LAVA);
		Settings.register(Lava.DELAY_DEFAULT);
		Settings.register(Lava.DELAY_NETHER);
		Settings.register(Lava.TICK_PRIORITY);
		
		Settings.register(Leaves.LEAVES);
		Settings.register(Leaves.DELAY);
		Settings.register(Leaves.TICK_PRIORITY);
		
		Settings.register(Lectern.LECTERN);
		Settings.register(Lectern.DELAY_RISING_EDGE);
		Settings.register(Lectern.DELAY_FALLING_EDGE);
		Settings.register(Lectern.POWER_WEAK);
		Settings.register(Lectern.POWER_STRONG);
		Settings.register(Lectern.TICK_PRIORITY_RISING_EDGE);
		Settings.register(Lectern.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(Lever.LEVER);
		Settings.register(Lever.BLOCK_UPDATE_ORDER);
		Settings.register(Lever.DELAY_RISING_EDGE);
		Settings.register(Lever.DELAY_FALLING_EDGE);
		Settings.register(Lever.POWER_WEAK);
		Settings.register(Lever.POWER_STRONG);
		Settings.register(Lever.TICK_PRIORITY_RISING_EDGE);
		Settings.register(Lever.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(LightWeightedPressurePlate.LIGHT_WEIGHTED_PRESSURE_PLATE);
		Settings.register(LightWeightedPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(LightWeightedPressurePlate.DELAY_RISING_EDGE);
		Settings.register(LightWeightedPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(LightWeightedPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(LightWeightedPressurePlate.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(LightWeightedPressurePlate.WEIGHT);
		
		Settings.register(MagentaGlazedTerracotta.MAGENTA_GLAZED_TERRACOTTA);
		Settings.register(MagentaGlazedTerracotta.IS_POWER_DIODE);
		
		Settings.register(MagmaBlock.MAGMA_BLOCK);
		Settings.register(MagmaBlock.DELAY);
		Settings.register(MagmaBlock.TICK_PRIORITY);
		
		Settings.register(NormalPiston.NORMAL_PISTON);
		Settings.register(NormalPiston.CAN_MOVE_SELF);
		Settings.register(NormalPiston.CONNECTS_TO_WIRE);
		Settings.register(NormalPiston.DELAY_RISING_EDGE);
		Settings.register(NormalPiston.DELAY_FALLING_EDGE);
		Settings.register(NormalPiston.HEAD_UPDATES_ON_EXTENSION);
		Settings.register(NormalPiston.IGNORE_POWER_FROM_FRONT);
		Settings.register(NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING);
		Settings.register(NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING);
		Settings.register(NormalPiston.LAZY_RISING_EDGE);
		Settings.register(NormalPiston.LAZY_FALLING_EDGE);
		Settings.register(NormalPiston.LOOSE_HEAD);
		Settings.register(NormalPiston.MOVABLE_WHEN_EXTENDED);
		Settings.register(NormalPiston.PUSH_LIMIT);
		Settings.register(NormalPiston.QC);
		Settings.register(NormalPiston.RANDOMIZE_QC);
		Settings.register(NormalPiston.SPEED_RISING_EDGE);
		Settings.register(NormalPiston.SPEED_FALLING_EDGE);
		Settings.register(NormalPiston.SUPPORTS_BRITTLE_BLOCKS);
		Settings.register(NormalPiston.TICK_PRIORITY_RISING_EDGE);
		Settings.register(NormalPiston.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(NormalPiston.UPDATE_SELF);
		
		Settings.register(NoteBlock.NOTE_BLOCK);
		Settings.register(NoteBlock.DELAY);
		Settings.register(NoteBlock.LAZY);
		Settings.register(NoteBlock.QC);
		Settings.register(NoteBlock.RANDOMIZE_QC);
		Settings.register(NoteBlock.TICK_PRIORITY);
		
		Settings.register(Observer.OBSERVER);
		Settings.register(Observer.BLOCK_UPDATE_ORDER);
		Settings.register(Observer.DELAY_RISING_EDGE);
		Settings.register(Observer.DELAY_FALLING_EDGE);
		Settings.register(Observer.DISABLE);
		Settings.register(Observer.IS_SOLID);
		Settings.register(Observer.MICRO_TICK_MODE);
		Settings.register(Observer.OBSERVE_BLOCK_UPDATES);
		Settings.register(Observer.POWER_WEAK);
		Settings.register(Observer.POWER_STRONG);
		Settings.register(Observer.TICK_PRIORITY_RISING_EDGE);
		Settings.register(Observer.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(PoweredRail.POWERED_RAIL);
		Settings.register(PoweredRail.DELAY_RISING_EDGE);
		Settings.register(PoweredRail.DELAY_FALLING_EDGE);
		Settings.register(PoweredRail.LAZY_RISING_EDGE);
		Settings.register(PoweredRail.LAZY_FALLING_EDGE);
		Settings.register(PoweredRail.POWER_LIMIT);
		Settings.register(PoweredRail.QC);
		Settings.register(PoweredRail.RANDOMIZE_QC);
		Settings.register(PoweredRail.TICK_PRIORITY_RISING_EDGE);
		Settings.register(PoweredRail.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(Rail.RAIL);
		Settings.register(Rail.DELAY);
		Settings.register(Rail.QC);
		Settings.register(Rail.RANDOMIZE_QC);
		Settings.register(Rail.TICK_PRIORITY);
		
		Settings.register(RedSand.RED_SAND);
		Settings.register(RedSand.BLOCK_UPDATE_ORDER);
		Settings.register(RedSand.CONNECTS_TO_WIRE);
		Settings.register(RedSand.POWER_WEAK);
		Settings.register(RedSand.POWER_STRONG);
		
		Settings.register(RedstoneBlock.REDSTONE_BLOCK);
		Settings.register(RedstoneBlock.BLOCK_UPDATE_ORDER);
		Settings.register(RedstoneBlock.POWER_WEAK);
		Settings.register(RedstoneBlock.POWER_STRONG);
		
		Settings.register(RedstoneLamp.REDSTONE_LAMP);
		Settings.register(RedstoneLamp.DELAY_RISING_EDGE);
		Settings.register(RedstoneLamp.DELAY_FALLING_EDGE);
		Settings.register(RedstoneLamp.LAZY_RISING_EDGE);
		Settings.register(RedstoneLamp.LAZY_FALLING_EDGE);
		Settings.register(RedstoneLamp.QC);
		Settings.register(RedstoneLamp.RANDOMIZE_QC);
		Settings.register(RedstoneLamp.TICK_PRIORITY_RISING_EDGE);
		Settings.register(RedstoneLamp.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(RedstoneOre.REDSTONE_ORE);
		Settings.register(RedstoneOre.BLOCK_UPDATE_ORDER);
		Settings.register(RedstoneOre.CAPACITOR_BEHAVIOR);
		Settings.register(RedstoneOre.CONNECTS_TO_WIRE);
		Settings.register(RedstoneOre.DELAY);
		Settings.register(RedstoneOre.POWER_WEAK);
		Settings.register(RedstoneOre.POWER_STRONG);
		Settings.register(RedstoneOre.TICK_PRIORITY);
		
		Settings.register(RedstoneTorch.REDSTONE_TORCH);
		Settings.register(RedstoneTorch.BLOCK_UPDATE_ORDER);
		Settings.register(RedstoneTorch.BURNOUT_COUNT);
		Settings.register(RedstoneTorch.BURNOUT_TIMER);
		Settings.register(RedstoneTorch.DELAY_BURNOUT);
		Settings.register(RedstoneTorch.DELAY_RISING_EDGE);
		Settings.register(RedstoneTorch.DELAY_FALLING_EDGE);
		Settings.register(RedstoneTorch.LAZY_RISING_EDGE);
		Settings.register(RedstoneTorch.LAZY_FALLING_EDGE);
		Settings.register(RedstoneTorch.MICRO_TICK_MODE);
		Settings.register(RedstoneTorch.POWER_WEAK);
		Settings.register(RedstoneTorch.POWER_STRONG);
		Settings.register(RedstoneTorch.SOFT_INVERSION);
		Settings.register(RedstoneTorch.TICK_PRIORITY_BURNOUT);
		Settings.register(RedstoneTorch.TICK_PRIORITY_RISING_EDGE);
		Settings.register(RedstoneTorch.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(RedstoneWire.REDSTONE_WIRE);
		Settings.register(RedstoneWire.BLOCK_UPDATE_ORDER);
		Settings.register(RedstoneWire.DELAY);
		Settings.register(RedstoneWire.INVERT_FLOW_ON_GLASS);
		Settings.register(RedstoneWire.MICRO_TICK_MODE);
		Settings.register(RedstoneWire.SLABS_ALLOW_UP_CONNECTION);
		Settings.register(RedstoneWire.TICK_PRIORITY);
		
		Settings.register(Repeater.REPEATER);
		Settings.register(Repeater.BLOCK_UPDATE_ORDER);
		Settings.register(Repeater.DELAY_RISING_EDGE);
		Settings.register(Repeater.DELAY_FALLING_EDGE);
		Settings.register(Repeater.LAZY_RISING_EDGE);
		Settings.register(Repeater.LAZY_FALLING_EDGE);
		Settings.register(Repeater.MICRO_TICK_MODE);
		Settings.register(Repeater.POWER_WEAK);
		Settings.register(Repeater.POWER_STRONG);
		Settings.register(Repeater.TICK_PRIORITY_RISING_EDGE);
		Settings.register(Repeater.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(Repeater.TICK_PRIORITY_FACING_DIODE);
		
		Settings.register(Scaffolding.SCAFFOLDING);
		Settings.register(Scaffolding.DELAY);
		Settings.register(Scaffolding.TICK_PRIORITY);
		
		Settings.register(Shulker.SHULKER);
		Settings.register(Shulker.IS_SOLID);
		Settings.register(Shulker.UPDATE_NEIGHBORS_WHEN_PEEKING);
		
		Settings.register(ShulkerBox.SHULKER_BOX);
		Settings.register(ShulkerBox.UPDATE_NEIGHBORS_WHEN_PEEKING);
		
		Settings.register(SoulSand.SOUL_SAND);
		Settings.register(SoulSand.DELAY);
		Settings.register(SoulSand.TICK_PRIORITY);
		
		Settings.register(Stairs.STAIRS);
		Settings.register(Stairs.FULL_FACES_ARE_SOLID);
		
		Settings.register(StickyPiston.STICKY_PISTON);
		Settings.register(StickyPiston.CAN_MOVE_SELF);
		Settings.register(StickyPiston.CONNECTS_TO_WIRE);
		Settings.register(StickyPiston.DO_BLOCK_DROPPING);
		Settings.register(StickyPiston.FAST_BLOCK_DROPPING);
		Settings.register(StickyPiston.SUPER_BLOCK_DROPPING);
		Settings.register(StickyPiston.DELAY_RISING_EDGE);
		Settings.register(StickyPiston.DELAY_FALLING_EDGE);
		Settings.register(StickyPiston.HEAD_UPDATES_ON_EXTENSION);
		Settings.register(StickyPiston.HEAD_UPDATES_WHEN_PULLING);
		Settings.register(StickyPiston.IGNORE_POWER_FROM_FRONT);
		Settings.register(StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING);
		Settings.register(StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING);
		Settings.register(StickyPiston.LAZY_RISING_EDGE);
		Settings.register(StickyPiston.LAZY_FALLING_EDGE);
		Settings.register(StickyPiston.LOOSE_HEAD);
		Settings.register(StickyPiston.MOVABLE_WHEN_EXTENDED);
		Settings.register(StickyPiston.PUSH_LIMIT);
		Settings.register(StickyPiston.PULL_LIMIT);
		Settings.register(StickyPiston.QC);
		Settings.register(StickyPiston.RANDOMIZE_QC);
		Settings.register(StickyPiston.SPEED_RISING_EDGE);
		Settings.register(StickyPiston.SPEED_FALLING_EDGE);
		Settings.register(StickyPiston.SUPER_STICKY);
		Settings.register(StickyPiston.SUPPORTS_BRITTLE_BLOCKS);
		Settings.register(StickyPiston.TICK_PRIORITY_RISING_EDGE);
		Settings.register(StickyPiston.TICK_PRIORITY_FALLING_EDGE);
		Settings.register(StickyPiston.UPDATE_SELF);
		
		Settings.register(StoneButton.STONE_BUTTON);
		Settings.register(StoneButton.BLOCK_UPDATE_ORDER);
		Settings.register(StoneButton.DELAY_RISING_EDGE);
		Settings.register(StoneButton.DELAY_FALLING_EDGE);
		Settings.register(StoneButton.POWER_WEAK);
		Settings.register(StoneButton.POWER_STRONG);
		Settings.register(StoneButton.TICK_PRIORITY_RISING_EDGE);
		Settings.register(StoneButton.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(StonePressurePlate.STONE_PRESSURE_PLATE);
		Settings.register(StonePressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(StonePressurePlate.DELAY_RISING_EDGE);
		Settings.register(StonePressurePlate.DELAY_FALLING_EDGE);
		Settings.register(StonePressurePlate.POWER_WEAK);
		Settings.register(StonePressurePlate.POWER_STRONG);
		Settings.register(StonePressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(StonePressurePlate.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(SugarCane.SUGAR_CANE);
		Settings.register(SugarCane.DELAY);
		Settings.register(SugarCane.TICK_PRIORITY);
		
		Settings.register(TargetBlock.TARGET_BLOCK);
		Settings.register(TargetBlock.BLOCK_UPDATE_ORDER);
		Settings.register(TargetBlock.DELAY_DEFAULT);
		Settings.register(TargetBlock.DELAY_PERSISTENT_PROJECTILE);
		Settings.register(TargetBlock.EMITS_STRONG_POWER);
		Settings.register(TargetBlock.TICK_PRIORITY);
		
		Settings.register(TNT.TNT);
		Settings.register(TNT.DELAY);
		Settings.register(TNT.FUSE_TIME);
		Settings.register(TNT.LAZY);
		Settings.register(TNT.QC);
		Settings.register(TNT.RANDOMIZE_QC);
		Settings.register(TNT.TICK_PRIORITY);
		
		Settings.register(Tripwire.TRIPWIRE);
		Settings.register(Tripwire.DELAY);
		Settings.register(Tripwire.TICK_PRIORITY);
		
		Settings.register(TripwireHook.TRIPWIRE_HOOK);
		Settings.register(TripwireHook.BLOCK_UPDATE_ORDER);
		Settings.register(TripwireHook.DELAY);
		Settings.register(TripwireHook.POWER_WEAK);
		Settings.register(TripwireHook.POWER_STRONG);
		Settings.register(TripwireHook.TICK_PRIORITY);
		
		Settings.register(Vines.VINES);
		Settings.register(Vines.DELAY);
		Settings.register(Vines.TICK_PRIORITY);
		
		Settings.register(Water.WATER);
		Settings.register(Water.DELAY);
		Settings.register(Water.TICK_PRIORITY);
		
		Settings.register(WhiteConcretePowder.WHITE_CONCRETE_POWDER);
		Settings.register(WhiteConcretePowder.IS_SOLID);
		
		Settings.register(WoodenButton.WOODEN_BUTTON);
		Settings.register(WoodenButton.BLOCK_UPDATE_ORDER);
		Settings.register(WoodenButton.DELAY_RISING_EDGE);
		Settings.register(WoodenButton.DELAY_FALLING_EDGE);
		Settings.register(WoodenButton.POWER_WEAK);
		Settings.register(WoodenButton.POWER_STRONG);
		Settings.register(WoodenButton.TICK_PRIORITY_RISING_EDGE);
		Settings.register(WoodenButton.TICK_PRIORITY_FALLING_EDGE);
		
		Settings.register(WoodenPressurePlate.WOODEN_PRESSURE_PLATE);
		Settings.register(WoodenPressurePlate.BLOCK_UPDATE_ORDER);
		Settings.register(WoodenPressurePlate.DELAY_RISING_EDGE);
		Settings.register(WoodenPressurePlate.DELAY_FALLING_EDGE);
		Settings.register(WoodenPressurePlate.POWER_WEAK);
		Settings.register(WoodenPressurePlate.POWER_STRONG);
		Settings.register(WoodenPressurePlate.TICK_PRIORITY_RISING_EDGE);
		Settings.register(WoodenPressurePlate.TICK_PRIORITY_FALLING_EDGE);
	}
}
