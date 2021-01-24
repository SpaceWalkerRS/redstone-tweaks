package redstonetweaks.interfaces.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.util.RelativePos;
import redstonetweaks.world.common.BlockUpdate;
import redstonetweaks.world.common.ComparatorUpdate;
import redstonetweaks.world.common.NeighborUpdate;
import redstonetweaks.world.common.ShapeUpdate;
import redstonetweaks.world.common.UpdateOrder;
import redstonetweaks.world.common.WorldTickHandler;

public interface RTIWorld {
	
	public WorldTickHandler getWorldTickHandler();
	
	public BlockEventHandler getBlockEventHandler(BlockPos pos);
	
	public boolean hasBlockEventHandler(BlockPos pos);
	
	public boolean addBlockEventHandler(BlockEventHandler blockEventHandler);
	
	public void removeBlockEventHandler(BlockPos pos);
	
	public boolean isTickingBlockEntities();
	
	public BlockEntity fetchQueuedBlockEntity(BlockPos pos);
	
	public void queueBlockEntityPlacement(BlockPos pos, BlockEntity blockEntity);
	
	public void startTickingBlockEntities(boolean startIterating);
	
	public boolean tryContinueTickingBlockEntities();
	
	public void finishTickingBlockEntities(Profiler profiler);
	
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler);
	
	public boolean normalWorldTicks();
	
	public boolean immediateNeighborUpdates();
	
	default void dispatchNeighborUpdate(boolean scheduled, NeighborUpdate neighborUpdate) {
		switch (neighborUpdate.getType()) {
		case BLOCK_UPDATE:
			dispatchBlockUpdate(true, (BlockUpdate)neighborUpdate);
			break;
		case COMPARATOR_UPDATE:
			dispatchComparatorUpdate(true, (ComparatorUpdate)neighborUpdate);
			break;
		case SHAPE_UPDATE:
			dispatchShapeUpdate(true, (ShapeUpdate)neighborUpdate);
			break;
		default:
			break;
		}
	}
	
	public void dispatchBlockUpdates(BlockPos sourcePos, Direction sourceFacing, Block sourceBlock, UpdateOrder updateOrder);
	
	public void dispatchBlockUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block sourceBlock);
	
	public void dispatchBlockUpdatesAroundExcept(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block sourceBlock, RelativePos except);
	
	public void dispatchBlockUpdate(boolean scheduled, BlockUpdate blockUpdate);
	
	public void dispatchComparatorUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, Direction sourceFacing, Block block);
	
	public void dispatchComparatorUpdate(boolean scheduled, ComparatorUpdate comparatorUpdate);
	
	public void dispatchShapeUpdatesAround(BlockPos notifierPos, BlockPos sourcePos, BlockState notifierState, int flags, int depth);
	
	public void dispatchShapeUpdate(boolean scheduled, ShapeUpdate shapeUpdate);
	
}
