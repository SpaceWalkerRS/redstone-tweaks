package redstonetweaks.block.piston;

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
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;

public class BlockEventHandler {
	
	private final World world;
	private final BlockState state;
	private final BlockPos pos;
	private final int type;
	private final boolean extend;
	private final int data;
	private final boolean sticky;
	private Direction facing;
	private Direction moveDirection;
	private BlockPos headPos;
	
	private int progress;
	
	private List<BlockPos> movedPositions;
	private List<BlockPos> brokenPositions;
	private Map<BlockPos, SlabType> splitSlabTypes;
	private Map<BlockPos, SlabType> mergedSlabTypes;
	private BlockState[] movedStates;
	private Map<BlockPos, BlockState> movedStatesMap;
	private BlockEntity[] movedBlockEntities;
	private BlockState[] affectedStates;
	private Iterator<Entry<BlockPos, BlockState>> leftOverStates;
	private Iterator<BlockPos> leftOverPositions;
	
	private int affectedIndex;
	private int index;
	
	private boolean isIterating;
	private boolean pushSelf;
	private boolean droppedBlock;
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
	
	// Return false if the block event has been aborted
	public boolean startBlockEvent() {
		if (!world.isClient()) {
			boolean extended = type != 0;
			boolean lazy = extended ? PistonHelper.lazyFallingEdge(sticky) : PistonHelper.lazyRisingEdge(sticky);
			boolean shouldExtend = lazy ? !extended : PistonHelper.isReceivingPower(world, pos, state, facing, true);
			
			if (shouldExtend && (type == 1 || type == 2)) {
				int flags = Tweaks.Global.DOUBLE_RETRACTION.get() ? 18 : 2;
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
		
		progress = 0;
		
		if (type == 0) {
			if (!startMove()) {
				facing = facing.getOpposite();
				moveDirection = moveDirection.getOpposite();
				headPos = pos.offset(facing);
				
				if (!PistonHelper.canMoveSelf(sticky) || !startMove()) {
					if (PistonHelper.updateSelfWhilePowered(sticky)) {
						world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonHelper.tickPriorityRisingEdge(sticky));
					}
					
					return false;
				}
				
				pushSelf = true;
			}
		} else if (type == 1 || type == 2) {
			BlockEntity blockEntity = world.getBlockEntity(headPos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
				
				if (!(world.getBlockState(pos).getBlock() instanceof PistonBlock)) {
					return false;
				}
			} else {
				tryContinueBlockEvent();
			}
		}
		
		return true;
	}
	
	// Return false if the block event has been completed
	public boolean tryContinueBlockEvent() {
		if (type == 0) {
			switch (progress) {
			case 0:
				if (tryContinueMove()) {
					return true;
				} else {
					if (pushSelf) {
						PistonType pistonType = sticky ? PistonType.STICKY : PistonType.DEFAULT;
						BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
						PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(state.with(Properties.EXTENDED, true), facing, true, true, sticky);
						
						BlockPos toPos = pos.offset(facing);
						
						world.setBlockState(toPos, Blocks.AIR.getDefaultState(), 18);
						
						world.setBlockState(toPos, pistonExtension, 20);
						world.setBlockEntity(toPos, pistonBlockEntity);
						
						world.updateNeighbors(toPos, pistonExtension.getBlock());
						pistonExtension.updateNeighbors(world, toPos, 2);
						
						progress++;
						return true;
					} else {
						world.setBlockState(pos, state.with(Properties.EXTENDED, true), 67);
						world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
						
						return false;
					}
				}
			case 1:
				PistonType pistonType = sticky ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.FACING, facing.getOpposite()).with(Properties.PISTON_TYPE, pistonType);
				
				world.setBlockState(pos, pistonHead, 67);
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, true, sticky));
				
				return false;
			default:
				return false;
			}
		} else if (type == 1 || type == 2) {
			switch (progress) {
			case 0:
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(state.getBlock().getDefaultState().with(Properties.FACING, Direction.byId(data & 7)), facing, false, true, sticky);
				
				world.setBlockState(pos, pistonExtension, 20);
				world.setBlockEntity(pos, pistonBlockEntity);
				
				world.updateNeighbors(pos, pistonExtension.getBlock());
				pistonExtension.updateNeighbors(world, pos, 2);
				
				if (redstonetweaks.setting.Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
					BlockPos frontPos = headPos.offset(facing);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (frontState.getBlock() instanceof PistonBlock && frontState.get(Properties.EXTENDED)) {
						world.updateNeighbor(frontPos, frontState.getBlock(), frontPos);
					}
				}
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, extend, sticky));
				
				progress++;
				return true;
			case 1:
				if (sticky) {
					progress++;
					
					BlockPos frontPos = headPos.offset(facing);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (frontState.isOf(Blocks.MOVING_PISTON)) {
						BlockEntity blockEntity = world.getBlockEntity(frontPos);
						
						if (blockEntity instanceof PistonBlockEntity) {
							pistonBlockEntity = (PistonBlockEntity)blockEntity;
							
							if (pistonBlockEntity.getFacing() == facing && pistonBlockEntity.isExtending()) {
								droppedBlock = true;
								
								if (!Tweaks.StickyPiston.DO_BLOCK_DROPPING.get() || Tweaks.StickyPiston.FAST_BLOCK_DROPPING.get()) {
									pistonBlockEntity.finish();
									
									droppedBlock = Tweaks.StickyPiston.DO_BLOCK_DROPPING.get();
								}
							}
						}
					}
					
					return true;
				}
				
				world.removeBlock(headPos, false);
				
				return false;
			case 2:
				if (!droppedBlock) {
					BlockPos frontPos = headPos.offset(facing);
					BlockState frontState = world.getBlockState(frontPos);
					
					if (!frontState.isAir()) {
						if (PistonBlock.isMovable(frontState, world, frontPos, moveDirection, false, facing) && (PistonHelper.getPistonBehavior(frontState) == PistonBehavior.NORMAL || frontState.getBlock() instanceof PistonBlock)) {
							progress++;
							
							return startMove();
						} else if (PistonHelper.canMoveSelf(sticky)) {
							PistonBehavior pistonBehavior = PistonHelper.getPistonBehavior(frontState);
							
							if (pistonBehavior != PistonBehavior.DESTROY && pistonBehavior != PistonBehavior.PUSH_ONLY) {
								pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
								pistonBlockEntity = PistonHelper.createPistonBlockEntity(state, world.getBlockEntity(pos), facing, true, true, sticky);
								
								world.setBlockState(headPos, pistonExtension, 84);
								world.setBlockEntity(headPos, pistonBlockEntity);
								world.setBlockState(pos, Blocks.AIR.getDefaultState(), 20);
								
								world.updateNeighbors(headPos, pistonExtension.getBlock());
								pistonExtension.updateNeighbors(world, headPos, 2);
								
								world.updateNeighbors(pos, state.getBlock());
								state.updateNeighbors(world, pos, 2);
							}
						}
							
					} else {
						world.removeBlock(frontPos, false);
					}
				}
				
				return false;
			case 3:
				return tryContinueMove();
			default:
				return false;
			}
		} else {
			return false;
		}
	}
	
	// Return true if the move started successfully
	private boolean startMove() {
		if (!extend && world.getBlockState(headPos).isOf(Blocks.PISTON_HEAD)) {
			world.setBlockState(headPos, Blocks.AIR.getDefaultState(), 20);
		}

		PistonHandler pistonHandler = new PistonHandler(world, pos, facing, extend);
		if (!pistonHandler.calculatePush()) {
			return false;
		} else {
			movedPositions = pistonHandler.getMovedBlocks();
			brokenPositions = pistonHandler.getBrokenBlocks();
			splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
			mergedSlabTypes = ((RTIPistonHandler)pistonHandler).getMergedSlabTypes();
			
			int movedCount = movedPositions.size();
			int brokenCount = brokenPositions.size();
			
			movedStates = new BlockState[movedCount];
			movedStatesMap = new HashMap<>();
			movedBlockEntities = new BlockEntity[movedCount];
			
			affectedStates = new BlockState[movedCount + brokenCount];
			
			for (index  = 0; index < movedCount; index++) {
				BlockPos movedPos = movedPositions.get(index);
				BlockState movedState = world.getBlockState(movedPos);
				BlockEntity movedBlockEntity = world.getBlockEntity(movedPos);
				
				movedStates[index] = movedState;
				movedStatesMap.put(movedPos, movedState);
				movedBlockEntities[index] = movedBlockEntity;
				
				if (movedBlockEntity != null) {
					world.removeBlockEntity(movedPos);
					
					// Fix for disappearing block entities on the client
					if (!world.isClient()) {
						movedBlockEntity.markDirty();
					}
				}
				
				// Notify clients of any pistons that are about to be "double retracted"
				PistonHelper.prepareDoubleRetraction(world, movedPos, movedState);
			}
			

			affectedIndex = 0;

			for (index = brokenCount - 1; index >= 0; --index) {
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
				
				BlockState movedState = movedStates[index];
				BlockState affectedState = world.getBlockState(fromPos);
				BlockEntity movedBlockEntity = movedBlockEntities[index];
				boolean isMergingSlabs = false;
				
				if (Tweaks.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedState)) {
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
				
				((RTIWorld)world).setMovedBlockEntity(PistonHelper.createPistonBlockEntity(movedState, movedBlockEntity, facing, extend, false, sticky, isMergingSlabs));
				world.setBlockState(toPos, Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing), 68);
				
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
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
				BlockState movingPiston = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
				
				world.setBlockState(headPos, movingPiston, 68);
				world.setBlockEntity(headPos, PistonHelper.createPistonBlockEntity(pistonHead, facing, true, true, sticky));
				
				movedStatesMap.remove(headPos);
			}
			moveProgress++;
			break;
		case 2:
			if (!isIterating) {
				isIterating = true;
				leftOverPositions = movedStatesMap.keySet().iterator();
			}
			if (leftOverPositions.hasNext()) {
				BlockPos leftOverPos = leftOverPositions.next();
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
				leftOverStates = movedStatesMap.entrySet().iterator();
			}
			if (leftOverStates.hasNext()) {
				Entry<BlockPos, BlockState> entry = leftOverStates.next();
				
				BlockPos leftOverPos = entry.getKey();
				BlockState leftOverState = entry.getValue();
				
				BlockState newState = Tweaks.Global.MERGE_SLABS.get() ? movedStatesMap.getOrDefault(entry.getKey(), Blocks.AIR.getDefaultState()) : Blocks.AIR.getDefaultState();
				
				leftOverState.prepare(world, leftOverPos, 2);
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
