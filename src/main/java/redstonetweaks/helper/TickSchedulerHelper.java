package redstonetweaks.helper;

import static redstonetweaks.setting.SettingsManager.*;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TickSchedulerHelper {
	
	public static <T> void schedule(WorldAccess world, BlockState state, TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay) {
		if (object == Fluids.WATER) {
			if (delay == 0) {
				state.getFluidState().onScheduledTick((World)world, pos);
			} else {
				tickScheduler.schedule(pos, object, delay, WATER.get(TICK_PRIORITY));
			}
		}
	}
}
