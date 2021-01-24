package redstonetweaks.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RTIRedstoneDiode {
	
	public boolean isChainBugOccurring(World world, BlockPos pos, BlockState state);
	
}
