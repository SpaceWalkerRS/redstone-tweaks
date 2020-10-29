package redstonetweaks.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChainHelper {
	
	public static boolean isFullyAnchored(World world, BlockPos pos, Direction.Axis axis, List<BlockPos> anchoredChains) {
		if (anchoredChains.contains(pos)) {
			return true;
		}
		
		List<BlockPos> chain = new ArrayList<>();
		
		for (Direction.AxisDirection side : Direction.AxisDirection.values()) {
			Direction dir = Direction.from(axis, side);
			
			BlockPos sidePos = pos.offset(dir);
			BlockState sideState = world.getBlockState(sidePos);
			
			while(sideState.isOf(Blocks.CHAIN)) {
				if (sideState.get(Properties.AXIS) == axis) {
					chain.add(sidePos);
				} else {
					return false;
				}
				
				sidePos = sidePos.offset(dir);
				sideState = world.getBlockState(sidePos);
			}
			
			if (!Block.sideCoversSmallSquare(world, sidePos, dir.getOpposite())) {
				return false;
			}
		}
		
		anchoredChains.addAll(chain);
		
		return true;
	}
}
