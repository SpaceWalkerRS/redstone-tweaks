package redstonetweaks.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface RTIRedstoneTorch {
	
	public Direction getFacing(BlockState state);
	
	public int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong);
	
	public int getPowerOutput(BlockView world, BlockPos pos, BlockState state, boolean strong);
	
}
