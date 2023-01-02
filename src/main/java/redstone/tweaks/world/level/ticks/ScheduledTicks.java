package redstone.tweaks.world.level.ticks;

import java.util.Random;

import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;

public class ScheduledTicks {

	private static final Random RAND = new Random();
	private static final TickPriority[] PRIORITIES = TickPriority.values();

	public static int prepareDelay(int delay) {
		if (Tweaks.Global.randomizeScheduledTickDelays()) {
			int min = 1;
			int max = 127;

			delay = RAND.nextInt(min, max);
		}

		return delay * Tweaks.Global.delayMultiplier();
	}

	public static TickPriority preparePriority(TickPriority priority) {
		if (Tweaks.Global.randomizeScheduledTickPriorities()) {
			priority = PRIORITIES[RAND.nextInt(PRIORITIES.length)];
		}

		return priority;
	}
}
