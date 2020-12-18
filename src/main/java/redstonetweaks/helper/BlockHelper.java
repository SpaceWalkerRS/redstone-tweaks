package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.setting.Tweaks;

public class BlockHelper {
	
	public static final Direction[] FACINGS = AbstractBlockHelper.FACINGS;
	
	public static BlockState postProcessState(World world, BlockState state, BlockPos pos) {
		BlockState blockState = state;
		
		for (Direction direction : FACINGS) {
			BlockPos neighborPos = pos.offset(direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(neighborPos), world, pos, neighborPos);
		}
		return blockState;
	}
	
	public static boolean isRigidPistonBase(BlockView world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof PistonBlock) {
			return isPistonRigid(state.isOf(Blocks.STICKY_PISTON));
		}
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
					BlockState piston = pistonBlockEntity.getPushedBlock();
					return isPistonRigid(piston.isOf(Blocks.STICKY_PISTON));
				}
			}
		}
		
		return false;
	}
	
	public static boolean isPistonRigid(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.SUPPORTS_BRITTLE_BLOCKS.get() : Tweaks.NormalPiston.SUPPORTS_BRITTLE_BLOCKS.get();
	}
	
	public static boolean isSplitSlab(BlockView world, BlockPos pos, BlockState state, Direction face) {
		if (state.isOf(Blocks.MOVING_PISTON) && face.getAxis().isVertical()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (!(blockEntity instanceof PistonBlockEntity)) {
				return false;
			}
			
			PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
			
			if (((RTIPistonBlockEntity)pistonBlockEntity).isMergingSlabs()) {
				return true;
			}
			
			BlockEntity movedBlockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedBlockEntity();
			
			if (movedBlockEntity instanceof PistonBlockEntity) {
				pistonBlockEntity = (PistonBlockEntity)movedBlockEntity;
			}
			
			return ((RTIPistonBlockEntity)pistonBlockEntity).isMergingSlabs();
		}
		
		return false;
	}
}
