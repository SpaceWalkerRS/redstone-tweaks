package redstonetweaks.block.piston;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.setting.Settings;

public class BlockEventHandler {
	
	private final World world;
	private final BlockState state;
	private final BlockPos pos;
	private final int type;
	private final boolean extend;
	private final int data;
	private final Direction facing;
	private final Direction moveDirection;
	private final BlockPos headPos;
	private final boolean sticky;
	
	private int retractionProgress;
	
	private Map<BlockPos, BlockState> movedBlocks;
	private List<BlockPos> movedBlocksPos;
	private List<BlockState> movedBlockStates;
	private List<BlockEntity> movedBlockEntities;
	private List<BlockPos> brokenBlocksPos;
	private Map<BlockPos, SlabType> splitSlabTypes;
	private BlockState[] affectedBlockStates;
	@SuppressWarnings("rawtypes")
	private Iterator leftOverBlocks;
	
	private int affectedBlocksIndex;
	private int index;
	
	private boolean isIterating;
	private int moveProgress;
	
	public BlockEventHandler(World world, BlockPos pos, BlockState state, int type, int data, boolean sticky) {
		this.world = world;
		this.pos = pos;
		this.state = state;
		this.type = type;
		this.extend = type == 0;
		this.data = data;
		this.facing = state.get(Properties.FACING);
		this.moveDirection = extend ? facing : facing.getOpposite();
		this.headPos = pos.offset(facing);
		this.sticky = sticky;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public boolean startBlockEvent() {
		if (!world.isClient()) {
			boolean extended = type != 0;
			boolean lazy = extended ? PistonHelper.lazyFallingEdge(sticky) : PistonHelper.lazyRisingEdge(sticky);
			boolean shouldExtend = lazy ? !extended : PistonHelper.isReceivingPower(world, pos, state, facing, true);
			
			if (shouldExtend && (type == 1 || type == 2)) {
				int flags = Settings.Global.DOUBLE_RETRACTION.get() ? 16 : 2;
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), flags);
				
				return false;
			}
			
			if (!shouldExtend && type == 0) {
				if (PistonHelper.updateSelfWhilePowered(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonHelper.tickPriorityRisingEdge(sticky));
				}
				
				return false;
			}
		}
		
		if (type == 0) {
			if (!startMove()) {
				return false;
			}
		} else if (type == 1 || type == 2) {
			retractionProgress = 0;
			
			BlockEntity blockEntity = world.getBlockEntity(headPos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			} else {
				tryContinueBlockEvent();
			}
		}
		
		return true;
	}
	
	// Return false if the block event has been completed
	public boolean tryContinueBlockEvent() {
		BlockState blockState;
		PistonBlockEntity pistonBlockEntity;
		
		if (type == 0) {
			if (tryContinueMove()) {
				return true;
			} else {
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), 67);
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
				
				return false;
			}
		} else if (type == 1 || type == 2) {
			switch (retractionProgress) {
			case 0:
				blockState = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				world.setBlockState(pos, blockState, 20);
				
				pistonBlockEntity = new PistonBlockEntity(state.getBlock().getDefaultState().with(Properties.FACING, Direction.byId(data & 7)), facing, false, true);
				((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(sticky);
				world.setBlockEntity(pos, pistonBlockEntity);
				
				world.updateNeighbors(pos, blockState.getBlock());
				blockState.updateNeighbors(world, pos, 2);
				
				if (redstonetweaks.setting.Settings.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
					BlockPos frontPos = pos.offset(facing, 2);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (frontState.getBlock() instanceof PistonBlock && frontState.get(Properties.EXTENDED)) {
						world.updateNeighbor(frontPos, frontState.getBlock(), frontPos);
					}
				}
				
				retractionProgress++;
				return true;
			case 1:
				if (sticky) {
					retractionProgress++;
					
					if (!Settings.StickyPiston.DO_BLOCK_DROPPING.get() || Settings.StickyPiston.FAST_BLOCK_DROPPING.get()) {
						BlockPos frontPos = pos.offset(facing, 2);
						BlockState frontState = world.getBlockState(frontPos);
						if (frontState.isOf(Blocks.MOVING_PISTON)) {
							BlockEntity blockEntity = world.getBlockEntity(frontPos);
							if (blockEntity instanceof PistonBlockEntity) {
								pistonBlockEntity = (PistonBlockEntity)blockEntity;
								if (pistonBlockEntity.getFacing() == facing && pistonBlockEntity.isExtending()) {
									pistonBlockEntity.finish();
									
									return true;
								}
							}
						}
					}
					
					return finishRetraction(false);
				} else {
					world.removeBlock(headPos, false);
				}
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
				return false;
			case 2:
				return finishRetraction(true);
			case 3:
				return tryContinueMove();
			default:
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean finishRetraction(boolean droppedBlock) {
		BlockPos frontPos = pos.offset(facing, 2);
		BlockState frontState = world.getBlockState(frontPos);;
		
		boolean stillRetracting = false;
		if (!(droppedBlock && Settings.StickyPiston.DO_BLOCK_DROPPING.get())) {
			if (frontState.isAir() || !PistonBlock.isMovable(frontState, world, frontPos, moveDirection, false, facing) || (Settings.Barrier.IS_MOVABLE.get() && frontState.isOf(Blocks.BARRIER) ? PistonBehavior.NORMAL : frontState.getPistonBehavior()) != PistonBehavior.NORMAL && !frontState.isOf(Blocks.PISTON) && !frontState.isOf(Blocks.STICKY_PISTON)) {
				world.removeBlock(headPos, false);
			} else {
				retractionProgress++;
				stillRetracting = startMove();
			}
		}
		
		world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
		return stillRetracting;
	}
	
	// Return false if the move started successfully
	private boolean startMove() {
		if (!extend && world.getBlockState(headPos).isOf(Blocks.PISTON_HEAD)) {
			world.setBlockState(headPos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, facing, extend);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			movedBlocks = Maps.newHashMap();
			movedBlocksPos = pistonHandler.getMovedBlocks();
			movedBlockStates = Lists.newArrayList();
			movedBlockEntities = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities();
			splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
			
			for (BlockPos movedBlockPos : movedBlocksPos) {
				BlockState movedBlockState = world.getBlockState(movedBlockPos);
				movedBlockStates.add(movedBlockState);
				movedBlocks.put(movedBlockPos, movedBlockState);
			}
			
			brokenBlocksPos = pistonHandler.getBrokenBlocks();
			affectedBlockStates = new BlockState[movedBlocksPos.size() + brokenBlocksPos.size()];

			affectedBlocksIndex = 0;

			for (index = brokenBlocksPos.size() - 1; index >= 0; --index) {
				BlockPos brokenBlockPos = brokenBlocksPos.get(index);
				BlockState brokenBlockState = world.getBlockState(brokenBlockPos);
				BlockEntity blockEntity = brokenBlockState.getBlock().hasBlockEntity() ? world.getBlockEntity(brokenBlockPos) : null;
				PistonBlock.dropStacks(brokenBlockState, world, brokenBlockPos, blockEntity);
				world.setBlockState(brokenBlockPos, Blocks.AIR.getDefaultState(), 18);
				affectedBlockStates[affectedBlocksIndex++] = brokenBlockState;
			}

			isIterating = false;
			moveProgress = 0;
			
			return true;
		}
	}
	
	private boolean tryContinueMove() {
		BlockPos blockPos;
		BlockState blockState;
		
		switch (moveProgress) {
		case 0:
			if (!isIterating) {
				isIterating = true;
				index = movedBlocksPos.size() - 1;
			}
			if (index >= 0) {
				blockPos = movedBlocksPos.get(index);
				blockState = world.getBlockState(blockPos);
				blockPos = blockPos.offset(moveDirection);
				// Merge slabs feature start
				if (Settings.Global.MERGE_SLABS.get())
					PistonHelper.tryMergeMovedSlab(world, blockState, blockPos, index, splitSlabTypes, movedBlocksPos, movedBlockStates, movedBlocks);
				// Merge slabs feature end
				movedBlocks.remove(blockPos);
				world.setBlockState(blockPos, Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing), 68);
				
				BlockState movedBlockState = movedBlockStates.get(index);
				BlockEntity movedBlockEntity = null;
				if (Settings.Global.MOVABLE_BLOCK_ENTITIES.get()) {
					movedBlockEntity = movedBlockEntities.get(index);
				}
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(movedBlockState, movedBlockEntity, facing, extend, false, sticky);
				world.setBlockEntity(blockPos, pistonBlockEntity);
				
				affectedBlockStates[affectedBlocksIndex++] = blockState;
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 1:
			if (extend) {
				PistonType pistonType = sticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, facing).with(PistonHeadBlock.TYPE, pistonType);
				blockState = Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, facing).with(PistonExtensionBlock.TYPE, pistonType);
				movedBlocks.remove(headPos);
				world.setBlockState(headPos, blockState, 68);
				PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pistonHead, facing, true, true);
				((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(sticky);
				world.setBlockEntity(headPos, pistonBlockEntity);
			}
			moveProgress++;
			break;
		case 2:
			if (!isIterating) {
				isIterating = true;
				leftOverBlocks = movedBlocks.keySet().iterator();
			}
			if (leftOverBlocks.hasNext()) {
				blockPos = (BlockPos)leftOverBlocks.next();
				// Merge slabs feature start
				BlockState newState;
				if (Settings.Global.MERGE_SLABS.get()) {
					newState = PistonHelper.getAdjustedSlabState(Blocks.AIR.getDefaultState(), world, blockPos, splitSlabTypes);
				} else {
					newState = Blocks.AIR.getDefaultState();
				}
				world.setBlockState(blockPos, newState, 82);
				// Merge slabs feature end
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 3:
			if (!isIterating) {
				isIterating = true;
				leftOverBlocks = movedBlocks.entrySet().iterator();
			}
			if (leftOverBlocks.hasNext()) {
				@SuppressWarnings("unchecked")
				Entry<BlockPos, BlockState> entry = (Entry<BlockPos, BlockState>)leftOverBlocks.next();
				blockPos = entry.getKey();
				blockState = entry.getValue();
				blockState.prepare(world, blockPos, 2);
				// Merge slabs feature start
				BlockState newState;
				if (Settings.Global.MERGE_SLABS.get()) {
					newState = PistonHelper.getAdjustedSlabState(Blocks.AIR.getDefaultState(), world, blockPos, splitSlabTypes);
				} else {
					newState = Blocks.AIR.getDefaultState();
				}
				newState.updateNeighbors(world, blockPos, 2);
				newState.prepare(world, blockPos, 2);
				// Merge slabs feature end
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 4:
			if (!isIterating) {
				isIterating = true;
				affectedBlocksIndex = 0;
				index = brokenBlocksPos.size() - 1;
			}
			if (index >= 0) {
				blockPos = brokenBlocksPos.get(index);
				blockState = affectedBlockStates[affectedBlocksIndex++];
				blockState.prepare(world, blockPos, 2);
				world.updateNeighborsAlways(blockPos, blockState.getBlock());
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 5:
			if (!isIterating) {
				isIterating = true;
				index = movedBlocksPos.size() - 1;
			}
			if (index >= 0) {
				blockPos = movedBlocksPos.get(index);
				blockState = affectedBlockStates[affectedBlocksIndex++];
				world.updateNeighborsAlways(blockPos, blockState.getBlock());
				if (Settings.BugFixes.MC120986.get() && blockState.hasComparatorOutput()) {
					world.updateComparators(blockPos, blockState.getBlock());
				}
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 6:
			if (extend) {
				if (!PistonHelper.suppressHeadUpdatesOnExtension(sticky)) {
					world.updateNeighborsAlways(headPos, Blocks.PISTON_HEAD);
				}
				
				moveProgress++;
				break;
			} else {
				return false;
			}
		default:
			return false;
		}
		return true;
	}
}
