package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.WorldAccess;

import redstonetweaks.setting.Tweaks;
import redstonetweaks.util.RTMathHelper;

public class TickSchedulerHelper {
	
	private static final int INSTANT_TICK_LIMIT = 512;
	private static int instantTicks;
	
	public static boolean scheduleBlockTick(WorldAccess world, BlockPos pos, BlockState state, int delay, TickPriority priority) {
		if (!world.isClient()) {
			delay = prepareDelay(world, delay);
			
			if (delay == 0 && instantTicks < INSTANT_TICK_LIMIT && world instanceof ServerWorld) {
				state.scheduledTick((ServerWorld)world, pos, world.getRandom());
				
				instantTicks++;
				
				return true;
			} else {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, preparePriority(world, priority));
			}
		}
		
		return false;
	}
	
	public static boolean scheduleFluidTick(WorldAccess world, BlockPos pos, FluidState state, int delay, TickPriority priority) {
		if (!world.isClient()) {
			delay = prepareDelay(world, delay);
			
			if (delay == 0 && instantTicks < INSTANT_TICK_LIMIT && world instanceof ServerWorld) {
				state.onScheduledTick((ServerWorld)world, pos);
				
				instantTicks++;
				
				return true;
			} else {
				world.getFluidTickScheduler().schedule(pos, state.getFluid(), delay, preparePriority(world, priority));
			}
		}
		
		return false;
	}
	
	private static int prepareDelay(WorldAccess world, int delay) {
		if (Tweaks.Global.RANDOMIZE_DELAYS.get()) {
			int min = 1;
			int max = 127;
			
			delay = RTMathHelper.randomInt(world.getRandom(), min, max);
		}
		
		return Tweaks.Global.DELAY_MULTIPLIER.get() * delay;
	}
	
	private static TickPriority preparePriority(WorldAccess world, TickPriority priority) {
		if (Tweaks.Global.RANDOMIZE_TICK_PRIORITIES.get()) {
			int index = world.getRandom().nextInt(TickPriority.values().length) + TickPriority.values()[0].getIndex();
			priority = TickPriority.byIndex(index);
		}
		
		return priority;
	}
	
	public static void tick() {
		instantTicks = 0;
	}
}
