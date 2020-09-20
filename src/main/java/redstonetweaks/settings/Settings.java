package redstonetweaks.settings;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;

public class Settings {
	
	public static class Global {
		
		public static final IntegerSetting DELAY_MULTIPLIER = new IntegerSetting("delayMultiplier", "The delay of all scheduled ticks will be multiplied by this value. When set to 0 all scheduled ticks will be executed instantaneously.", 1, 0, 127);
		public static final BooleanSetting DO_BLOCK_UPDATES = new BooleanSetting("doBlockUpdates", "Allow worlds to dispatch block updates.", true);
		public static final BooleanSetting DO_SHAPE_UPDATES = new BooleanSetting("doShapeUpdates", "Allow worlds to dispatch shape updates.", true);
		public static final BooleanSetting DO_COMPARATOR_UPDATES = new BooleanSetting("doComparatorUpdates", "Allow worlds to dispatch comparator updates.", true);
		public static final BooleanSetting DOUBLE_RETRACTION = new BooleanSetting("doubleRetraction", "A re-implementation of behavior that was present in 1.3-1.8, known as \"Jeb retraction\" or \"instant double retraction\". It creates a very narrow window where unpowered pistons can be moved.", true);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MULTIPLIER, 
				DO_BLOCK_UPDATES,
				DO_SHAPE_UPDATES,
				DO_COMPARATOR_UPDATES,
				DOUBLE_RETRACTION
		);
	}
	
	public static class BugFixes {
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				
		);
	}
	
	public static class ActivatorRail {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Rising edge delay.", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Falling edge delay.", 0, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "When enabled, the block is \"lazy\" on  the rising edge. Whenever it is ticked, if it is unpowered, it will power on without checking for received power.", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "When enabled, the block is \\\"lazy\\\" on  the falling edge. Whenever it is ticked, if it is powered, it will power off without checking for received power.", false);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting("powerLimit", "The maximum distance power can flow through rails.", 9, 1, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "Rising edge tick priority.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "Falling edge tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_LIMIT,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Bamboo {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay.", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "Tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class BubbleColumn {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "Delay.", 5, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "Tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Cactus {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class CommandBlock {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 1, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Comparator {
		
		public static final BooleanSetting ADDITION_MODE = new BooleanSetting("additionMode", "When enabled, the comparator's subtract mode turns into \"addition mode\". The output will be the sum of the back input and the highest side input.", false);
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 2, 0, 127);
		public static final BooleanSetting REDSTONE_BLOCKS_VALID_SIDE_INPUT = new BooleanSetting("redstoneBlocksValidSideInput", "Count redstone blocks as valid side inputs.", true);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting("tickPriorityFacingDiode", "Tick priority if the block in front is another diode (repeater/comparator) that is not facing it.", TickPriority.HIGH);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				ADDITION_MODE,
				DELAY,
				REDSTONE_BLOCKS_VALID_SIDE_INPUT,
				TICK_PRIORITY,
				TICK_PRIORITY_FACING_DIODE
		);
	}
	
	public static class Composter {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 20, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Coral {
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay.", 60, 0, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay.", 100, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class CoralBlock {
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay.", 60, 0, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay.", 100, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class DetectorRail {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 20, 1, 127);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "Weak power output.", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "Strong power output.", 15, 0, 15);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY
		);
	}
	
	public static class Dispenser {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 4, 0, 127);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", "", true);
		public static final DirectionListSetting QC = new DirectionListSetting("quasiConnectivity", "A list of all directions in which quasi connectivity for this block is enabled. If quasi connectivity is enabled in a direction then the block checks for power to its direct neighbor in that direction.", Arrays.asList(Direction.DOWN));
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", "When enabled, quasi connectivity works randomly in all directions where it is enabled.", false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Dropper {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 4, 0, 127);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", "", true);
		public static final DirectionListSetting QC = new DirectionListSetting("quasiConnectivity", "A list of all directions in which quasi connectivity for this block is enabled. If quasi connectivity is enabled in a direction then the block checks for power to its direct neighbor in that direction.", Arrays.asList(Direction.DOWN));
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", "When enabled, quasi connectivity works randomly in all directions where it is enabled.", false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				QC,
				RANDOMIZE_QC,
				TICK_PRIORITY
		);
	}
	
	public static class Farmland {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Fire {
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay.", 10, 1, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay.", 30, 1, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class FrostedIce {
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "Minimum delay.", 20, 0, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "Maximum delay.", 40, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class GrassPath {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class GravityBlock {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 2, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class HeavyWeightedPressurePlate {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 10, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		public static final IntegerSetting WEIGHT = new IntegerSetting("weight", "", 150, 1, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				WEIGHT
		);
	}
	
	public static class Hopper {
		
		public static final IntegerSetting COOLDOWN = new IntegerSetting("cooldown", "", 8, 0, 127);
		public static final IntegerSetting PRIORITY_COOLDOWN = new IntegerSetting("priorityCooldown", "", 7, 0, 127);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 0, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "", false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				COOLDOWN,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class Lava {
		
		public static final IntegerSetting DELAY_DEFAULT = new IntegerSetting("delayDefault", "Delay in non-nether dimensions.", 30, 0, 127);
		public static final IntegerSetting DELAY_NETHER = new IntegerSetting("delayNether", "Delay in the nether dimension.", 10, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_DEFAULT,
				DELAY_NETHER,
				TICK_PRIORITY
		);
	}
	
	public static class Leaves {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class Lectern {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 2, 0, 127);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 15, 0, 15);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		
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
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 0, 0, 127);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 15, 0, 15);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class LightWeightedPressurePlate {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 10, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		public static final IntegerSetting WEIGHT = new IntegerSetting("weight", "", 15, 1, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				WEIGHT
		);
	}
	
	public static class MagmaBlock {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 20, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class NormalPiston {
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "", false);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 0, 0, 127);
		public static final BooleanSetting SUPPORTS_BRITTLE_BLOCKS = new BooleanSetting("supportsBrittleBlocks", "Allow brittle blocks, like torches, pressure plates and doors, to be placed on any face.", false);
		public static final BooleanSetting UPDATE_SELF_WHEN_POWERED = new BooleanSetting("updateSelfWhenPowered", "If the piston is powered but cannot extend, it will update itself each tick until it can extend. This is achieved using scheduled ticks.", false);
		public static final BooleanSetting SUPPRESS_HEAD_UPDATES_ON_EXTENSION = new BooleanSetting("suppressHeadUpdatesOnExtension", "Suppress block updates emitted by the moving piston head when the piston starts extending.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHEN_EXTENDING = new BooleanSetting("ignoreUpdatesWhenExtending", "Ignore any neighbor updates received during the extension.", false);
		public static final BooleanSetting IGNORE_UPDATES_WHEN_RETRACTING = new BooleanSetting("ignoreUpdatesWhenRetracting", "Ignore any neighbor updates received during the retraction.", true);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "", true);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "", true);
		public static final DirectionListSetting QC = new DirectionListSetting("quasiConnectivity", "", Arrays.asList(Direction.DOWN));
		public static final BooleanSetting RANDOMIZE_QC = new BooleanSetting("randomizeQuasiConnectivity", "", false);
		public static final IntegerSetting SPEED_RISING_EDGE = new IntegerSetting("speedRisingEdge", "", 2, 0, 127);
		public static final IntegerSetting SPEED_FALLING_EDGE = new IntegerSetting("speedFallingEdge", "", 2, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		public static final IntegerSetting PUSH_LIMIT = new IntegerSetting("pushLimit", "", 12, 0, 1023);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				CONNECTS_TO_WIRE,
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				SUPPORTS_BRITTLE_BLOCKS,
				UPDATE_SELF_WHEN_POWERED,
				SUPPRESS_HEAD_UPDATES_ON_EXTENSION,
				IGNORE_UPDATES_WHEN_EXTENDING,
				IGNORE_UPDATES_WHEN_RETRACTING,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				QC,
				RANDOMIZE_QC,
				SPEED_RISING_EDGE,
				SPEED_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE,
				PUSH_LIMIT
		);
	}
	
	public static class NoteBlock {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 0, 0, 127);
		public static final BooleanSetting LAZY = new BooleanSetting("lazy", "", false);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				TICK_PRIORITY
		);
	}
	
	public static class Observer {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 2, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 2, 0, 127);
		public static final BooleanSetting DISABLE = new BooleanSetting("disable", "", false);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 15, 0, 15);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				DISABLE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class TallPlants {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class PoweredRail {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Rising edge delay.", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Falling edge delay.", 0, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "When enabled, the block is \"lazy\" on  the rising edge. Whenever it is ticked, if it is unpowered, it will power on without checking for received power.", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "When enabled, the block is \\\"lazy\\\" on  the falling edge. Whenever it is ticked, if it is powered, it will power off without checking for received power.", false);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting("powerLimit", "The maximum distance power can flow through rails.", 9, 1, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "Rising edge tick priority.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "Falling edge tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				POWER_LIMIT,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class RedstoneBlock {
		
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 0, 0, 15);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				POWER_WEAK,
				POWER_STRONG
		);
	}
	
	public static class RedstoneLamp {
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Rising edge delay.", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Falling edge delay.", 4, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "When enabled, the block is \"lazy\" on  the rising edge. Whenever it is ticked, if it is unpowered, it will power on without checking for received power.", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "When enabled, the block is \\\"lazy\\\" on  the falling edge. Whenever it is ticked, if it is powered, it will power off without checking for received power.", false);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "Rising edge tick priority.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "Falling edge tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_RISING_EDGE,
				DELAY_FALLING_EDGE,
				LAZY_RISING_EDGE,
				LAZY_FALLING_EDGE,
				TICK_PRIORITY_RISING_EDGE,
				TICK_PRIORITY_FALLING_EDGE
		);
	}
	
	public static class RedstoneOre {
		
		public static final BooleanSetting CONNECTS_TO_WIRE = new BooleanSetting("connectsToWire", "", false);
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 0, 0, 127);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 0, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 0, 0, 15);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				CONNECTS_TO_WIRE,
				POWER_WEAK,
				POWER_STRONG,
				TICK_PRIORITY
		);
	}
	
	public static class RedstoneTorch {
		
		public static final IntegerSetting BURNOUT_COUNT = new IntegerSetting("burnoutCount", "", 8, 0, 127);
		public static final IntegerSetting BURNOUT_TIMER = new IntegerSetting("burnoutTimer", "", 60, 0, 127);
		public static final IntegerSetting DELAY_BURNOUT = new IntegerSetting("delayBurnout", "", 160, 0, 1023);
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "Rising edge delay.", 2, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "Falling edge delay.", 2, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "When enabled, the block is \"lazy\" on  the rising edge. Whenever it is ticked, if it is unpowered, it will power on without checking for received power.", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "When enabled, the block is \\\"lazy\\\" on  the falling edge. Whenever it is ticked, if it is powered, it will power off without checking for received power.", false);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 15, 0, 15);
		public static final BooleanSetting SOFT_INVERSION = new BooleanSetting("softInversion", "", false);
		public static final TickPrioritySetting TICK_PRIORITY_BURNOUT = new TickPrioritySetting("tickPriorityBurnout", "Rising edge tick priority.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "Rising edge tick priority.", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "Falling edge tick priority.", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
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
	
	public static final List<SettingsPack> SETTINGS_PACKS = Arrays.asList(
			new SettingsPack("global", Global.SETTINGS),
			new SettingsPack("bugFixes", BugFixes.SETTINGS),
			
			new SettingsPack("activatorRail", ActivatorRail.SETTINGS),
			new SettingsPack("bamboo", Bamboo.SETTINGS),
			new SettingsPack("bubbleColumn", BubbleColumn.SETTINGS),
			new SettingsPack("cactus", Cactus.SETTINGS),
			new SettingsPack("commandBlock", CommandBlock.SETTINGS),
			new SettingsPack("comparator", Comparator.SETTINGS),
			new SettingsPack("composter", Composter.SETTINGS),
			new SettingsPack("coral", Coral.SETTINGS),
			new SettingsPack("coralBlock", CoralBlock.SETTINGS),
			new SettingsPack("detectorRail", DetectorRail.SETTINGS),
			new SettingsPack("dispenser", Dispenser.SETTINGS),
			new SettingsPack("dropper", Dropper.SETTINGS),
			new SettingsPack("farmland", Farmland.SETTINGS),
			new SettingsPack("fire", Fire.SETTINGS),
			new SettingsPack("frostedIce", FrostedIce.SETTINGS),
			new SettingsPack("grassPath", GrassPath.SETTINGS),
			new SettingsPack("gravityBlock", GravityBlock.SETTINGS),
			new SettingsPack("heavyWeightedPressurePlate", HeavyWeightedPressurePlate.SETTINGS),
			new SettingsPack("hopper", Hopper.SETTINGS),
			new SettingsPack("lava", Lava.SETTINGS),
			new SettingsPack("leaves", Leaves.SETTINGS),
			new SettingsPack("lectern", Lectern.SETTINGS),
			new SettingsPack("lever", Lever.SETTINGS),
			new SettingsPack("lightWeightedPressurePlate", LightWeightedPressurePlate.SETTINGS),
			new SettingsPack("magmaBlock", MagmaBlock.SETTINGS),
			new SettingsPack("normalPiston", NormalPiston.SETTINGS),
			new SettingsPack("noteBlock", NoteBlock.SETTINGS),
			new SettingsPack("observer", Observer.SETTINGS),
			new SettingsPack("tallPlants", TallPlants.SETTINGS),
			new SettingsPack("poweredRail", PoweredRail.SETTINGS),
			new SettingsPack("redstoneBlock", RedstoneBlock.SETTINGS),
			new SettingsPack("redstoneLamp", RedstoneLamp.SETTINGS),
			new SettingsPack("redstoneOre", RedstoneOre.SETTINGS),
			new SettingsPack("redstoneTorch", RedstoneTorch.SETTINGS)
	);
	
	public static SettingsPack getPackFromName(String name) {
		for (SettingsPack pack : SETTINGS_PACKS) {
			if (pack.getName() == name) {
				return pack;
			}
		}
		
		return null;
	}
}
