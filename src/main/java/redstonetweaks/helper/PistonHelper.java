package redstonetweaks.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.DirectionalBooleanSetting;

public class PistonHelper {
	
	private static final List<Block> IMMOVABLE_BLOCK_ENTITIES = Arrays.asList(
		Blocks.MOVING_PISTON,
		Blocks.BEACON,
		Blocks.ENCHANTING_TABLE,
		Blocks.ENDER_CHEST,
		Blocks.END_GATEWAY,
		Blocks.END_PORTAL
	);
	
	// When the doubleRetraction setting is enabled this method is called from getMovedBlocks
	// in PistonHandler.class to get all the moved block states. If the block state is
	// a retracted piston a block change packet is sent to the client.
	public static BlockState getDoubleRetractionState(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (state.getBlock() instanceof PistonBlock && !state.get(Properties.EXTENDED)) {
			BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
			((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
		}
		
		return state;
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		return createPistonBlockEntity(pushedBlockState, null, pistonDir, extending, isSource, isMovedByStickyPiston);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, BlockEntity pushedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		return createPistonBlockEntity(pushedBlockState, pushedBlockEntity, pistonDir, extending, isSource, isMovedByStickyPiston, false);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, BlockEntity pushedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston, boolean isMergingSlabs) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlockState, pistonDir, extending, isSource);
		
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(isMovedByStickyPiston);
		((RTIPistonBlockEntity)pistonBlockEntity).setMovedBlockEntity(pushedBlockEntity);
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMergingSlabs(isMergingSlabs);
		
		return pistonBlockEntity;
	}
	
	public static boolean isSticky(BlockState state) {
		return state.isOf(Blocks.STICKY_PISTON);
	}
	
	public static boolean isExtended(World world, BlockPos pos, BlockState state, Direction facing) {
		boolean isExtended = state.get(Properties.EXTENDED);
		if (!isExtended && Settings.Global.DOUBLE_RETRACTION.get()) {
			BlockState frontState = world.getBlockState(pos.offset(facing));
			isExtended = frontState.isOf(Blocks.PISTON_HEAD) && frontState.get(Properties.FACING) == facing;
		}
		
		return isExtended;
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing) {
		return isReceivingPower(world, pos, state, facing, false);
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing, boolean onBlockEvent) {
		for (Direction direction : Direction.values()) {
			if (direction != facing && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
			return true;
		}
		return WorldHelper.isQCPowered(world, pos, state, onBlockEvent, getQC(state), randQC(state));
	}
	
	public static boolean canMoveBlockEntityOf(Block block) {
		return block.hasBlockEntity() && !IMMOVABLE_BLOCK_ENTITIES.contains(block);
	}
	
	public static boolean doBlockDropping() {
		return Settings.StickyPiston.DO_BLOCK_DROPPING.get();
	}
	
	public static boolean fastBlockDropping() {
		return doBlockDropping() && Settings.StickyPiston.FAST_BLOCK_DROPPING.get();
	}
	
	public static DirectionalBooleanSetting getQC(BlockState state) {
		return isSticky(state) ? Settings.StickyPiston.QC : Settings.NormalPiston.QC;
	}
	
	public static boolean randQC(BlockState state) {
		return isSticky(state) ? Settings.StickyPiston.RANDOMIZE_QC.get() : Settings.NormalPiston.RANDOMIZE_QC.get();
	}
	
	public static boolean connectsToWire(boolean sticky) {
		return sticky ? Settings.StickyPiston.CONNECTS_TO_WIRE.get() : Settings.NormalPiston.CONNECTS_TO_WIRE.get();
	}
	
	public static int delayRisingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.DELAY_RISING_EDGE.get() : Settings.NormalPiston.DELAY_RISING_EDGE.get();
	}
	
	public static int delayFallingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.DELAY_FALLING_EDGE.get() : Settings.NormalPiston.DELAY_FALLING_EDGE.get();
	}
	
	public static boolean ignoreUpdatesWhileExtending(boolean sticky) {
		return sticky ? Settings.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.get() : Settings.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.get();
	}
	
	public static boolean ignoreUpdatesWhileRetracting(boolean sticky) {
		return sticky ? Settings.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.get() : Settings.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.get();
	}
	
	public static boolean lazyRisingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.LAZY_RISING_EDGE.get() : Settings.NormalPiston.LAZY_RISING_EDGE.get();
	}
	
	public static boolean lazyFallingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.LAZY_FALLING_EDGE.get() : Settings.NormalPiston.LAZY_FALLING_EDGE.get();
	}
	
	public static int speedRisingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.SPEED_RISING_EDGE.get() : Settings.NormalPiston.SPEED_RISING_EDGE.get();
	}
	
	public static int speedFallingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.SPEED_FALLING_EDGE.get() : Settings.NormalPiston.SPEED_FALLING_EDGE.get();
	}
	
	public static boolean suppressHeadUpdatesOnExtension(boolean sticky) {
		return sticky ? Settings.StickyPiston.SUPPRESS_HEAD_UPDATES_ON_EXTENSION.get() : Settings.NormalPiston.SUPPRESS_HEAD_UPDATES_ON_EXTENSION.get();
	}
	
	public static TickPriority tickPriorityRisingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.TICK_PRIORITY_RISING_EDGE.get() : Settings.NormalPiston.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	public static TickPriority tickPriorityFallingEdge(boolean sticky) {
		return sticky ? Settings.StickyPiston.TICK_PRIORITY_FALLING_EDGE.get() : Settings.NormalPiston.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	public static boolean updateSelfWhilePowered(boolean sticky) {
		return sticky ? Settings.StickyPiston.UPDATE_SELF_WHILE_POWERED.get() : Settings.NormalPiston.UPDATE_SELF_WHILE_POWERED.get();
	}
	
	public static void tryMergeMovedSlab(World world, BlockState movedState, BlockPos frontPos, int listIndex, Map<BlockPos, SlabType> splitSlabTypes, List<BlockPos> movedPositions, List<BlockState> movedStates, Map<BlockPos, BlockState> remainingStates) {
		if (SlabHelper.isSlab(movedState)) {
			SlabType type = movedState.get(SlabBlock.TYPE);
			
			// Check if we should split the slab
			if (type == SlabType.DOUBLE) {
				// Test if we should pull the slab apart
				type = splitSlabTypes.get(movedPositions.get(listIndex));
				
				if (type != null) {
					movedState = movedState.with(SlabBlock.TYPE, type);
					movedStates.set(listIndex, movedState);
				}
			}
			
			// Merge the slab with other slabs.
			if (type != SlabType.DOUBLE) {
				BlockState frontState = world.getBlockState(frontPos);
				
				if (frontState.isOf(movedState.getBlock())) {
					SlabType frontType = frontState.get(SlabBlock.TYPE);
					
					if (frontType == SlabType.DOUBLE) {
						if (splitSlabTypes.get(frontPos) == type) {
							// Merge with the double slab (the double slab has split).
							movedStates.set(listIndex, movedState.with(SlabBlock.TYPE, SlabType.DOUBLE));
						}
					} else {
						// Test if we can merge the two slabs (make sure it is not part of moving blocks).
						if (frontType == SlabHelper.getOppositeType(type) && !remainingStates.containsKey(frontPos))
							movedStates.set(listIndex, movedState.with(SlabBlock.TYPE, SlabType.DOUBLE));
					}
				}
			}
		}
	}
	
	public static BlockState getAdjustedSlabState(BlockState fallbackState, WorldAccess world, BlockPos pos, Map<BlockPos, SlabType> splitSlabTypes) {
		BlockState oldBlockState = world.getBlockState(pos);
		if (SlabHelper.isSlab(oldBlockState) && oldBlockState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
			// Check if we have split the slab, and one of the two
			// halves should stay behind.
			SlabType splitType = splitSlabTypes.get(pos);
			if (splitType != null && splitType != SlabType.DOUBLE)
				return oldBlockState.with(SlabBlock.TYPE, SlabHelper.getOppositeType(splitType));
		}

		return fallbackState;
	}
	
	public static boolean canSlabStickTo(BlockState state, Direction dir) {
		if (Settings.Global.MERGE_SLABS.get() && dir.getAxis().isVertical()) {
			SlabType type = state.get(SlabBlock.TYPE);
			
			if (type != SlabType.DOUBLE && type != SlabHelper.getTypeFromDirection(dir)) {
				return false;
			}
		}
		
		return true;
	}
}