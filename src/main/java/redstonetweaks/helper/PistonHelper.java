package redstonetweaks.helper;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.block.piston.MotionType;
import redstonetweaks.block.piston.MovedBlock;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPistonHandler;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;

public class PistonHelper {
	
	// Blocks with block entities that are immovable
	private static final Set<Block> IMMOVABLE_BLOCKS = ImmutableSet.of(
		Blocks.MOVING_PISTON,
		Blocks.BEACON,
		Blocks.ENCHANTING_TABLE,
		Blocks.ENDER_CHEST,
		Blocks.END_GATEWAY,
		Blocks.END_PORTAL
	);
	
	public static PistonHandler createPistonHandler(World world, BlockPos pos, Direction pistonDir, boolean extending, boolean sticky) {
		PistonHandler pistonHandler = new PistonHandler(world, pos, pistonDir, extending);
		
		((RTIPistonHandler)pistonHandler).setSticky(sticky);
		
		return pistonHandler;
	}
	
	public static PistonBlockEntity createPistonBlockEntity(boolean extending, Direction facing, boolean sticky, boolean isSource, boolean sourceIsMoving, BlockState movedState) {
		return createPistonBlockEntity(extending, facing, sticky, isSource, sourceIsMoving, movedState, null, null, null);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(boolean extending, Direction facing, boolean sticky, boolean isSource, boolean sourceIsMoving, BlockState movedState, BlockEntity movedBlockEntity, BlockState mergedState, BlockEntity mergedBlockEntity) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(movedState, facing, extending, isSource);
		
		((RTIPistonBlockEntity)pistonBlockEntity).setSticky(sticky);
		((RTIPistonBlockEntity)pistonBlockEntity).setSourceIsMoving(sourceIsMoving);
		((RTIPistonBlockEntity)pistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
		((RTIPistonBlockEntity)pistonBlockEntity).setMergingState(mergedState);
		((RTIPistonBlockEntity)pistonBlockEntity).setMergingBlockEntity(mergedBlockEntity);
		
		((RTIPistonBlockEntity)pistonBlockEntity).init();
		
		return pistonBlockEntity;
	}
	
	public static BlockState getPiston(boolean sticky, Direction facing, boolean extended) {
		return (sticky ? Blocks.STICKY_PISTON : Blocks.PISTON).getDefaultState().with(Properties.FACING, facing).with(Properties.EXTENDED, extended);
	}
	
	public static boolean isPiston(BlockState state, boolean sticky, Direction facing) {
		return isPiston(state, sticky) && state.get(Properties.FACING) == facing;
	}
	
	public static boolean isPiston(BlockState state, boolean sticky) {
		return isPiston(state) && isSticky(state) == sticky;
	}
	
	public static boolean isPiston(BlockState state, Direction facing) {
		return isPiston(state) && state.get(Properties.FACING) == facing;
	}
	
	public static boolean isPiston(BlockState state) {
		return state.getBlock() instanceof PistonBlock;
	}
	
	public static boolean isSticky(BlockState state) {
		return state.isOf(Blocks.STICKY_PISTON);
	}
	
	public static BlockState getPistonHead(boolean sticky, Direction facing) {
		return Blocks.PISTON_HEAD.getDefaultState().with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT).with(Properties.FACING, facing);
	}
	
	public static boolean isPistonHead(BlockState state, boolean sticky, Direction facing) {
		return isPistonHead(state, sticky) && state.get(Properties.FACING) == facing;
	}
	
	public static boolean isPistonHead(BlockState state, boolean sticky) {
		return isPistonHead(state) && isStickyHead(state) == sticky;
	}
	
	public static boolean isPistonHead(BlockState state, Direction facing) {
		return isPistonHead(state) && state.get(Properties.FACING) == facing;
	}
	
	public static boolean isPistonHead(BlockState state) {
		return state.isOf(Blocks.PISTON_HEAD);
	}
	
	public static boolean isStickyHead(BlockState state) {
		return state.get(Properties.PISTON_TYPE) == PistonType.STICKY;
	}
	
	public static boolean isMovedPiston(World world, BlockPos pos, BlockState state, boolean sticky, Direction facing) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				return isPiston(((RTIPistonBlockEntity)blockEntity).getMovedMovingState(), sticky, facing);
			}
		}
		
		return false;
	}
	
	public static boolean isMovedPistonHead(World world, BlockPos pos, BlockState state, boolean sticky, Direction facing) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				return isPistonHead(((RTIPistonBlockEntity)blockEntity).getMovedMovingState(), sticky, facing);
			}
		}
		
		return false;
	}
	
	// Do not use to check if piston is movable!
	public static boolean isExtended(World world, BlockPos pos, BlockState state) {
		return isExtended(world, pos, state, true);
	}
	
	// Do not use to check if piston is movable!
	public static boolean isExtended(World world, BlockPos pos, BlockState state, boolean checkExtending) {
		if (checkExtending && isExtending(world, pos, state)) {
			return false;
		}
		
		return state.get(Properties.EXTENDED);
	}
	
	public static boolean isExtending(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(Properties.FACING);
		BlockPos headPos = pos.offset(facing);
		BlockState pistonHead = world.getBlockState(headPos);
		
		return isExtendingPistonHead(world, headPos, pistonHead, isSticky(state), facing);
	}
	
	public static boolean isExtendingPistonHead(World world, BlockPos pos,BlockState state) {
		return isExtendingPistonHead(world, pos, state, null, null);
	}

	public static boolean isExtendingPistonHead(World world, BlockPos pos,BlockState state, Boolean sticky, Direction facing) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				if (pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && !((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving() && (sticky == null || sticky == ((RTIPistonBlockEntity)pistonBlockEntity).isSticky()) && (facing == null || facing == pistonBlockEntity.getFacing())) {
					return true;
				}
				
				blockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedMovingBlockEntity();
				
				if (blockEntity instanceof PistonBlockEntity) {
					pistonBlockEntity = (PistonBlockEntity)blockEntity;
					
					return pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && !((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving() && (sticky == null || sticky == ((RTIPistonBlockEntity)pistonBlockEntity).isSticky()) && (facing == null || facing == pistonBlockEntity.getFacing());
				}
			}
		}
		
		return false;
	}
	
	public static boolean isExtendingBackwards(World world, BlockPos pos,BlockState state, Direction facing) {
		if (state.isOf(Blocks.MOVING_PISTON) && state.get(Properties.FACING) == facing) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				if (pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && ((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving()) {
					return true;
				}
				
				blockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedMovingBlockEntity();
				
				if (blockEntity instanceof PistonBlockEntity) {
					pistonBlockEntity = (PistonBlockEntity)blockEntity;
					
					return pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && ((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving();
				}
			}
		}
		
		return false;
	}
	
	public static boolean isPushingBlock(World world, BlockPos pos, BlockState state, Direction dir) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				return pistonBlockEntity.isExtending() && pistonBlockEntity.getFacing() == dir;
			}
		}
		
		return false;
	}
	
	public static boolean isMovablePiston(World world, BlockPos pos, BlockState state) {
		return isMovablePiston(world, pos, state, true) || isMovablePiston(world, pos, state, false);
	}
	
	public static boolean isMovablePiston(World world, BlockPos pos, BlockState state, boolean sticky) {
		if (isPiston(state, sticky)) {
			if (!state.get(Properties.EXTENDED)) {
				return true;
			}
			if (PistonSettings.movableWhenExtended(sticky)) {
				if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() || PistonSettings.looseHead(sticky)) {
					return true;
				}
				
				Direction facing = state.get(Properties.FACING);
				BlockPos headPos = pos.offset(facing);
				BlockState pistonHead = world.getBlockState(headPos);
				
				return !isMovedPistonHead(world, headPos, pistonHead, sticky, facing);
			}
		}
		if (isPistonHead(state, sticky)) {
			if (PistonSettings.looseHead(sticky)) {
				return true;
			}
			if (PistonSettings.movableWhenExtended(sticky)) {
				if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get()) {
					return true;
				}
				
				Direction facing = state.get(Properties.FACING);
				BlockPos basePos = pos.offset(facing.getOpposite());
				BlockState pistonBase = world.getBlockState(basePos);
				
				return !isMovedPiston(world, basePos, pistonBase, sticky, facing) && (!PistonSettings.canMoveSelf(sticky) || !isExtendingBackwards(world, basePos, pistonBase, facing));
			}
		}
		
		return false;
	}
	
	public static boolean hasPistonHead(World world, BlockPos pos, boolean sticky, Direction facing) {
		BlockPos headPos = pos.offset(facing);
		BlockState pistonHead = world.getBlockState(headPos);
		
		return isPistonHead(pistonHead, sticky, facing) || isExtendingPistonHead(world, headPos, pistonHead, sticky, facing);
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing) {
		return isReceivingPower(world, pos, state, facing, false);
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state, Direction facing, boolean onBlockEvent) {
		boolean sticky = isSticky(state);
		
		for (Direction direction : Direction.values()) {
			if (PistonSettings.ignorePowerFromFront(sticky) && direction == facing) {
				continue;
			}
			
			if (world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		
		return WorldHelper.isQCPowered(world, pos, state, onBlockEvent, PistonSettings.getQC(sticky), PistonSettings.randQC(sticky));
	}
	
	public static boolean canMoveBlockEntityOf(Block block) {
		return block.hasBlockEntity() && !IMMOVABLE_BLOCKS.contains(block);
	}
	
	public static float getSoundPitch(World world, boolean extending, boolean sticky) {
		float basePitch = 0.6F + world.getRandom().nextFloat() * (extending ? 0.25F : 0.15F);
		
		return adjustSoundPitch(basePitch, extending, sticky);
	}
	
	public static float adjustSoundPitch(float pitch, boolean extending, boolean sticky) {
		int speed = PistonSettings.speed(sticky, extending);
		
		return speed == 0 ? Float.POSITIVE_INFINITY : pitch * (2.0F / speed);
	}
	
	public static PistonBehavior getPistonBehavior(BlockState state) {
		if (redstonetweaks.setting.Tweaks.Barrier.IS_MOVABLE.get() && state.isOf(Blocks.BARRIER)) {
			return PistonBehavior.NORMAL;
		}
		if (isPiston(state) && (!state.get(Properties.EXTENDED) || PistonSettings.movableWhenExtended(isSticky(state)))) {
			return PistonBehavior.NORMAL;
		}
		if (isPistonHead(state) && (PistonSettings.movableWhenExtended(isStickyHead(state)) || PistonSettings.looseHead(isStickyHead(state)))) {
			return PistonBehavior.NORMAL;
		}
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			return PistonBehavior.NORMAL;
		}
		
		return state.getPistonBehavior();
	}
	
	// Check if a piston can pull the block towards it or itself towards the block
	public static boolean canPull(BlockState state) {
		PistonBehavior behavior = getPistonBehavior(state);
		
		return behavior != PistonBehavior.PUSH_ONLY && behavior != PistonBehavior.DESTROY;
	}
	
	public static boolean canSlabStickTo(BlockState state, Direction dir) {
		if (Tweaks.Global.MERGE_SLABS.get() && dir.getAxis().isVertical()) {
			SlabType type = state.get(SlabBlock.TYPE);
			
			if (type == SlabHelper.getTypeFromDirection(dir.getOpposite())) {
				return false;
			}
		}
		
		return true;
	}
	
	// The block state that will be used to calculate the movement of a piston
	public static BlockState getStateForMovement(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				state = ((RTIPistonBlockEntity)blockEntity).getStateToMove();
			}
		}
		
		return state;
	}
	
	public static BlockEntity getBlockEntityToMove(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (blockEntity != null) {
			world.removeBlockEntity(pos);
			
			// Fix for disappearing block entities on the client
			if (world.isClient()) {
				blockEntity.markDirty();
			}
		}
		
		return blockEntity;
	}
	
	public static MovedBlock trySplitDoubleSlab(World world, BlockPos pos, BlockState movedState, BlockEntity blockEntity, SlabType movedType) {
		if (SlabHelper.isSlab(movedState)) {
			world.setBlockState(pos, movedState.with(Properties.SLAB_TYPE, SlabHelper.getOppositeType(movedType)), 82);
			
			movedState = movedState.with(Properties.SLAB_TYPE, movedType);
		} else if (movedState.isOf(Blocks.MOVING_PISTON) && blockEntity != null && blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity remainingBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			PistonBlockEntity movedBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			
			MovedBlock remainingBlock = ((RTIPistonBlockEntity)remainingBlockEntity).splitDoubleSlab(SlabHelper.getOppositeType(movedType));
			MovedBlock movedBlock = ((RTIPistonBlockEntity)movedBlockEntity).splitDoubleSlab(movedType);
			
			WorldHelper.setBlockWithEntity(world, pos, remainingBlock.getBlockState(), remainingBlock.getBlockEntity(), 82);
			
			return movedBlock;
		}
		
		return new MovedBlock(movedState, blockEntity);
	}
	
	// Notify clients of any pistons that are about to be "double retracted"
	public static void prepareDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			if (isPiston(state) && !state.get(Properties.EXTENDED) && ((RTIServerWorld)world).hasBlockEvent(pos, MotionType.RETRACT_A, MotionType.RETRACT_B, MotionType.RETRACT_FORWARDS)) {
				BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
				((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
			}
		}
	}
	
	// This fixes some cases of pistons disappearing on clients
	public static void cancelDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			if (isPiston(state) && !state.get(Properties.EXTENDED)) {
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), 16);
				
				BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
				((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
			}
		}
	}
	
	public static boolean isPotentiallySticky(BlockState state) {
		return isPotentiallySticky(state.getBlock());
	}
	
	public static boolean isPotentiallySticky(Block block) {
		if (block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK) {
			return true;
		}
		if (Tweaks.StickyPiston.SUPER_STICKY.get() && (block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		if (Tweaks.Global.CHAINSTONE.get() && block == Blocks.CHAIN) {
			return true;
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() && (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)) {
			return true;
		}
		if (Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.get() && (block == Blocks.PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		if (Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.get() && (block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		
		return false;
	}
	
	// dir is the direction from pos towards adjacentPos
	public static boolean isAdjacentBlockStuck(World world, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		if (adjacentState.isAir()) {
			return false;
		}
		if (SlabHelper.isSlab(adjacentState) && !PistonHelper.canSlabStickTo(adjacentState, dir.getOpposite()))
			return false;
		
		// Default vanilla implementation: slime and honey do not stick to each other
		if (state.isOf(Blocks.SLIME_BLOCK) && !adjacentState.isOf(Blocks.HONEY_BLOCK)) {
			return true;
		}
		if (state.isOf(Blocks.HONEY_BLOCK) && !adjacentState.isOf(Blocks.SLIME_BLOCK)) {
			return true;
		}
		
		if (Tweaks.StickyPiston.SUPER_STICKY.get()) {
			if ((PistonHelper.isPiston(state, true, dir) && !state.get(Properties.EXTENDED) && (world.isClient() || !((RTIServerWorld) world).hasBlockEvent(pos, MotionType.RETRACT_A, MotionType.RETRACT_B, MotionType.RETRACT_FORWARDS))) || PistonHelper.isPistonHead(state, true, dir)) {
				return true;
			}
		}
		if (Tweaks.Global.CHAINSTONE.get() && state.isOf(Blocks.CHAIN)) {
			Direction.Axis axis = state.get(Properties.AXIS);
			
			if (axis == dir.getAxis()) {
				// Identify situations where the adjacent block is definitely not pulled along
				if (adjacentState.isOf(Blocks.CHAIN)) {
					if (adjacentState.get(Properties.AXIS) != axis) {
						return false;
					}
				} else if (!Block.sideCoversSmallSquare(world, adjacentPos, dir.getOpposite())) {
					return false;
				}
				
				return isFullyAnchoredChain(world, pos, axis);
			}
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() && (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST))) {
			ChestType chestType = state.get(Properties.CHEST_TYPE);
			
			if (chestType == ChestType.SINGLE) {
				return false;
			}
			
			if (adjacentState.isOf(state.getBlock()) && adjacentState.get(Properties.CHEST_TYPE) == chestType.getOpposite()) {
				Direction facing = state.get(Properties.HORIZONTAL_FACING);
				
				if (chestType == ChestType.LEFT) {
					return (dir == facing.rotateYClockwise());
				}
				
				return (dir == facing.rotateYCounterclockwise());
			}
		}
		for (boolean sticky : new boolean[] { false, true }) {
			if (PistonSettings.movableWhenExtended(sticky) && !PistonSettings.looseHead(sticky)) {
				if (PistonHelper.isPiston(state, sticky, dir)) {
					if (PistonHelper.isExtended(world, pos, state, false) && (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() || !PistonHelper.isExtending(world, pos, state)) && PistonHelper.isPistonHead(adjacentState, sticky, dir)) {
						return true;
					}
				} else if (PistonHelper.isPistonHead(state, sticky, dir.getOpposite())) {
					if (PistonHelper.isPiston(adjacentState, sticky, dir.getOpposite())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean isFullyAnchoredChain(World world, BlockPos pos, Direction.Axis axis) {
		for (Direction.AxisDirection side : Direction.AxisDirection.values()) {
			Direction dir = Direction.from(axis, side);
			
			BlockPos sidePos = pos.offset(dir);
			BlockState sideState = PistonHelper.getStateForMovement(world, sidePos);
			
			while(sideState.isOf(Blocks.CHAIN)) {
				if (sideState.get(Properties.AXIS) != axis) {
					return false;
				}
				
				sidePos = sidePos.offset(dir);
				sideState = PistonHelper.getStateForMovement(world, sidePos);
			}
			
			if (!Block.sideCoversSmallSquare(world, sidePos, dir.getOpposite())) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void tryMove(World world, BlockPos pos, BlockState state, boolean sticky, boolean extended, boolean onScheduledTick) {
		if (((RTIWorld)world).hasBlockEventHandler(pos)) {
			return;
		}
		
		Direction facing = state.get(Properties.FACING);
		int delay;
		boolean lazy;
		if (extended) {
			delay = PistonSettings.delayFallingEdge(sticky);
			lazy = PistonSettings.lazyFallingEdge(sticky);
		} else {
			delay = PistonSettings.delayRisingEdge(sticky);
			lazy = PistonSettings.lazyRisingEdge(sticky);
		}
		boolean powered = PistonHelper.isReceivingPower(world, pos, state, facing);
		
		boolean shouldExtend = (onScheduledTick && lazy) ? !extended : powered;
		
		if (shouldExtend && !extended) {
			int type = createPistonHandler(world, pos, facing, true, sticky).calculatePush() ? MotionType.EXTEND : ((PistonSettings.canMoveSelf(sticky) && createPistonHandler(world, pos, facing.getOpposite(), true, sticky).calculatePush()) ? MotionType.EXTEND_BACKWARDS : MotionType.NONE);
			
			if (type == MotionType.NONE) {
				if (powered && PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityRisingEdge(sticky));
				}
			} else {
				if (delay == 0 || onScheduledTick) {
					world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
				} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, PistonSettings.tickPriorityRisingEdge(sticky));
				}
			}
		} else if (!shouldExtend && extended && (!PistonSettings.looseHead(sticky) || PistonHelper.hasPistonHead(world, pos, sticky, facing))) {
			int type = MotionType.RETRACT_A;
			
			if (sticky && PistonSettings.canMoveSelf(sticky)) {
				BlockPos frontPos = pos.offset(facing, 2);
				BlockState frontState = world.getBlockState(frontPos);
				
				if (!PistonHelper.isPushingBlock(world, frontPos, frontState, facing) && PistonHelper.canPull(frontState) && !(PistonBlock.isMovable(frontState, world, frontPos, facing.getOpposite(), false, facing) && createPistonHandler(world, pos, facing, false, sticky).calculatePush())) {
					type = MotionType.RETRACT_FORWARDS;
				}
			}
			
			if (delay == 0 || onScheduledTick) {
				if (Tweaks.Global.DOUBLE_RETRACTION.get()) {
					state = getPiston(sticky, facing, false);
					
					world.setBlockState(pos, state, 16);
				}
				
				world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
			} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, PistonSettings.tickPriorityFallingEdge(sticky));
			}
		}
		
		if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
			updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
		}
	}
	
	public static void updateAdjacentRedstoneTorches(World world, BlockPos pos, Block block) {
		if (!world.isDebugWorld()) {
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				
				if (world.getBlockState(neighborPos).getBlock() instanceof RedstoneTorchBlock) {
					world.updateNeighbor(neighborPos, block, pos);
				}
			}
		}
	}
}