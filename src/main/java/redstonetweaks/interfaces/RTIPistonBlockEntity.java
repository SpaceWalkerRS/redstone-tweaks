package redstonetweaks.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface RTIPistonBlockEntity {
	
	public boolean isMovedByStickyPiston();
	
	public void setIsMovedByStickyPiston(boolean newValue);
	
	public BlockState getMovedState();
	
	public void setMovedBlockEntity(BlockEntity pushedBlockEntity);
	
	public BlockEntity getMovedBlockEntity();
	
	public boolean isMergingSlabs();
	
	public void setIsMergingSlabs(boolean isMergingSlabs);
	
	public void finishSource();
	
}