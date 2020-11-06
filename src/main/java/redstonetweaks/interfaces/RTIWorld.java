package redstonetweaks.interfaces;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.world.common.WorldTickHandler;

public interface RTIWorld {
	
	public WorldTickHandler getWorldTickHandler();
	
	public void addMovedBlockEntity(BlockPos pos, BlockEntity blockEntity);
	
	public boolean addBlockEventHandler(BlockEventHandler blockEventHandler);
	
	public void removeBlockEventHandler(BlockPos pos);
	
	public BlockEventHandler getBlockEventHandler(BlockPos pos);
	
	public void startTickingBlockEntities(boolean startIterating);
	
	public boolean tryContinueTickingBlockEntities();
	
	public void finishTickingBlockEntities(Profiler profiler);
	
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler);
	
	public boolean tickWorldsNormally();
	
	public boolean updateNeighborsNormally();
	
}
