package redstonetweaks.helper;

import net.minecraft.util.math.BlockPos;

public interface ServerTickSchedulerHelper<T> {
	
	public boolean hasScheduledTickAtTime(BlockPos pos, T object, int delay);
	
	public void startTicking();
	
	public boolean tryContinueTicking();
}
