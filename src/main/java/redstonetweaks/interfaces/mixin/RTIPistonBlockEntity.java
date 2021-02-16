package redstonetweaks.interfaces.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import redstonetweaks.block.piston.MovedBlock;

public interface RTIPistonBlockEntity {
	
	public void init();
	
	public void setSource(boolean source);
	
	public void finishSource();
	
	public boolean isSticky();
	
	public void setSticky(boolean newValue);
	
	public boolean sourceIsMoving();
	
	public void setSourceIsMoving(boolean moveSource);

	public boolean isMerging();
	
	public void setIsMerging(boolean isMerging);
	
	public void setMovedState(BlockState state);
	
	public BlockEntity getMovedBlockEntity();
	
	public void setMovedBlockEntity(BlockEntity pushedBlockEntity);
	
	public BlockState getMergingState();
	
	public void setMergingState(BlockState state);
	
	public BlockEntity getMergingBlockEntity();
	
	public void setMergingBlockEntity(BlockEntity blockEntity);
	
	public PistonBlockEntity getParent();
	
	public void setParentPistonBlockEntity(PistonBlockEntity pistonBlockEntity);
	
	public BlockState getMovedMovingState();
	
	public void setMovedMovingState(BlockState state);
	
	public BlockEntity getMovedMovingBlockEntity();
	
	public void setMovedMovingBlockEntity(BlockEntity blockEntity);
	
	public BlockState getStateForMovement();
	
	public PistonBlockEntity copy();
	
	public MovedBlock splitDoubleSlab(SlabType keepType);
	
	public MovedBlock detachPistonHead(Direction motionDir, boolean returnMovingPart);
	
	public boolean isSideSolid(BlockView world, BlockPos pos, Direction face, SideShapeType shapeType);
	
}
