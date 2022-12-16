package redstone.tweaks.interfaces.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickPriority;

public interface FluidOverrides {

	TickPriority tickPriority();

	public static boolean scheduleOrDoTick(LevelAccessor level, BlockPos pos, FluidState state, int delay, TickPriority priority) {
		if (level instanceof ServerLevel) {
			if (delay > 0) {
				level.scheduleTick(pos, state.getType(), delay, priority);
			} else {
				state.tick((ServerLevel)level, pos);
			}
		}

		return false;
	}
}
