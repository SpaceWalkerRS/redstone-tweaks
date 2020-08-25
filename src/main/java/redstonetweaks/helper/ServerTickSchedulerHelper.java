package redstonetweaks.helper;

import net.minecraft.util.math.BlockPos;

public interface ServerTickSchedulerHelper {
	
	public <T> boolean isScheduledAtTime(BlockPos pos, T object, int delay);
	
	public void startTicking();
	
	public boolean tryContinueTicking();
}
