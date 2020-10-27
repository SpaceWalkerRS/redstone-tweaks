package redstonetweaks.helper;

import net.minecraft.block.entity.BlockEntity;

public interface PistonBlockEntityHelper {
	
	public boolean isMovedByStickyPiston();
	
	public void setIsMovedByStickyPiston(boolean newValue);
	
	public void setPushedBlockEntity(BlockEntity pushedBlockEntity);
	
	public BlockEntity getPushedBlockEntity();
	
}
