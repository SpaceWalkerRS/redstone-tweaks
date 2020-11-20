package redstonetweaks.interfaces;

import java.util.List;
import java.util.Map;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;

public interface RTIPistonHandler {
	
	public void addMovedBlockEntity(BlockEntity blockEntity);
	
	public List<BlockEntity> getMovedBlockEntities();
	
	public Map<BlockPos, SlabType> getMergedSlabTypes();
	
	public Map<BlockPos, SlabType> getSplitSlabTypes();
	
}
