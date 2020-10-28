package redstonetweaks.interfaces;

import net.minecraft.block.entity.BlockEntity;

public interface RTIPistonBlockEntity {
	
	public boolean isMovedByStickyPiston();
	
	public void setIsMovedByStickyPiston(boolean newValue);
	
	public void setPushedBlockEntity(BlockEntity pushedBlockEntity);
	
	public BlockEntity getPushedBlockEntity();
	
}
