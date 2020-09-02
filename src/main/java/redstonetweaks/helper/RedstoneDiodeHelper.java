package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RedstoneDiodeHelper {
	
	public boolean isInputBugOccurring(World world, BlockPos pos, BlockState state);
	
}
