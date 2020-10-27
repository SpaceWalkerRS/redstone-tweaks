package redstonetweaks.setting;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.TickPriority;

import redstonetweaks.setting.types.BooleanSetting;
import redstonetweaks.setting.types.BugFixSetting;
import redstonetweaks.setting.types.DirectionalBooleanSetting;
import redstonetweaks.setting.types.ISetting;
import redstonetweaks.setting.types.IntegerSetting;
import redstonetweaks.setting.types.TickPrioritySetting;
import redstonetweaks.setting.types.UpdateOrderSetting;
import redstonetweaks.util.Directionality;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.BlockUpdate;
import redstonetweaks.world.common.UpdateOrder;

public class Settings {
	
	private static class Common {
		
		// Values that are common between several settings
		
		private static final String DESC_DELAY_ACTIVATING = "Delay in ticks before activating.";
		private static final String DESC_DELAY_BREAKING = "Delay in ticks before breaking.";
		private static final String DESC_DELAY_RISING_EDGE = "Delay in ticks before powering on.";
		private static final String DESC_DELAY_FALLING_EDGE = "Delay in ticks before powering off.";
		private static final String DESC_LAZY = "When enabled, the block is \"lazy\". Whenever it is ticked it will activate without checking for\nreceived power.";
		private static final String DESC_LAZY_RISING_EDGE = "When enabled, the block is \"lazy\" on  the rising edge. Whenever it is ticked, if it is\nunpowered, it will power on without checking for received power.";
		private static final String DESC_LAZY_FALLING_EDGE = "When enabled, the block is \"lazy\" on  the falling edge. Whenever it is ticked, if it is powered,\nit will power off without checking for received power.";
		private static final String DESC_POWER_WEAK = "Weak power output.";
		private static final String DESC_POWER_STRONG = "Strong power output.";
		private static final String DESC_QC = "A list of all directions in which quasi connectivity for this block is enabled. If quasi\nconnectivity is enabled in a direction then the block checks for power to its neighbor in that\ndirection.";
		private static final String DESC_RANDOMIZE_QC = "When enabled, quasi connectivity works randomly in all directions where it is enabled.";
		private static final String DESC_TICK_PRIORITY = "The tick priority of any ticks scheduled by this block.";
		private static final String DESC_TICK_PRIORITY_FACING_DIODE = "The tick priority when facing another diode that is not facing it.";
		private static final String DESC_TICK_PRIORITY_RISING_EDGE = "The tick priority of ticks scheduled for the rising edge.";
		private static final String DESC_TICK_PRIORITY_FALLING_EDGE = "The tick priority of ticks scheduled for the falling edge.";
		
		private static final int MAX_DELAY = 1023;
		private static final int MAX_POWER = 1023;
	}
	
	public static class Global {
		
		public static final String ID = "global";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which the world updates neighbors of a block.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL, BlockUpdate.Mode.SINGLE_UPDATE, true).
				add(RelativePos.SELF, RelativePos.WEST).
				add(RelativePos.SELF, RelativePos.EAST).
				add(RelativePos.SELF, RelativePos.DOWN).
				add(RelativePos.SELF, RelativePos.UP).
				add(RelativePos.SELF, RelativePos.NORTH).
				add(RelativePos.SELF, RelativePos.SOUTH));
		public static final IntegerSetting DELAY_MULTIPLIER = new IntegerSetting(ID, "delayMultiplier", "The delay of all scheduled ticks will be multiplied by this value. When set to 0 all scheduled\nticks will be executed instantaneously.", 1, 0, 127);
		public static final BooleanSetting DO_BLOCK_UPDATES = new BooleanSetting(ID, "doBlockUpdates", "Allow worlds to dispatch block updates.", true);
		public static final BooleanSetting DO_SHAPE_UPDATES = new BooleanSetting(ID, "doShapeUpdates", "Allow worlds to dispatch shape updates.", true);
		public static final BooleanSetting DO_COMPARATOR_UPDATES = new BooleanSetting(ID, "doComparatorUpdates", "Allow worlds to dispatch comparator updates.", true);
		public static final BooleanSetting DOUBLE_RETRACTION = new BooleanSetting(ID, "doubleRetraction", "A re-implementation of behavior that was present in 1.3-1.8, known as \"Jeb retraction\" or\n\"instant double retraction\". It creates a very narrow window where unpowered pistons can be moved.", false);
		public static final BooleanSetting INSTANT_BLOCK_EVENTS = new BooleanSetting(ID, "instantBlockEvents", "Execute block events at the moment they are scheduled.", false);
		public static final BooleanSetting MOVABLE_BLOCK_ENTITIES = new BooleanSetting(ID, "movableBlockEntities", "Allow blocks with block entities to be moved by pistons.", false);
		public static final IntegerSetting POWER_MAX = new IntegerSetting(ID, "maxPower", "The maximum power output of analogue components like redstone wire, comparators, weighted pressure\nplates, etc.", 15, 0, Common.MAX_POWER);
		public static final BooleanSetting RANDOMIZE_BLOCK_EVENTS = new BooleanSetting(ID, "randomizeBlockEvents", "Randomize the order in which block events are processed.", false);
		public static final BooleanSetting RANDOMIZE_DELAYS = new BooleanSetting(ID, "randomizeDelays", "Randomize the delays of all block and fluid ticks that are scheduled.", false);
		public static final BooleanSetting RANDOMIZE_TICK_PRIORITIES = new BooleanSetting(ID, "randomizeTickPriorities", "Randomize the tick priorities of all block and fluid ticks that are scheduled.", false);
		public static final BooleanSetting SHOW_NEIGHBOR_UPDATES = new BooleanSetting(ID, "showNeighborUpdates", "When used in combination with showProcessingOrder, neighbor updates become scheduled events.\nThe world tick will be paused until all neighbor updates have been executed. Colored boxes are\ndrawn at the location of each neighbor update. The white box is the notifier position, a yellow\nbox is a block update, a blue box a shape update and a red box a comparator update.", false);
		public static final IntegerSetting SHOW_PROCESSING_ORDER = new IntegerSetting(ID, "showProcessingOrder", "When enabled, the world tick will be broken down and each of its phases being executed at the\ngiven interval of server ticks. Some phases, like those of scheduled ticks, block events and\nblock entities, will break down even further and execute one their actions per interval. Information\nabout the current tick, current world and current phase will be displayed in the top left of the screen.", 0, 0, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_MULTIPLIER, 
				DO_BLOCK_UPDATES,
				DO_SHAPE_UPDATES,
				DO_COMPARATOR_UPDATES,
				DOUBLE_RETRACTION,
				INSTANT_BLOCK_EVENTS,
				MOVABLE_BLOCK_ENTITIES,
				POWER_MAX,
				RANDOMIZE_BLOCK_EVENTS,
				RANDOMIZE_DELAYS,
				RANDOMIZE_TICK_PRIORITIES,
				SHOW_NEIGHBOR_UPDATES,
				SHOW_PROCESSING_ORDER
		);
	}
	
	public static class BugFixes {
		
		public static final String ID = "bugFixes";
		
		public static final BugFixSetting MC54711 = new BugFixSetting("MC-54711", "A hacky fix for the chain bug. This patch only changes behavior of quick off-pulses in\nrepeaters and comparators.");
		public static final BugFixSetting MC120986 = new BugFixSetting("MC-120986", "While not nearly a complete fix for the bug described in the report, this patch does fix\ncomparators not being updated when pistons move blocks with a comparator output.");
		public static final BugFixSetting MC136566 = new BugFixSetting("MC-136566", "Fixes blocks not being updated when a moved active observer materializes.");
		public static final BugFixSetting MC137127 = new BugFixSetting("MC-137127", "Fixes observers not being updated when a moved active observer materializes.");
		public static final BugFixSetting MC172213 = new BugFixSetting("MC-172213", "A fix for the so-called \"input bug\", which causes redstone components to lose 1 tick of delay if\nactivated by a player input. To fix this world time is incremented after all dimensions have been ticked.");
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				MC54711,
				MC120986,
				MC136566,
				MC137127,
				MC172213
		);
	}
	
	public static class ActivatorRail {
		
		public static final String ID = "activatorRail";
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting(ID, "powerLimit", "The maximum distance power can flow through rails.", 9, 1, 1023);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_LIMIT,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Bamboo {
		
		public static final String ID = "bamboo";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_BREAKING, 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Barrier {
		
		public static final String ID = "barrier";
		
		public static final BooleanSetting IS_MOVABLE = new BooleanSetting(ID, "isMovable", "When enabled, barriers can be moved by pistons.", false);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				IS_MOVABLE
		);
	}
	
	public static class BubbleColumn {
		
		public static final String ID = "bubbleColumn";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay before a bubble column is created.", 5, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Cactus {
		
		public static final String ID = "cactus";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_BREAKING, 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class ChorusPlant {
		
		public static final String ID = "chorusPlant";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_BREAKING, 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class CommandBlock {
		
		public static final String ID = "commandBlock";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 1, 1, Common.MAX_DELAY);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Comparator {
		
		public static final String ID = "comparator";
		
		public static final BooleanSetting ADDITION_MODE = new BooleanSetting(ID, "additionMode", "When enabled, the comparator's subtract mode turns into \"addition mode\". The output will be the\nsum of the back input and the highest side input.", false);
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which the comparator updates its neighbors when its power output changes.", new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before changing power output.", 2, 0, Common.MAX_DELAY);
		public static final BooleanSetting REDSTONE_BLOCKS_VALID_SIDE_INPUT = new BooleanSetting(ID, "redstoneBlocksValidSideInput", "Count redstone blocks as valid side inputs.", true);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting(ID, "tickPriorityFacingDiode", Common.DESC_TICK_PRIORITY_FACING_DIODE, TickPriority.HIGH);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				ADDITION_MODE,
				BLOCK_UPDATE_ORDER,
				DELAY,
				REDSTONE_BLOCKS_VALID_SIDE_INPUT,
				TICK_PRIORITY,
				TICK_PRIORITY_FACING_DIODE
		);
	}
	
	public static class Composter {
		
		public static final String ID = "composter";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "The delay in ticks before transitioning from level 7 to level 8.", 20, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Coral {
		
		public static final String ID = "coral";
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(ID, "delayMin", "Minimum delay in ticks.", 60, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(ID, "delayMax", "Maximum delay in ticks.", 100, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class CoralBlock {
		
		public static final String ID = "coralBlock";
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(ID, "delayMin", "Minimum delay in ticks.", 60, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(ID, "delayMax", "Maximum delay in ticks.", 100, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class DetectorRail {
		
		public static final String ID = "detectorRail";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before attempting to depower.", 20, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY
		);
	}
	
	public static class Dispenser {
		
		public static final String ID = "dispenser";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 4, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(ID, "lazy", Common.DESC_LAZY, true);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, true, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class DragonEgg {
		
		public static final String ID = "dragonEgg";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before attempting to fall.", 5, 0, Common.MAX_DELAY);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY
		);
	}
	
	public static class Dropper {
		
		public static final String ID = "dropper";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 4, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(ID, "lazy", Common.DESC_LAZY, true);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, true, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Farmland {
		
		public static final String ID = "farmland";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before turning into dirt.", 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Fire {
		
		public static final String ID = "fire";
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(ID, "delayMin", "Minimum delay in ticks.", 10, 1, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(ID, "delayMax", "Maximum delay in ticks.", 30, 1, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class FrostedIce {
		
		public static final String ID = "frostedIce";
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting(ID, "delayMin", "Minimum delay in ticks.", 20, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting(ID, "delayMax", "Maximum delay in ticks.", 40, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class GrassPath {
		
		public static final String ID = "grassPath";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before turning into dirt.", 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class GravityBlock {
		
		public static final String ID = "gravityBlock";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay before attempting to fall.", 2, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class HeavyWeightedPressurePlate {
		
		public static final String ID = "heavyWeightedPressurePlate";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are update when the pressure plate powers or depowers.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 10, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		public static final IntegerSetting WEIGHT = new IntegerSetting(ID, "weight", "The number of entities needed for the pressure plate to emit maximum power.", 150, 1, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				WEIGHT
		);
	}
	
	public static class Hopper {
		
		public static final String ID = "hopper";
		
		public static final IntegerSetting COOLDOWN_DEFAULT = new IntegerSetting(ID, "cooldownDefault", "The default cooldown after transfering or receiving an item.", 8, 0, Common.MAX_DELAY);
		public static final IntegerSetting COOLDOWN_PRIORITY = new IntegerSetting(ID, "cooldownPriority", "The cooldown if an item is received from a hopper that ticked at the same time or earlier.", 7, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", "Delay in ticks before being locked.", 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", "Delay in ticks before being unlocked", 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				COOLDOWN_DEFAULT,
				COOLDOWN_PRIORITY,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Lava {
		
		public static final String ID = "lava";
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting(ID, "delayDefault", "Delay in ticks in non-nether dimensions.", 30, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_NETHER = new IntegerSetting(ID, "delayNether", "Delay in ticks in the nether dimension.", 10, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_DEFAULT,
				DELAY_NETHER,
				TICK_PRIORITY
		);
	}
	
	public static class Leaves {
		
		public static final String ID = "leaves";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before a leaf block updates its distance to the nearest log.", 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Lectern {
		
		public static final String ID = "lectern";
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Lever {
		
		public static final String ID = "lever";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are updated when the lever is toggled.", new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class LightWeightedPressurePlate {
		
		public static final String ID = "lightWeightedPressurePlate";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are update when the pressure plate powers or depowers.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 10, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		public static final IntegerSetting WEIGHT = new IntegerSetting(ID, "weight", "The number of entities needed for the pressure plate to emit maximum power.", 15, 1, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				WEIGHT
		);
	}
	
	public static class MagentaGlazedTerracotta {
		
		public static final String ID = "magentaGlazedTerracotta";
		
		public static final BooleanSetting IS_POWER_DIODE = new BooleanSetting(ID, "isPowerDiode", "When enabled, power can only flow in the direction of the arrow on the top side of the block.\nAdditionally, a redstone wire block on top of the block can only flow in the direction of the arrow\non the top side of the block.", false);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				IS_POWER_DIODE
		);
	}
	
	public static class MagmaBlock {
		
		public static final String ID = "magmaBlock";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before a magma block updates water above it to create a bubble column.", 20, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class NormalPiston {
		
		public static final String ID = "normalPiston";
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(ID, "connectsToWire", "When enabled, normal pistons connect to redstone wire.", false);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", "Delay in ticks before extending.", 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", "Delay in ticks before retracting", 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting(ID, "supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face, without\nbreaking when the piston extends or retracts.", false);
		public static final BooleanSetting UPDATE_SELF_WHILE_POWERED = new BooleanSetting(ID, "updateSelfWhilePowered", "If the piston is powered but cannot extend, it will update itself each tick until it can extend.\nThis is achieved using scheduled ticks.", false);
		public static final BooleanSetting SUPPRESS_HEAD_UPDATES_ON_EXTENSION = new BooleanSetting(ID, "suppressHeadUpdatesOnExtension", "Suppress block updates emitted by the moving piston head when the piston starts extending.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting(ID, "ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting(ID, "ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.", true);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, true, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting(ID, "speedRisingEdge", "The duration of the extension in ticks.", 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting(ID, "speedFallingEdge", "The duration of the retraction in ticks.", 2, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting(ID, "pushLimit", "The maximum number of blocks a piston can move.", 12, 0, 2048);
		public static final BooleanSetting MERGE_SLABS = new BooleanSetting(ID, "mergeSlabs", "?? FILL ME IN ??", false);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				CONNECTS_TO_WIRE,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				SUPPORTS_BRITTLE_BLOCKS,
				UPDATE_SELF_WHILE_POWERED,
				SUPPRESS_HEAD_UPDATES_ON_EXTENSION,
				IGNORE_UPDATES_WHILE_EXTENDING,
				IGNORE_UPDATES_WHILE_RETRACTING,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				QC,
				RANDOMIZE_QC,
				SPEED_RISING_EDGE,
				SPEED_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				PUSH_LIMIT,
				MERGE_SLABS
		);
	}
	
	public static class NoteBlock {
		
		public static final String ID = "noteBlock";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before playing a note.", 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(ID, "lazy", Common.DESC_LAZY, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Observer {
		
		public static final String ID = "observer";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are updated when an observer powers or depowers.", new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final BooleanSetting DISABLE = new BooleanSetting(ID, "disable", "Disable observers.", false);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				DISABLE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class PoweredRail {
		
		public static final String ID = "poweredRail";
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting(ID, "powerLimit", "The maximum distance power can flow through rails.", 9, 1, 1023);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_LIMIT,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class RedstoneBlock {
		
		public static final String ID = "redstoneBlock";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone blocks emit a strong power greater than 0.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST));
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 0, 0, Common.MAX_POWER);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				POWER_WEAK,
				POWER_STRONG
		);
	}
	
	public static class RedstoneLamp {
		
		public static final String ID = "redstoneLamp";
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 4, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class RedstoneOre {
		
		public static final String ID = "redstoneOre";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which indirect neighbors of the block are updated if redstone ore emits a strong power greater than 0.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.DOWN, RelativePos.UP).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.UP, RelativePos.DOWN).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.NORTH, RelativePos.SOUTH).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.SOUTH, RelativePos.NORTH).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.WEST, RelativePos.EAST).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.EAST, RelativePos.WEST));
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(ID, "connectsToWire", "When enabled, redstone ore connects to redstone wire.", false);
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 0, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 0, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY,
				CONNECTS_TO_WIRE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY
		);
	}
	
	public static class RedstoneTorch {
		
		public static final String ID = "redstoneTorch";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are updated when a redstone torch powers on or off.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
		public static final IntegerSetting BURNOUT_COUNT = new IntegerSetting(ID, "burnoutCount", "The number of times a redstone torch must depower to burn out.", 8, 0, 2048);
		public static final IntegerSetting BURNOUT_TIMER = new IntegerSetting(ID, "burnoutTimer", "The time in ticks during which a redstone torch must depower a set number of times to burn out.", 60, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_BURNOUT = new IntegerSetting(ID, "delayBurnout", "The amount of time for which a redstone torch will be burned out.", 160, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final BooleanSetting SOFT_INVERSION = new BooleanSetting(ID, "softInversion", "An implementation of behavior present in the Bedrock Edition known as \"soft inversion\". It\ncauses a redstone torche attached to a piston to depower when that piston is powered.", false);
		public static final TickPrioritySetting TICK_PRIORITY_BURNOUT = new TickPrioritySetting(ID, "tickPriorityBurnout", "The tick priority of the tick scheduled when a redstone torch burns out.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				BURNOUT_COUNT,
				BURNOUT_TIMER,
				DELAY_BURNOUT,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				SOFT_INVERSION,
				TICK_PRIORITY_BURNOUT,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class RedstoneWire {
		
		public static final String ID = "redstoneWire";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which redstone wire updates its neighbors after a state change.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.LOCATIONAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.UP, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.NORTH, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SOUTH, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.WEST, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.EAST, RelativePos.WEST));
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting INVERT_FLOW_ON_GLASS = new BooleanSetting(ID, "invertFlowOnGlass", "When enabled, redstone wire power can flow down glass, but not up it.", false);
		public static final BooleanSetting SLABS_ALLOW_UP_CONNECTION = new BooleanSetting(ID, "slabsAllowUpConnection", "When enabled, redstone wire can visually and logically connect to other redstone wire on top of\na neighboring slab block.", true);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY,
				INVERT_FLOW_ON_GLASS,
				SLABS_ALLOW_UP_CONNECTION,
				TICK_PRIORITY
		);
	}
	
	public static class Repeater {
		
		public static final String ID = "repeater";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighbors are updated when a repeater powers or depowers.", new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.SINGLE_UPDATE, RelativePos.SELF, RelativePos.FRONT).
				add(BlockUpdate.Mode.NEIGHBORS_EXCEPT, RelativePos.FRONT, RelativePos.BACK));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 2, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, true);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting(ID, "tickPriorityFacingDiode", Common.DESC_TICK_PRIORITY_FACING_DIODE, TickPriority.EXTREMELY_HIGH);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.HIGH);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.VERY_HIGH);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_FACING_DIODE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Scaffolding {
		
		public static final String ID = "scaffolding";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before a scaffolding block updates its distance to the nearest supported scaffolding block.", 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class SoulSand {
		
		public static final String ID = "soulSand";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before a soulsand block updates water above it to create a bubble column.", 20, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Stairs {
		
		public static final String ID = "stairs";
		
		public static final BooleanSetting FULL_FACES_ARE_SOLID = new BooleanSetting(ID, "fullFacesAreSolid", "When enabled, all full faces of stair blocks act like full solid blocks, meaning they conduct\npower and cut off redstone wire.", false);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				FULL_FACES_ARE_SOLID
		);
	}
	
	public static class StickyPiston {
		
		public static final String ID = "stickyPiston";
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting(ID, "connectsToWire", "When enabled, sticky pistons connect to redstone wire.", false);
		public static final BooleanSetting DO_BLOCK_DROPPING = new BooleanSetting(ID, "doBlockDropping", "When enabled, sticky pistons drop their block when given a short pulse (less than or equal to their speed).", true);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final BooleanSetting FAST_BLOCK_DROPPING = new BooleanSetting(ID, "fastBlockDropping", "When enabled and doBlockDropping is also enabled, a sticky piston will instantly place the block it is dropping.", true);
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting(ID, "supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face without\nbreaking when the piston extends or retracts.", false);
		public static final BooleanSetting UPDATE_SELF_WHILE_POWERED = new BooleanSetting(ID, "updateSelfWhilePowered", "If the piston is powered but cannot extend, it will update itself each tick until it can extend.\nThis is achieved using scheduled ticks.", false);
		public static final BooleanSetting SUPPRESS_HEAD_UPDATES_ON_EXTENSION = new BooleanSetting(ID, "suppressHeadUpdatesOnExtension", "Suppress block updates emitted by the moving piston head when the piston starts extending.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_EXTENDING = new BooleanSetting(ID, "ignoreUpdatesWhileExtending", "Ignore any neighbor updates received during the extension.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHILE_RETRACTING = new BooleanSetting(ID, "ignoreUpdatesWhileRetracting", "Ignore any neighbor updates received during the retraction.", true);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting(ID, "lazyRisingEdge", Common.DESC_LAZY_RISING_EDGE, false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting(ID, "lazyFallingEdge", Common.DESC_LAZY_FALLING_EDGE, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, true, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting(ID, "speedRisingEdge", "The duration of the extension in ticks.", 2, 0, Common.MAX_DELAY);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting(ID, "speedFallingEdge", "The duration of the retraction in ticks.", 2, 0, Common.MAX_DELAY);
		public static final BooleanSetting SUPER_STICKY = new BooleanSetting(ID, "superSticky", "Make the face of sticky pistons stick to blocks when moved.", false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting(ID, "pushLimit", "The maximum number of blocks a piston can move.", 12, 0, 2048);
		public static final BooleanSetting MERGE_SLABS = new BooleanSetting(ID, "mergeSlabs", "?? FILL ME IN ??", false);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				CONNECTS_TO_WIRE,
				DO_BLOCK_DROPPING,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				FAST_BLOCK_DROPPING,
				SUPPORTS_BRITTLE_BLOCKS,
				UPDATE_SELF_WHILE_POWERED,
				SUPPRESS_HEAD_UPDATES_ON_EXTENSION,
				IGNORE_UPDATES_WHILE_EXTENDING,
				IGNORE_UPDATES_WHILE_RETRACTING,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				QC,
				RANDOMIZE_QC,
				SPEED_RISING_EDGE,
				SPEED_FALLING_EDGE,
				SUPER_STICKY,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				PUSH_LIMIT,
				MERGE_SLABS
		);
	}
	
	public static class StoneButton {
		
		public static final String ID = "stoneButton";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles.", new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 20, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class StonePressurePlate {
		
		public static final String ID = "stonePressurePlate";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers or depowers.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 20, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class SugarCane {
		
		public static final String ID = "sugarCane";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_BREAKING, 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class TargetBlock {
		
		public static final String ID = "targetBlock";
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting(ID, "delayDefault", "The default delay in ticks before powering off.", 8, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_PERSISTENT_PROJECTILE = new IntegerSetting(ID, "delayPersistentProjectile", "The delay in ticks before powering off when hit by a persistent projectile, like an arrow or a trident.", 20, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_DEFAULT,
				DELAY_PERSISTENT_PROJECTILE,
				TICK_PRIORITY
		);
	}
	
	public static class TNT {
		
		public static final String ID = "tnt";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_ACTIVATING, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting FUSE_TIME = new IntegerSetting(ID, "fuseTime", "The delay in ticks before a TNT entity explodes.", 80, 0, Common.MAX_DELAY);
		public static final BooleanSetting LAZY = new BooleanSetting(ID, "lazy", Common.DESC_LAZY, false);
		public static final DirectionalBooleanSetting QC = new DirectionalBooleanSetting(ID, "quasiConnectivity", Common.DESC_QC, new Boolean[] {false, false, false, false, false, false});
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting(ID, "randomizeQuasiConnectivity", Common.DESC_RANDOMIZE_QC, false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				FUSE_TIME,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Tripwire {
		
		public static final String ID = "tripwire";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before attempting to power off.", 10, 1, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class TripwireHook {
		
		public static final String ID = "tripwireHook";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighboring blocks are updated when a tripwire hook powers on or off.", new UpdateOrder(Directionality.HORIZONTAL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before attempting to power off.", 10, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY
		);
	}
	
	public static class Vines {
		
		public static final String ID = "vines";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", Common.DESC_DELAY_BREAKING, 1, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Water {
		
		public static final String ID = "water";
		
		public static final IntegerSetting DELAY = new IntegerSetting(ID, "delay", "Delay in ticks before attempting to flow.", 5, 0, Common.MAX_DELAY);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting(ID, "tickPriority", Common.DESC_TICK_PRIORITY, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class WoodenButton {
		
		public static final String ID = "woodenButton";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighboring blocks are updated when a button toggles..", new UpdateOrder(Directionality.ALL, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.FRONT, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 30, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class WoodenPressurePlate {
		
		public static final String ID = "woodenPressurePlate";
		
		public static final UpdateOrderSetting BLOCK_UPDATE_ORDER = new UpdateOrderSetting(ID, "blockUpdateOrder", "The order in which neighboring blocks are updated when a pressure plate powers on or off.", new UpdateOrder(Directionality.NONE, UpdateOrder.NotifierOrder.NORMAL).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.SELF, RelativePos.WEST).
				add(BlockUpdate.Mode.NEIGHBORS, RelativePos.DOWN, RelativePos.WEST));
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting(ID, "delayRisingEdge", Common.DESC_DELAY_RISING_EDGE, 0, 0, Common.MAX_DELAY);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting(ID, "delayFallingEdge", Common.DESC_DELAY_FALLING_EDGE, 20, 1, Common.MAX_DELAY);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting(ID, "weakPower", Common.DESC_POWER_WEAK, 15, 0, Common.MAX_POWER);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting(ID, "strongPower", Common.DESC_POWER_STRONG, 15, 0, Common.MAX_POWER);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting(ID, "tickPriorityRisingEdge", Common.DESC_TICK_PRIORITY_RISING_EDGE, TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting(ID, "tickPriorityFallingEdge", Common.DESC_TICK_PRIORITY_FALLING_EDGE, TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				BLOCK_UPDATE_ORDER,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static final List<SettingsPack> SETTINGS_PACKS = Arrays.asList(
			new SettingsPack("Global", Global.SETTINGS),
			new SettingsPack("Bug Fixes", BugFixes.SETTINGS),
			
			new SettingsPack("Activator Rail", ActivatorRail.SETTINGS),
			new SettingsPack("Bamboo", Bamboo.SETTINGS),
			new SettingsPack("Barrier", Barrier.SETTINGS),
			new SettingsPack("Bubble Column", BubbleColumn.SETTINGS),
			new SettingsPack("Cactus", Cactus.SETTINGS),
			new SettingsPack("Chorus Plant", ChorusPlant.SETTINGS),
			new SettingsPack("Command Block", CommandBlock.SETTINGS),
			new SettingsPack("Comparator", Comparator.SETTINGS),
			new SettingsPack("Composter", Composter.SETTINGS),
			new SettingsPack("Coral", Coral.SETTINGS),
			new SettingsPack("Coral Block", CoralBlock.SETTINGS),
			new SettingsPack("Detector Rail", DetectorRail.SETTINGS),
			new SettingsPack("Dispenser", Dispenser.SETTINGS),
			new SettingsPack("Dragon Egg", DragonEgg.SETTINGS),
			new SettingsPack("Dropper", Dropper.SETTINGS),
			new SettingsPack("Farmland", Farmland.SETTINGS),
			new SettingsPack("Fire", Fire.SETTINGS),
			new SettingsPack("Frosted Ice", FrostedIce.SETTINGS),
			new SettingsPack("Grass Path", GrassPath.SETTINGS),
			new SettingsPack("Gravity Block", GravityBlock.SETTINGS),
			new SettingsPack("Heavy Weighted Pressure Plate", HeavyWeightedPressurePlate.SETTINGS),
			new SettingsPack("Hopper", Hopper.SETTINGS),
			new SettingsPack("Lava", Lava.SETTINGS),
			new SettingsPack("Leaves", Leaves.SETTINGS),
			new SettingsPack("Lectern", Lectern.SETTINGS),
			new SettingsPack("Lever", Lever.SETTINGS),
			new SettingsPack("Light Weighted Pressure Plate", LightWeightedPressurePlate.SETTINGS),
			new SettingsPack("Magenta Glazed Terracotta", MagentaGlazedTerracotta.SETTINGS),
			new SettingsPack("Magma Block", MagmaBlock.SETTINGS),
			new SettingsPack("Normal Piston", NormalPiston.SETTINGS),
			new SettingsPack("Note Block", NoteBlock.SETTINGS),
			new SettingsPack("Observer", Observer.SETTINGS),
			new SettingsPack("Powered Rail", PoweredRail.SETTINGS),
			new SettingsPack("Redstone Block", RedstoneBlock.SETTINGS),
			new SettingsPack("Redstone Lamp", RedstoneLamp.SETTINGS),
			new SettingsPack("Redstone Ore", RedstoneOre.SETTINGS),
			new SettingsPack("Redstone Torch", RedstoneTorch.SETTINGS),
			new SettingsPack("Redstone Wire", RedstoneWire.SETTINGS),
			new SettingsPack("Repeater", Repeater.SETTINGS),
			new SettingsPack("Scaffolding", Scaffolding.SETTINGS),
			new SettingsPack("Soul Sand", SoulSand.SETTINGS),
			new SettingsPack("Stairs", Stairs.SETTINGS),
			new SettingsPack("Sticky Piston", StickyPiston.SETTINGS),
			new SettingsPack("Stone Button", StoneButton.SETTINGS),
			new SettingsPack("Stone Pressure Plate", StonePressurePlate.SETTINGS),
			new SettingsPack("Sugar Cane", SugarCane.SETTINGS),
			new SettingsPack("Target Block", TargetBlock.SETTINGS),
			new SettingsPack("TNT", TNT.SETTINGS),
			new SettingsPack("Tripwire", Tripwire.SETTINGS),
			new SettingsPack("Tripwire Hook", TripwireHook.SETTINGS),
			new SettingsPack("Vines", Vines.SETTINGS),
			new SettingsPack("Water", Water.SETTINGS),
			new SettingsPack("Wooden Button", WoodenButton.SETTINGS),
			new SettingsPack("Wooden Pressure Plate", WoodenPressurePlate.SETTINGS)
	);
	
	public static int settingCount;
	
	public static ISetting getSettingFromId(String id) {
		for (SettingsPack pack : SETTINGS_PACKS) {
			for (ISetting setting : pack.getSettings()) {
				if (setting.getId().equals(id)) {
					return setting;
				}
			}
		}
		
		return null;
	}
	
	public static void reset() {
		for (SettingsPack pack : SETTINGS_PACKS) {
			for (ISetting setting : pack.getSettings()) {
				setting.reset();
			}
		}
	}
	
	static {
		
		SETTINGS_PACKS.forEach((pack) -> settingCount += pack.getSettings().size());
		
	}
}
