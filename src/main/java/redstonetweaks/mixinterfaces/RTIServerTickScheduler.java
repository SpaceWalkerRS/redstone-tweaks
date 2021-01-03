package redstonetweaks.mixinterfaces;

import net.minecraft.util.math.BlockPos;

public interface RTIServerTickScheduler {
	
	public boolean hasScheduledTickAtTime(BlockPos pos, Object object, int delay);
	
	public void startTicking();
	
	public boolean tryContinueTicking();
	
}
