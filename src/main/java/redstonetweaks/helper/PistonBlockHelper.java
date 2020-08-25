package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// This class declares methods for the piston mixin classes
// that also need to be accessible to other classes.
public class PistonBlockHelper {
	
	// When the doubleRetraction setting is enabled this method is called
	// from onSyncedBlockEvent to get the state of the block in front of the piston head
	// To make doubleRetraction act like it did in 1.8, we need to update this block,
	// but to prevent changing other behavior we only update the block if it is an extended piston.
	public static BlockState getDoubleRetractionState(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (state.getBlock() instanceof PistonBlock) {
			if (state.get(Properties.EXTENDED)) {
				world.updateNeighbor(pos, state.getBlock(), pos);
				
				state = world.getBlockState(pos);
			}
			
			// We need to send a block change packet regardless of if the piston is extended at this point
			// It may have depowered earlier in the same tick
			BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
			((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
		}
		
		return state;
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing) {
		return isReceivingPower(world, pos, state, facing, false);
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing, boolean onBlockEvent) {
		for (Direction direction : Direction.values()) {
			if (direction != facing && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
			return true;
		}
		return WorldHelper.isQCPowered(world, pos, state, onBlockEvent);
	}
}