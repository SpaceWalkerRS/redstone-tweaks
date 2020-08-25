package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockEntityHelper {
	
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type);
	
}
