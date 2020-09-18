package redstonetweaks.settings;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.TickPriority;

public class Settings {
	
	public static class Global {
		
		public static final IntegerSetting DELAY_MULTIPLIER = new IntegerSetting("delayMultiplier", "The delay of all scheduled ticks will be multiplied by this value. When set to 0 all scheduled ticks will be executed instantaneously.", 1, 0, 127);
		public static final BooleanSetting DO_BLOCK_UPDATES = new BooleanSetting("doBlockUpdates", "", true);
		public static final BooleanSetting DO_SHAPE_UPDATES = new BooleanSetting("doShapeUpdates", "", true);
		public static final BooleanSetting DO_COMPARATOR_UPDATES = new BooleanSetting("doComparatorUpdates", "", true);
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
		
		public static final IntegerSetting DELAY_RISING_EDGE = new IntegerSetting("delayRisingEdge", "", 0, 0, 127);
		public static final IntegerSetting DELAY_FALLING_EDGE = new IntegerSetting("delayFallingEdge", "", 0, 0, 127);
		public static final BooleanSetting LAZY_RISING_EDGE = new BooleanSetting("lazyRisingEdge", "", false);
		public static final BooleanSetting LAZY_FALLING_EDGE = new BooleanSetting("lazyFallingEdge", "", false);
		public static final IntegerSetting POWER_LIMIT = new IntegerSetting("powerLimit", "", 8, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY_RISING_EDGE = new TickPrioritySetting("tickPriorityRisingEdge", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FALLING_EDGE = new TickPrioritySetting("tickPriorityFallingEdge", "", TickPriority.NORMAL);
		
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
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 1, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				TICK_PRIORITY
		);
	}
	
	public static class BubbleColumn {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 5, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
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
		
		public static final BooleanSetting ADDITION_MODE = new BooleanSetting("additionMode", "", false);
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 2, 0, 127);
		public static final BooleanSetting REDSTONE_BLOCKS_VALID_SIDE_INPUT = new BooleanSetting("redstoneBlocksValidSideInput", "", true);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		public static final TickPrioritySetting TICK_PRIORITY_FACING_DIODE = new TickPrioritySetting("tickPriorityFacingDiode", "", TickPriority.HIGH);
		
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
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "", 60, 0, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "", 100, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class CoralBlock {
		
		public static final IntegerSetting DELAY_MIN = new IntegerSetting("delayMin", "", 60, 0, 127);
		public static final IntegerSetting DELAY_MAX = new IntegerSetting("delayMax", "", 100, 0, 127);
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY_MIN,
				DELAY_MAX,
				TICK_PRIORITY
		);
	}
	
	public static class DetectorRail {
		
		public static final IntegerSetting DELAY = new IntegerSetting("delay", "", 20, 1, 127);
		public static final IntegerSetting POWER_WEAK = new IntegerSetting("weakPower", "", 15, 0, 15);
		public static final IntegerSetting POWER_STRONG = new IntegerSetting("strongPower", "", 15, 0, 15);
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
		public static final TickPrioritySetting TICK_PRIORITY = new TickPrioritySetting("tickPriority", "", TickPriority.NORMAL);
		
		public static final List<ISetting> SETTINGS = Arrays.asList(
				DELAY,
				LAZY,
				TICK_PRIORITY
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
			new SettingsPack("coralBlock", CoralBlock.SETTINGS)
	);
}
