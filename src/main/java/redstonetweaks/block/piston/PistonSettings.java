package redstonetweaks.block.piston;

import net.minecraft.world.TickPriority;

import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

public class PistonSettings {
	
	public static boolean canMoveSelf(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.CAN_MOVE_SELF.get() : Tweaks.NormalPiston.CAN_MOVE_SELF.get();
	}
	
	public static boolean connectsToWire(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.CONNECTS_TO_WIRE.get() : Tweaks.NormalPiston.CONNECTS_TO_WIRE.get();
	}
	
	public static int delayRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.DELAY_RISING_EDGE.get() : Tweaks.NormalPiston.DELAY_RISING_EDGE.get();
	}
	
	public static int delayFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.DELAY_FALLING_EDGE.get() : Tweaks.NormalPiston.DELAY_FALLING_EDGE.get();
	}
	
	public static boolean doBlockDropping() {
		return Tweaks.StickyPiston.DO_BLOCK_DROPPING.get();
	}
	
	public static boolean fastBlockDropping() {
		return doBlockDropping() && Tweaks.StickyPiston.FAST_BLOCK_DROPPING.get();
	}
	
	public static boolean superBlockDropping() {
		return fastBlockDropping() && Tweaks.StickyPiston.SUPER_BLOCK_DROPPING.get();
	}
	
	public static boolean headUpdatesOnExtension(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.HEAD_UPDATES_ON_EXTENSION.get() : Tweaks.NormalPiston.HEAD_UPDATES_ON_EXTENSION.get();
	}
	
	public static boolean headUpdatesWhenPulling() {
		return Tweaks.StickyPiston.HEAD_UPDATES_WHEN_PULLING.get();
	}
	
	public static boolean ignoreUpdatesWhileExtending(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.get() : Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.get();
	}
	
	public static boolean ignoreUpdatesWhileRetracting(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.get() : Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.get();
	}
	
	public static boolean lazyRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.LAZY_RISING_EDGE.get() : Tweaks.NormalPiston.LAZY_RISING_EDGE.get();
	}
	
	public static boolean lazyFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.LAZY_FALLING_EDGE.get() : Tweaks.NormalPiston.LAZY_FALLING_EDGE.get();
	}
	
	public static boolean looseHead(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.LOOSE_HEAD.get() : Tweaks.NormalPiston.LOOSE_HEAD.get();
	}
	
	public static boolean movableWhenExtended(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.get() : Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.get();
	}
	
	public static int pushLimit(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.PUSH_LIMIT.get() : Tweaks.NormalPiston.PUSH_LIMIT.get();
	}
	
	public static DirectionToBooleanSetting getQC(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.QC : Tweaks.NormalPiston.QC;
	}
	
	public static boolean randQC(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.RANDOMIZE_QC.get() : Tweaks.NormalPiston.RANDOMIZE_QC.get();
	}
	
	public static int speedRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.SPEED_RISING_EDGE.get() : Tweaks.NormalPiston.SPEED_RISING_EDGE.get();
	}
	
	public static int speedFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.SPEED_FALLING_EDGE.get() : Tweaks.NormalPiston.SPEED_FALLING_EDGE.get();
	}
	
	public static TickPriority tickPriorityRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.TICK_PRIORITY_RISING_EDGE.get() : Tweaks.NormalPiston.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	public static TickPriority tickPriorityFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.NormalPiston.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	public static boolean updateSelf(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.UPDATE_SELF.get() : Tweaks.NormalPiston.UPDATE_SELF.get();
	}
}
