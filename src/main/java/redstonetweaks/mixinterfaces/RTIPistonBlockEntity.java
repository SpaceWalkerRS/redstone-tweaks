package redstonetweaks.mixinterfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;

public interface RTIPistonBlockEntity {
	
	public boolean isMovedByStickyPiston();
	
	public void setIsMovedByStickyPiston(boolean newValue);
	
	public BlockState getMovedState();
	
	public void setMovedState(BlockState state);
	
	public void setPushedBlock(BlockState state);
	
	public void setPushedBlockEntity(BlockEntity pushedBlockEntity);
	
	public void setParentPistonBlockEntity(PistonBlockEntity pistonBlockEntity);
	
	public BlockEntity getPushedBlockEntity();
	
	public BlockEntity getMovedBlockEntity();
	
	public void setMovedBlockEntity(BlockEntity blockEntity);
	
	public boolean isMergingSlabs();
	
	public void setIsMergingSlabs(boolean isMergingSlabs);
	
	public boolean sourceIsMoving();
	
	public void setSourceIsMoving(boolean moveSource);
	
	public void setSource(boolean source);
	
	public void finishSource();
	
}
