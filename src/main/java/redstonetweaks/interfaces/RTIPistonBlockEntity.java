package redstonetweaks.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

public interface RTIPistonBlockEntity {
	
	public boolean isMovedByStickyPiston();
	
	public void setIsMovedByStickyPiston(boolean newValue);
	
	public void setMovedBlockEntity(BlockEntity pushedBlockEntity);
	
	public BlockEntity getMovedBlockEntity();
	
	public BlockState getStationaryState();
	
	public void setStationaryState(BlockState state);
	
}
