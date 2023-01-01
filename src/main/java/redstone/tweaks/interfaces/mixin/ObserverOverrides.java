package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;

public interface ObserverOverrides extends BlockOverrides {

	public static void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state, boolean powered) {
		int delay = powered ? Tweaks.Observer.delayFallingEdge() : Tweaks.Observer.delayRisingEdge();
		TickPriority priority = powered ? Tweaks.Observer.tickPriorityFallingEdge() : Tweaks.Observer.tickPriorityRisingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, Tweaks.Observer::microtickMode);
	}
}
