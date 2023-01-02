package redstone.tweaks.mixin.common.behavior_overrides;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BehaviorOverrides;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements BehaviorOverrides {

	private Integer delayOverride;
	private Boolean microtickModeOverride;
	private Integer signalOverride;
	private Integer directSignalOverride;
	private TickPriority tickPriorityOverride;

	@Override
	public void setDelayOverride(int delay) {
		delayOverride = delay;
	}

	@Override
	public void setMicrotickModeOverride(boolean microtickMode) {
		microtickModeOverride = microtickMode;
	}

	@Override
	public void setSignalOverride(int signal) {
		signalOverride = signal;
	}

	@Override
	public void setDirectSignalOverride(int signal) {
		directSignalOverride = signal;
	}

	@Override
	public void setTickPriorityOverride(TickPriority priority) {
		tickPriorityOverride = priority;
	}

	@Override
	public int overrideDelay(int delay) {
		return delayOverride != null && Tweaks.BehaviorOverrides.delay() ? delayOverride : delay;
	}

	@Override
	public boolean overrideMicrotickMode(boolean microtickMode) {
		return microtickModeOverride != null && Tweaks.BehaviorOverrides.microtickMode() ? microtickModeOverride : microtickMode;
	}

	@Override
	public int overrideSignal(int signal) {
		return signalOverride != null && Tweaks.BehaviorOverrides.signal() ? signalOverride : signal;
	}

	@Override
	public int overrideDirectSignal(int signal) {
		return directSignalOverride != null && Tweaks.BehaviorOverrides.signalDirect() ? directSignalOverride : signal;
	}

	@Override
	public TickPriority overrideTickPriority(TickPriority priority) {
		return tickPriorityOverride != null && Tweaks.BehaviorOverrides.tickPriority() ? tickPriorityOverride : priority;
	}
}
