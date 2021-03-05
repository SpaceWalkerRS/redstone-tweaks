package redstonetweaks.interfaces.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public interface RTIPlant {
	
	public boolean hasAttachmentTo(BlockState state, Direction dir, Block neighborBlock);
	
}
