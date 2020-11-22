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
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.setting.Tweaks;

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
	
	private Map<BlockPos, BlockState> movedStatesMap;
	private List<BlockPos> movedPositions;
	private List<BlockState> movedStates;
	private List<BlockEntity> movedBlockEntities;
	private List<BlockPos> brokenPositions;
	private Map<BlockPos, SlabType> splitSlabTypes;
	private Map<BlockPos, SlabType> mergedSlabTypes;
	private BlockState[] affectedStates;
	@SuppressWarnings("rawtypes")
	private Iterator leftOverBlocks;
	
	private int affectedIndex;
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
				int flags = Tweaks.Global.DOUBLE_RETRACTION.get() ? 16 : 2;
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
		if (type == 0) {
			if (tryContinueMove()) {
				return true;
			} else {
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), 67);
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
				
				return false;
			}
		} else if (type == 1 || type == 2) {
			switch (retractionProgress) {
			case 0:
				BlockState pistonBase = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				world.setBlockState(pos, pistonBase, 20);
				
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(state.getBlock().getDefaultState().with(Properties.FACING, Direction.byId(data & 7)), facing, false, true, sticky);
				world.setBlockEntity(pos, pistonBlockEntity);
				
				world.updateNeighbors(pos, pistonBase.getBlock());
				pistonBase.updateNeighbors(world, pos, 2);
				
				if (redstonetweaks.setting.Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
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
					
					if (!Tweaks.StickyPiston.DO_BLOCK_DROPPING.get() || Tweaks.StickyPiston.FAST_BLOCK_DROPPING.get()) {
						BlockPos frontPos = headPos.offset(facing);
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
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
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
		if (!(droppedBlock && Tweaks.StickyPiston.DO_BLOCK_DROPPING.get())) {
			if (frontState.isAir() || !PistonBlock.isMovable(frontState, world, frontPos, moveDirection, false, facing) || PistonHelper.getPistonBehavior(frontState) != PistonBehavior.NORMAL && !frontState.isOf(Blocks.PISTON) && !frontState.isOf(Blocks.STICKY_PISTON)) {
				world.removeBlock(headPos, false);
			} else {
				retractionProgress++;
				stillRetracting = startMove();
			}
		}
		
		world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
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
			movedStatesMap = Maps.newHashMap();
			movedPositions = pistonHandler.getMovedBlocks();
			movedStates = Lists.newArrayList();
			movedBlockEntities = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities();
			splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
			mergedSlabTypes = ((RTIPistonHandler)pistonHandler).getMergedSlabTypes();
			
			for (BlockPos movedPos : movedPositions) {
				BlockState movedState = world.getBlockState(movedPos);
				movedStates.add(movedState);
				movedStatesMap.put(movedPos, movedState);
				
				if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get()) {
					BlockEntity movedBlockEntity = world.getBlockEntity(movedPos);
					
					((RTIPistonHandler)pistonHandler).addMovedBlockEntity(movedBlockEntity);
					
					if (movedBlockEntity != null) {
						world.removeBlockEntity(movedPos);
						
						// Fix for disappearing block entities on the client
						if (!world.isClient()) {
							movedBlockEntity.markDirty();
						}
					}
				}
				
				// Notify clients of any pistons that are about to be "double retracted"
				PistonHelper.prepareDoubleRetraction(world, movedPos, movedState);
			}
			
			brokenPositions = pistonHandler.getBrokenBlocks();
			affectedStates = new BlockState[movedPositions.size() + brokenPositions.size()];

			affectedIndex = 0;

			for (index = brokenPositions.size() - 1; index >= 0; --index) {
				BlockPos brokenPos = brokenPositions.get(index);
				BlockState brokenState = world.getBlockState(brokenPos);
				BlockEntity blockEntity = brokenState.getBlock().hasBlockEntity() ? world.getBlockEntity(brokenPos) : null;
				
				PistonBlock.dropStacks(brokenState, world, brokenPos, blockEntity);
				world.setBlockState(brokenPos, Blocks.AIR.getDefaultState(), 18);
				
				affectedStates[affectedIndex++] = brokenState;
			}

			isIterating = false;
			moveProgress = 0;
			
			return true;
		}
	}
	
	private boolean tryContinueMove() {
		switch (moveProgress) {
		case 0:
			if (!isIterating) {
				isIterating = true;
				index = movedPositions.size() - 1;
			}
			if (index >= 0) {
				BlockPos fromPos = movedPositions.get(index);
				BlockPos toPos = fromPos.offset(moveDirection);
				
				BlockState movedState = movedStates.get(index);
				BlockState affectedState = world.getBlockState(fromPos);
				BlockEntity movedBlockEntity = null;
				boolean isMergingSlabs = false;
				
				if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get()) {
					movedBlockEntity = movedBlockEntities.get(index);
				}
				if (Tweaks.Global.MERGE_SLABS.get()) {
					SlabType movingType = splitSlabTypes.get(fromPos);
					if (movingType != null) {
						SlabType remainingType = SlabHelper.getOppositeType(movingType);
						BlockState remainingState = movedState.with(Properties.SLAB_TYPE, remainingType);
						
						movedStatesMap.put(fromPos, remainingState);
						world.setBlockState(fromPos, remainingState, 4);
						
						movedState = movedState.with(Properties.SLAB_TYPE, movingType);
					}
					if (mergedSlabTypes.containsKey(toPos)) {
						isMergingSlabs = true;
					}
				}
				
				world.setBlockState(toPos, Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing), 68);
				world.setBlockEntity(toPos, PistonHelper.createPistonBlockEntity(movedState, movedBlockEntity, facing, extend, false, sticky, isMergingSlabs));
				
				movedStatesMap.remove(toPos);
				affectedStates[affectedIndex++] = affectedState;
				
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
				BlockState movingPiston = Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, facing).with(PistonExtensionBlock.TYPE, pistonType);
				
				world.setBlockState(headPos, movingPiston, 68);
				world.setBlockEntity(headPos, PistonHelper.createPistonBlockEntity(pistonHead, facing, true, true, sticky));
				
				movedStatesMap.remove(headPos);
			}
			moveProgress++;
			break;
		case 2:
			if (!isIterating) {
				isIterating = true;
				leftOverBlocks = movedStatesMap.keySet().iterator();
			}
			if (leftOverBlocks.hasNext()) {
				BlockPos leftOverPos = (BlockPos)leftOverBlocks.next();
				BlockState airState = Blocks.AIR.getDefaultState();
				
				if (!redstonetweaks.setting.Tweaks.Global.MERGE_SLABS.get() || !splitSlabTypes.containsKey(leftOverPos)) {
					world.setBlockState(leftOverPos, airState, 82);
				}
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 3:
			if (!isIterating) {
				isIterating = true;
				leftOverBlocks = movedStatesMap.entrySet().iterator();
			}
			if (leftOverBlocks.hasNext()) {
				@SuppressWarnings("unchecked")
				Entry<BlockPos, BlockState> entry = (Entry<BlockPos, BlockState>)leftOverBlocks.next();
				BlockPos leftOverPos = entry.getKey();
				BlockState leftOverState = entry.getValue();
				
				leftOverState.prepare(world, leftOverPos, 2);
				BlockState newState;
				if (Tweaks.Global.MERGE_SLABS.get() && splitSlabTypes.containsKey(leftOverPos)) {
					newState = movedStatesMap.get(leftOverPos);
				} else {
					newState = Blocks.AIR.getDefaultState();
				}
				newState.updateNeighbors(world, leftOverPos, 2);
				newState.prepare(world, leftOverPos, 2);
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 4:
			if (!isIterating) {
				isIterating = true;
				affectedIndex = 0;
				index = brokenPositions.size() - 1;
			}
			if (index >= 0) {
				BlockPos brokenPos = brokenPositions.get(index);
				BlockState brokenState = affectedStates[affectedIndex++];
				
				brokenState.prepare(world, brokenPos, 2);
				world.updateNeighborsAlways(brokenPos, brokenState.getBlock());
				
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 5:
			if (!isIterating) {
				isIterating = true;
				index = movedPositions.size() - 1;
			}
			if (index >= 0) {
				BlockPos blockPos = movedPositions.get(index);
				BlockState movedState = affectedStates[affectedIndex++];
				
				world.updateNeighborsAlways(blockPos, movedState.getBlock());
				if (Tweaks.BugFixes.MC120986.get() && movedState.hasComparatorOutput()) {
					world.updateComparators(blockPos, movedState.getBlock());
				}
				
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			break;
		case 6:
			if (extend) {
				if (PistonHelper.headUpdatesOnExtension(sticky)) {
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
