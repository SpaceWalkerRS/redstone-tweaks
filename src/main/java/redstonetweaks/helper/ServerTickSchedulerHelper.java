package redstonetweaks.helper;

import net.minecraft.util.math.BlockPos;

public interface ServerTickSchedulerHelper {
	
	public boolean hasScheduledTickAtTime(BlockPos pos, Object object, int delay);
	
	public void startTicking();
	
	public boolean tryContinueTicking();
	
}
