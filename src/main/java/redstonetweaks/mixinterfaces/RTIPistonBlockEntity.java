package redstonetweaks.mixinterfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;

public interface RTIPistonBlockEntity {
	
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
	
	public void setParentPistonBlockEntity(PistonBlockEntity pistonBlockEntity);
	
	public BlockState getMovedMovingState();
	
	public void setMovedMovingState(BlockState state);
	
	public BlockEntity getMovedMovingBlockEntity();
	
	public void setMovedMovingBlockEntity(BlockEntity blockEntity);
	
	public BlockState getStateToMove();
	
	public PistonBlockEntity copy();
	
	public void splitDoubleSlab(SlabType keepType);
	
}
