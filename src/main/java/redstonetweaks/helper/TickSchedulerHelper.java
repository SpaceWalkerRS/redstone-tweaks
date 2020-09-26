package redstonetweaks.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import redstonetweaks.settings.Settings;

public class TickSchedulerHelper {
	
	public static <T> void schedule(WorldAccess world, BlockState state, TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay, TickPriority priority) {
		if (delay == 0) {
			if (object instanceof Block && !world.isClient()) {
				state.scheduledTick((ServerWorld)world, pos, world.getRandom());
			} else if (object instanceof Fluid) {
				state.getFluidState().onScheduledTick((World)world, pos);
			}
		} else {
			tickScheduler.schedule(pos, object, delay, priority);
		}
	}
	
	public static <T> void scheduleWater(WorldAccess world, BlockState state, TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay) {
		schedule(world, state, tickScheduler, pos, object, delay, Settings.Water.TICK_PRIORITY.get());
	}
}
