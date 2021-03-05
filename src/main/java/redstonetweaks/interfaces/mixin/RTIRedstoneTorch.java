package redstonetweaks.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface RTIRedstoneTorch {
	
	public BlockPos getAttachedToPos(World world, BlockPos pos, BlockState state);
	
}
