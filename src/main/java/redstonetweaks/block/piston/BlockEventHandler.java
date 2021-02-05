package redstonetweaks.block.piston;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
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
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPistonHandler;
import redstonetweaks.interfaces.mixin.RTIWorld;
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
	
	private int progress;
	
	private List<BlockPos> movedPositions;
	private List<BlockPos> brokenPositions;
	private Map<BlockPos, SlabType> splitSlabTypes;
	private Map<BlockPos, SlabType> mergedSlabTypes;
	private Map<BlockPos, Boolean> loosePistonHeads;
	private BlockState[] movedStates;
	private Map<BlockPos, BlockState> movedStatesMap;
	private Map<BlockPos, BlockState> newStatesMap;
	private BlockEntity[] movedBlockEntities;
	private BlockState[] removedStates;
	private List<BlockPos> droppedBlocks;
	private Iterator<Entry<BlockPos, BlockState>> movedStatesIt;
	private Iterator<BlockPos> movedPositionsIt;
	
	private int removedIndex;
	private int index;
	
	private boolean isIterating;
	private boolean droppedBlock;
	private int moveProgress;
	
	public BlockEventHandler(World world, BlockPos pos, BlockState state, int type, int data, boolean sticky) {
		this.world = world;
		this.pos = pos;
		this.state = state;
		this.type = type;
		this.extend = (type == MotionType.EXTEND || type == MotionType.EXTEND_BACKWARDS);
		this.data = data;
		this.facing = state.get(Properties.FACING);
		this.moveDirection = (type == MotionType.EXTEND || type == MotionType.RETRACT_FORWARDS) ? facing : facing.getOpposite();
		this.headPos = pos.offset(facing);
		this.sticky = sticky;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	// Return false if the block event has been aborted
	public boolean startBlockEvent() {
		if (!world.isClient()) {
			boolean lazy = extend ? PistonSettings.lazyRisingEdge(sticky) : PistonSettings.lazyFallingEdge(sticky);
			boolean shouldExtend =  lazy ? extend : PistonHelper.isReceivingPower(world, pos, state, facing, true);
			
			if (shouldExtend && !extend) {
				int flags = PistonHelper.doDoubleRetraction(sticky) ? 18 : 2;
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), flags);
				
				return false;
			}
			
			if (!shouldExtend && extend) {
				return false;
			}
		}
		
		progress = 0;
		
		if (extend) {
			if (!startMove()) {
				if (PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityRisingEdge(sticky));
				}
				
				return false;
			}
		} else {
			if (PistonSettings.looseHead(sticky) && !PistonHelper.hasPistonHead(world, pos, sticky, facing)) {
				if (PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityFallingEdge(sticky));
				}
				
				return false;
			}
			
			if (type == MotionType.RETRACT_FORWARDS) {
				boolean canRetractForwards = false;
				
				if (sticky) {
					BlockPos blockPos = headPos.offset(facing);
					BlockState blockState = world.getBlockState(blockPos);
					
					if (!blockState.isAir() && PistonHelper.canPull(blockState) && !(PistonBlock.isMovable(blockState, world, blockPos, facing.getOpposite(), false, facing) && new PistonHandler(world, pos, facing, false).calculatePush())) {
						canRetractForwards = true;
					}
				}
				
				if (!canRetractForwards) {
					if (!world.isClient()) {
						world.addSyncedBlockEvent(pos, state.getBlock(), MotionType.RETRACT_A, data);
					}
					
					return false;
				}
			}
			
			BlockEntity blockEntity = world.getBlockEntity(headPos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				((RTIPistonBlockEntity)blockEntity).finishSource();
			} else {
				tryContinueBlockEvent();
			}
			
		}
		
		return true;
	}
	
	// Return false if the block event has been completed
	public boolean tryContinueBlockEvent() {
		if (type == MotionType.EXTEND) {
			if (tryContinueMove()) {
				return true;
			} else {
				// We remove the block event handler a little earlier to preserve vanilla behavior
				// like pistons 0-ticking from a button placed on them
				((RTIWorld)world).removeBlockEventHandler(pos);
				
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), 67);
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
				
				return false;
			}
		} else if (type == MotionType.EXTEND_BACKWARDS) {
			switch (progress) {
			case 0:
				if (!tryContinueMove()) {
					// A short arm is placed so the rod does not poke out of the back of the piston base
					BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT).with(Properties.SHORT, true);
					world.setBlockState(pos, pistonHead, 67);
					
					progress++;
				}
				
				return true;
			case 1:
				BlockPos behindPos = pos.offset(moveDirection);
				
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(true, facing, sticky, true, true, state.with(Properties.EXTENDED, true));
				
				((RTIWorld)world).queueBlockEntityPlacement(behindPos, pistonBlockEntity);
				world.setBlockState(behindPos, pistonExtension, 20);
				
				world.updateNeighbors(behindPos, pistonExtension.getBlock());
				pistonExtension.updateNeighbors(world, behindPos, 2);
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, true, sticky));
				
				return false;
			default:
				return false;
			}
		} else if (type == MotionType.RETRACT_A || type == MotionType.RETRACT_B) {
			switch (progress) {
			case 0:
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(false, facing, sticky, true, false, PistonHelper.getPiston(sticky, Direction.byId(data & 7), false));
				
				((RTIWorld)world).queueBlockEntityPlacement(pos, pistonBlockEntity);
				world.setBlockState(pos, pistonExtension, 20);
				
				world.updateNeighbors(pos, pistonExtension.getBlock());
				pistonExtension.updateNeighbors(world, pos, 2);
				
				if (!world.isClient()) {
					BlockPos frontPos = headPos.offset(facing);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (PistonHelper.isPiston(frontState) && frontState.get(Properties.EXTENDED) && PistonHelper.doDoubleRetraction(PistonHelper.isSticky(frontState))) {
						world.updateNeighbor(frontPos, frontState.getBlock(), frontPos);
					}
				}
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
				
				progress++;
				
				return true;
			case 1:
				if (sticky) {
					progress++;
					
					if (!PistonSettings.doBlockDropping() || PistonSettings.fastBlockDropping()) {
						if (PistonSettings.superBlockDropping()) {
							droppedBlocks = ((RTIPistonHandler)new PistonHandler(world, pos, facing, true)).getMovingStructure();
						} else {
							droppedBlocks = new ArrayList<>();
							
							BlockPos frontPos = pos.offset(facing, 2);
							BlockState frontState = world.getBlockState(frontPos);
							
							if (frontState.isOf(Blocks.MOVING_PISTON)) {
								BlockEntity blockEntity = world.getBlockEntity(frontPos);
								
								if (blockEntity instanceof PistonBlockEntity) {
									PistonBlockEntity pistonBlockEntity2 = (PistonBlockEntity)blockEntity;
									
									if (pistonBlockEntity2.getFacing() == facing && pistonBlockEntity2.isExtending()) {
										droppedBlocks.add(frontPos);
									}
								}
							}
						}
					} else {
						// Skip block dropping
						progress++;
					}
					
					return true;
				}
				
				world.removeBlock(headPos, false);
				
				return false;
			case 2:
				if (isIterating) {
					if (index >= 0) {
						BlockEntity blockEntity = world.getBlockEntity(droppedBlocks.get(index));
						
						if (blockEntity instanceof PistonBlockEntity) {
							((RTIPistonBlockEntity)blockEntity).finishSource();
						}
						
						index--;
					} else {
						progress++;
					}
				} else {
					if (droppedBlocks.isEmpty()) {
						progress++;
					} else {
						isIterating = true;
						index = droppedBlocks.size() - 1;
						
						droppedBlock = true;
					}
				}
				
				return true;
			case 3:
				if (!droppedBlock) {
					BlockPos frontPos = headPos.offset(facing);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (!frontState.isAir() && PistonBlock.isMovable(frontState, world, frontPos, moveDirection, false, facing) && PistonHelper.getPistonBehavior(frontState) == PistonBehavior.NORMAL) {
						progress++;
						
						if (world.getBlockState(headPos).isOf(Blocks.PISTON_HEAD)) {
							world.setBlockState(headPos, Blocks.AIR.getDefaultState(), PistonSettings.headUpdatesWhenPulling() ? 5 : 20);
						}
						
						return true;
					} else {
						world.removeBlock(headPos, false);
					}
				} else {
					// A fix for head duping when movableWhenExtended is enabled
					world.removeBlock(headPos, false);
				}
				
				return false;
			case 4:
				progress++;
				
				return startMove();
			case 5:
				return tryContinueMove();
			default:
				return false;
			}
		} else if (type == MotionType.RETRACT_FORWARDS) {
			switch (progress) {
			case 0:
				BlockState air = Blocks.AIR.getDefaultState();
				
				world.setBlockState(pos, air, 18);
				world.setBlockState(headPos, air, 18);
				
				world.updateNeighbors(pos, air.getBlock());
				air.updateNeighbors(world, pos, 2);
				
				progress++;
				
				return true;
			case 1:
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(false, facing, sticky, true, true, state.with(Properties.EXTENDED, false));
				
				((RTIWorld)world).queueBlockEntityPlacement(headPos, pistonBlockEntity);
				world.setBlockState(headPos, pistonExtension, 67);
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, true, sticky));
				
				return false;
			default:
				return false;
			}
		} else {
			return false;
		}
	}
	
	// Return true if the move started successfully
	private boolean startMove() {
		PistonHandler pistonHandler = new PistonHandler(world, pos, type == MotionType.EXTEND_BACKWARDS ? moveDirection : facing, extend);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			movedPositions = pistonHandler.getMovedBlocks();
			brokenPositions = pistonHandler.getBrokenBlocks();
			loosePistonHeads = ((RTIPistonHandler)pistonHandler).getLoosePistonHeads();
			splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
			mergedSlabTypes = ((RTIPistonHandler)pistonHandler).getMergedSlabTypes();
			
			int movedCount = movedPositions.size();
			int brokenCount = brokenPositions.size();
			
			movedStates = new BlockState[movedCount];
			movedStatesMap = new HashMap<>();
			movedBlockEntities = new BlockEntity[movedCount];
			
			removedStates = new BlockState[movedCount + brokenCount];
			
			for (index  = 0; index < movedCount; index++) {
				BlockPos fromPos = movedPositions.get(index);
				BlockPos toPos = fromPos.offset(moveDirection);
				BlockState movedState = world.getBlockState(fromPos);
				BlockEntity movedBlockEntity = PistonHelper.getBlockEntityToMove(world, fromPos);
				
				if (!PistonHelper.prepareDoubleRetraction(world, fromPos, movedState)) {
					MovedBlock movedBlock;
					
					if (loosePistonHeads.get(fromPos) == Boolean.TRUE) {
						movedBlock = PistonHelper.detachPistonHead(world, fromPos, movedState, movedBlockEntity, moveDirection);
					} else
					if (loosePistonHeads.get(toPos) == Boolean.FALSE) {
						movedBlock = PistonHelper.attachPistonHead(world, fromPos, movedState, movedBlockEntity, moveDirection);
					} else
					if (splitSlabTypes.containsKey(fromPos)) {
						movedBlock = PistonHelper.splitDoubleSlab(world, fromPos, movedState, movedBlockEntity, splitSlabTypes.get(fromPos));
					} else {
						movedBlock = new MovedBlock(movedState, movedBlockEntity);
					}
					
					movedState = movedBlock.getBlockState();
					movedBlockEntity = movedBlock.getBlockEntity();
				}
				
				movedStates[index] = movedState;
				movedStatesMap.put(fromPos, movedState);
				movedBlockEntities[index] = movedBlockEntity;
			}

			removedIndex = 0;

			for (index = brokenCount - 1; index >= 0; --index) {
				BlockPos brokenPos = brokenPositions.get(index);
				BlockState brokenState = world.getBlockState(brokenPos);
				BlockEntity blockEntity = brokenState.getBlock().hasBlockEntity() ? world.getBlockEntity(brokenPos) : null;
				
				PistonBlock.dropStacks(brokenState, world, brokenPos, blockEntity);
				world.setBlockState(brokenPos, Blocks.AIR.getDefaultState(), 18);
				
				removedStates[removedIndex++] = brokenState;
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
				
				BlockState movedState = movedStates[index];
				BlockState removedState = world.getBlockState(fromPos);
				BlockEntity movedBlockEntity = movedBlockEntities[index];
				BlockState mergingState = null;
				BlockEntity mergingBlockEntity = null;
				
				if (mergedSlabTypes.containsKey(toPos) || loosePistonHeads.get(toPos) == Boolean.FALSE) {
					mergingState = removedState;
					mergingBlockEntity = PistonHelper.getBlockEntityToMove(world, toPos);
				}
				
				if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get()) {
					// This ensures the block entity gets placed
					world.setBlockState(toPos, Blocks.AIR.getDefaultState(), 80);
				}
				
				((RTIWorld)world).queueBlockEntityPlacement(toPos, PistonHelper.createPistonBlockEntity(extend, facing, sticky, false, false, movedState, movedBlockEntity, mergingState, mergingBlockEntity));
				world.setBlockState(toPos, Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing), 68);
				
				movedStatesMap.remove(toPos);
				removedStates[removedIndex++] = removedState;
				
				index--;
			} else {
				isIterating = false;
				moveProgress++;
			}
			
			break;
		case 1:
			if (type == MotionType.EXTEND) {
				PistonType pistonType = sticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
				
				BlockState movingPiston = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(true, facing, sticky, true, true, pistonHead);
				
				((RTIWorld)world).queueBlockEntityPlacement(headPos, pistonBlockEntity);
				world.setBlockState(headPos, movingPiston, 68);
				
				movedStatesMap.remove(headPos);
			}
			
			moveProgress++;
			
			break;
		case 2:
			if (!isIterating) {
				isIterating = true;
				movedPositionsIt = movedStatesMap.keySet().iterator();
			}
			if (movedPositionsIt.hasNext()) {
				BlockPos remainingPos = movedPositionsIt.next();
				
				BlockState newState = (splitSlabTypes.containsKey(remainingPos) || loosePistonHeads.containsKey(remainingPos)) ? world.getBlockState(remainingPos) : Blocks.AIR.getDefaultState();
				
				if (!newState.isAir()) {
					world.setBlockState(remainingPos, newState, 82);
				}
				
				newStatesMap.put(remainingPos, newState);
			} else {
				isIterating = false;
				moveProgress++;
			}
			
			break;
		case 3:
			if (!isIterating) {
				isIterating = true;
				movedStatesIt = movedStatesMap.entrySet().iterator();
			}
			if (movedStatesIt.hasNext()) {
				Entry<BlockPos, BlockState> entry = movedStatesIt.next();
				
				BlockPos remainingPos = entry.getKey();
				BlockState oldState = entry.getValue();
				BlockState newState = newStatesMap.get(remainingPos);
				
				oldState.prepare(world, remainingPos, 2);
				newState.updateNeighbors(world, remainingPos, 2);
				newState.prepare(world, remainingPos, 2);
			} else {
				isIterating = false;
				moveProgress++;
			}
			
			break;
		case 4:
			if (!isIterating) {
				isIterating = true;
				removedIndex = 0;
				index = brokenPositions.size() - 1;
			}
			if (index >= 0) {
				BlockPos brokenPos = brokenPositions.get(index);
				BlockState brokenState = removedStates[removedIndex++];
				
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
				BlockState movedState = removedStates[removedIndex++];
				
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
			if (extend && PistonSettings.headUpdatesOnExtension(sticky)) {
				world.updateNeighborsAlways(headPos, Blocks.PISTON_HEAD);
			}
			
			return false;
		default:
			return false;
		}
		
		return true;
	}
}
