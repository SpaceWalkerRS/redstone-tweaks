package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;

public class BlockHelper {
	
	public static final Direction[] FACINGS = AbstractBlockHelper.FACINGS;
	
	public static boolean isSideSolid(BlockView world, BlockPos pos, Direction face, BlockState state, SideShapeType shapeType) {
		return isSolidPiston(world, pos, state, face) || isMergingStationarySlab(world, pos, state, face, shapeType);
	}
	
	public static boolean isSolidPiston(BlockView world, BlockPos pos, BlockState state, Direction face) {
		if (PistonHelper.isPiston(state)) {
			return PistonSettings.supportsBrittleBlocks(PistonHelper.isSticky(state));
		}
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
					if (((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving()) {
						// The side where the piston head is will be solid since the head is not moving
						return face == pistonBlockEntity.getFacing();
					}
					
					return PistonSettings.supportsBrittleBlocks(((RTIPistonBlockEntity)pistonBlockEntity).isSticky());
				}
			}
		}
		
		return false;
	}
	
	public static boolean isMergingStationarySlab(BlockView world, BlockPos pos, BlockState state, Direction face, SideShapeType shapeType) {
		if (state.isOf(Blocks.MOVING_PISTON) && face.getAxis().isVertical()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				return ((RTIPistonBlockEntity)blockEntity).isSideSolid(world, pos, face, shapeType);
			}
		}
		
		return false;
	}
}
