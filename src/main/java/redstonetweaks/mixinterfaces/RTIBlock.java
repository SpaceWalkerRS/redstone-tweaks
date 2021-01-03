package redstonetweaks.mixinterfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RTIBlock {
	
	// Return true if the action should be synced with the client
	public boolean continueAction(World world, BlockPos pos, int type);
	
}
