package redstonetweaks.world.common;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public interface IIncompleteActionScheduler {
	
	public boolean hasScheduledActions();
	
	default void scheduleBlockAction(BlockPos pos, int type, Block block) {
		scheduleBlockAction(pos, type, -1, block);
	}
	
	default void scheduleBlockAction(BlockPos pos, int type, double viewDistance, Block block) {
		scheduleAction(new IncompleteBlockAction(pos, type, viewDistance, block));
	}
	
	public void scheduleAction(IIncompleteAction action);
	
}
