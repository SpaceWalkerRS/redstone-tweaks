package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

public interface BehaviorOverrides {

	void setDelayOverride(int delay);

	void setMicrotickModeOverride(boolean microtickMode);

	void setSignalOverride(int signal);

	void setDirectSignalOverride(int signal);

	void setTickPriorityOverride(TickPriority priority);

	int overrideDelay(int delay);

	boolean overrideMicrotickMode(boolean microtickMode);

	int overrideSignal(int signal);

	int overrideDirectSignal(int signal);

	TickPriority overrideTickPriority(TickPriority priority);

	public static int overrideDelay(BlockState state, int delay) {
		return ((BehaviorOverrides)state.getBlock()).overrideDelay(delay);
	}

	public static boolean overrideMicrotickMode(BlockState state, boolean microtickMode) {
		return ((BehaviorOverrides)state.getBlock()).overrideMicrotickMode(microtickMode);
	}

	public static int overrideSignal(BlockState state, int signal) {
		return ((BehaviorOverrides)state.getBlock()).overrideSignal(signal);
	}

	public static int overrideDirectSignal(BlockState state, int signal) {
		return ((BehaviorOverrides)state.getBlock()).overrideDirectSignal(signal);
	}

	public static TickPriority overrideTickPriority(BlockState state, TickPriority priority) {
		return ((BehaviorOverrides)state.getBlock()).overrideTickPriority(priority);
	}
}
