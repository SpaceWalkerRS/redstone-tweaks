package redstonetweaks.helper;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.block.piston.MotionType;
import redstonetweaks.block.piston.MovedBlock;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPistonHandler;
import redstonetweaks.interfaces.mixin.RTIPlant;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

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
	
	// Blocks vegetation can be placed on
	private static final Set<Block> SOIL_BLOCKS = ImmutableSet.of(
		Blocks.GRASS_BLOCK,
		Blocks.DIRT,
		Blocks.COARSE_DIRT,
		Blocks.PODZOL,
		Blocks.FARMLAND,
		Blocks.SAND,
		Blocks.RED_SAND,
		Blocks.TERRACOTTA,
		Blocks.WHITE_TERRACOTTA,
		Blocks.ORANGE_TERRACOTTA,
		Blocks.MAGENTA_TERRACOTTA,
		Blocks.LIGHT_BLUE_TERRACOTTA,
		Blocks.YELLOW_TERRACOTTA,
		Blocks.LIME_TERRACOTTA,
		Blocks.PINK_TERRACOTTA,
		Blocks.GRAY_TERRACOTTA,
		Blocks.LIGHT_GRAY_TERRACOTTA,
		Blocks.CYAN_TERRACOTTA,
		Blocks.PURPLE_TERRACOTTA,
		Blocks.BLUE_TERRACOTTA,
		Blocks.BROWN_TERRACOTTA,
		Blocks.GREEN_TERRACOTTA,
		Blocks.RED_TERRACOTTA,
		Blocks.BLACK_TERRACOTTA
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
		return getPistonHead(sticky, facing, false);
	}
	
	public static BlockState getPistonHead(boolean sticky, Direction facing, boolean shortArm) {
		return Blocks.PISTON_HEAD.getDefaultState().with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT).with(Properties.FACING, facing).with(Properties.SHORT, shortArm);
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
	
	public static boolean isReceivingPower(World world, BlockPos pos, BlockState state) {
		return isReceivingPower(world, pos, isSticky(state), state.get(Properties.FACING), false);
	}
	
	public static boolean isReceivingPower(World world, BlockPos pos, boolean sticky, Direction facing, boolean onBlockEvent) {
		boolean ignorePowerFromFront = PistonSettings.ignorePowerFromFront(sticky);
		
		for (Direction direction : Direction.values()) {
			if (ignorePowerFromFront && direction == facing) {
				continue;
			}
			
			if (world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		
		return WorldHelper.isQCPowered(world, pos, onBlockEvent, PistonSettings.getQC(sticky), PistonSettings.randQC(sticky));
	}
	
	public static boolean canMoveBlockEntityOf(Block block) {
		return block.hasBlockEntity() && !IMMOVABLE_BLOCKS.contains(block);
	}
	
	public static boolean launchesEntities(Block block) {
		return block == Blocks.SLIME_BLOCK;
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
		if (redstonetweaks.setting.settings.Tweaks.Barrier.IS_MOVABLE.get() && state.isOf(Blocks.BARRIER)) {
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
		if (Tweaks.Global.MOVABLE_BRITTLE_BLOCKS.get() && state.getPistonBehavior() == PistonBehavior.DESTROY) {
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
		if (((RTIWorld)world).hasBlockEventHandler(pos)) {
			return Blocks.BEDROCK.getDefaultState();
		}
		
		BlockState state = world.getBlockState(pos);
		
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				state = ((RTIPistonBlockEntity)blockEntity).getStateForMovement();
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
	
	// Notify clients of any pistons that are about to be "double retracted"
	public static boolean prepareDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (!world.isClient() && isPiston(state) && !state.get(Properties.EXTENDED)) {
			if (doDoubleRetraction(isSticky(state)) && ((RTIServerWorld)world).hasBlockEvent(pos, state.getBlock(), MotionType.RETRACT_A, MotionType.RETRACT_B, MotionType.RETRACT_FORWARDS)) {
				BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
				((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
				
				return true;
			}
		}
		
		return false;
	}
	
	// This fixes some cases of pistons disappearing on clients
	public static void cancelDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (!world.isClient() && isPiston(state) && !state.get(Properties.EXTENDED) && doDoubleRetraction(isSticky(state))) {
			world.setBlockState(pos, state.with(Properties.EXTENDED, true), 16);
			
			BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
			((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
		}
	}
	
	public static MovedBlock splitDoubleSlab(World world, BlockPos pos, BlockState movedState, BlockEntity blockEntity, SlabType movedType) {
		if (SlabHelper.isSlab(movedState)) {
			world.setBlockState(pos, movedState.with(Properties.SLAB_TYPE, SlabHelper.getOppositeType(movedType)), 82);
			
			movedState = movedState.with(Properties.SLAB_TYPE, movedType);
		} else if (movedState.isOf(Blocks.MOVING_PISTON) && blockEntity != null && blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity remainingBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			PistonBlockEntity movedBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			
			MovedBlock remainingBlock = ((RTIPistonBlockEntity)remainingBlockEntity).splitDoubleSlab(SlabHelper.getOppositeType(movedType));
			MovedBlock movedBlock = ((RTIPistonBlockEntity)movedBlockEntity).splitDoubleSlab(movedType);
			
			WorldHelper.setBlockWithEntity(world, pos, remainingBlock, 82);
			
			return movedBlock;
		}
		
		return new MovedBlock(movedState, blockEntity);
	}
	
	public static MovedBlock detachPistonHead(World world, BlockPos pos, BlockState movedState, BlockEntity blockEntity, Direction motionDir) {
		if (isPiston(movedState)) {
			Direction facing = movedState.get(Properties.FACING);
			boolean sticky = isSticky(movedState);
			boolean moveHead = facing == motionDir;
			
			BlockState base = movedState.with(Properties.EXTENDED, true);
			BlockState head = getPistonHead(sticky, facing).with(Properties.SHORT, moveHead);
			
			world.setBlockState(pos, moveHead ? base : head, 82);
			
			movedState = moveHead ? head : base;
		} else if (movedState.isOf(Blocks.MOVING_PISTON) && blockEntity != null && blockEntity instanceof PistonBlockEntity) {
			PistonBlockEntity remainingBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			PistonBlockEntity movedBlockEntity = ((RTIPistonBlockEntity)blockEntity).copy();
			
			MovedBlock remainingBlock = ((RTIPistonBlockEntity)remainingBlockEntity).detachPistonHead(motionDir, false);
			MovedBlock movedBlock = ((RTIPistonBlockEntity)movedBlockEntity).detachPistonHead(motionDir, true);
			
			WorldHelper.setBlockWithEntity(world, pos, remainingBlock, 82);
			
			return movedBlock;
		}
		
		return new MovedBlock(movedState, blockEntity);
	}
	
	public static MovedBlock attachPistonHead(World world, BlockPos pos, BlockState movedState, BlockEntity blockEntity, Direction motionDir) {
		if (movedState.isOf(Blocks.MOVING_PISTON) && blockEntity != null && blockEntity instanceof PistonBlockEntity) {
			return new MovedBlock(((PistonBlockEntity)blockEntity).getPushedBlock(), null);
		}
		
		return new MovedBlock(movedState, blockEntity);
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
		if (Tweaks.Global.MOVABLE_BRITTLE_BLOCKS.get() && (block instanceof DoorBlock || block instanceof TallPlantBlock || SOIL_BLOCKS.contains(block))) {
			return true;
		}
		if (Tweaks.Global.STICKY_CONNECTIONS.get() && (block instanceof HorizontalConnectingBlock || block instanceof WallBlock)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isAdjacentBlockStuck(World world, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		return isAdjacentBlockStuck(world, pos, state, adjacentPos, adjacentState, dir, true, null, null, null);
	}
	
	// dir is the direction from pos towards adjacentPos
	public static boolean isAdjacentBlockStuck(World world, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState, Direction dir, boolean extend, Direction motionDir, BlockPos sourcePos, BlockPos headPos) {
		if (adjacentPos.equals(sourcePos) || adjacentState.isAir()) {
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
			if ((PistonHelper.isPiston(state, true, dir) && !state.get(Properties.EXTENDED)) || PistonHelper.isPistonHead(state, true, dir)) {
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
				
				return isFullyAnchoredChain(world, pos, axis, extend, motionDir, sourcePos, headPos);
			}
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() && (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST))) {
			ChestType chestType = state.get(Properties.CHEST_TYPE);
			
			if (chestType == ChestType.SINGLE) {
				return false;
			}
			
			if (adjacentState.isOf(state.getBlock()) && adjacentState.get(Properties.CHEST_TYPE) == chestType.getOpposite()) {
				Direction facing = state.get(Properties.HORIZONTAL_FACING);
				
				return chestType == ChestType.LEFT ? dir == facing.rotateYClockwise() : dir == facing.rotateYCounterclockwise();
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
		if (Tweaks.Global.MOVABLE_BRITTLE_BLOCKS.get()) {
			if (state.contains(Properties.DOUBLE_BLOCK_HALF) && adjacentState.isOf(state.getBlock())) {
				if (dir.getAxis().isHorizontal()) {
					return false;
				}
				
				DoubleBlockHalf half = state.get(Properties.DOUBLE_BLOCK_HALF);
				
				if (half == adjacentState.get(Properties.DOUBLE_BLOCK_HALF)) {
					return false;
				}
				if ((dir == Direction.UP) == (half == DoubleBlockHalf.UPPER)) {
					return false;
				}
				
				return true;
			} else
			if (adjacentState.getBlock() instanceof PlantBlock) {
				return ((RTIPlant)adjacentState.getBlock()).hasAttachmentTo(adjacentState, dir.getOpposite(), state.getBlock());
			} else
			if (state.getBlock().isIn(BlockTags.JUNGLE_LOGS) && adjacentState.isOf(Blocks.COCOA)) {
				if (dir.getAxis().isVertical()) {
					return false;
				}
				
				return dir.getOpposite() == adjacentState.get(Properties.HORIZONTAL_FACING);
			} else
			if (state.getBlock() instanceof VineBlock && adjacentState.isOf(state.getBlock())) {
				return dir.getAxis().isVertical();
			}
		}
		if (Tweaks.Global.STICKY_CONNECTIONS.get()) {
			Block block = state.getBlock();
			
			if (block instanceof HorizontalConnectingBlock || block instanceof WallBlock) {
				if (adjacentState.isOf(block)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isFullyAnchoredChain(World world, BlockPos pos, Direction.Axis axis, boolean extend, Direction motionDir, BlockPos sourcePos, BlockPos headPos) {
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
			
			if (sourcePos != null && sidePos.equals(sourcePos)) {
				return false;
			}
			
			if (!Block.sideCoversSmallSquare(world, sidePos, dir.getOpposite())) {
				if (extend || !sidePos.equals(headPos) || motionDir == null || axis != motionDir.getAxis()) {
					return false;
				}
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
		TickPriority tickPriority;
		boolean lazy;
		if (extended) {
			delay = PistonSettings.delayFallingEdge(sticky);
			tickPriority = PistonSettings.tickPriorityFallingEdge(sticky);
			lazy = PistonSettings.lazyFallingEdge(sticky);
		} else {
			delay = PistonSettings.delayRisingEdge(sticky);
			tickPriority = PistonSettings.tickPriorityRisingEdge(sticky);
			lazy = PistonSettings.lazyRisingEdge(sticky);
		}
		
		BlockPos frontPos = pos.offset(facing, extended ? 2 : 1);
		BlockState frontState = world.getBlockState(frontPos);
		
		delay = ((RTIAbstractBlockState)frontState).delayOverride(delay);
		tickPriority = ((RTIAbstractBlockState)frontState).tickPriorityOverride(tickPriority);
		
		boolean powered = PistonHelper.isReceivingPower(world, pos, sticky, facing, false);
		boolean shouldExtend = (onScheduledTick && lazy) ? !extended : powered;
		
		if (shouldExtend && !extended) {
			int type;
			if (isPiston(state)) {
				type = createPistonHandler(world, pos, facing, true, sticky).calculatePush() ? MotionType.EXTEND : ((PistonSettings.canMoveSelf(sticky) && createPistonHandler(world, pos, facing.getOpposite(), true, sticky).calculatePush()) ? MotionType.EXTEND_BACKWARDS : MotionType.NONE);
			} else {
				type = MotionType.EXTEND;
			}
			
			if (type == MotionType.NONE) {
				if (powered && PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, tickPriority);
				}
			} else {
				if (onScheduledTick) {
					world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
				} else if (delay == 0 || Tweaks.Global.DELAY_MULTIPLIER.get() == 0 || !((RTIServerWorld)world).hasBlockEvent(pos, state.getBlock())) {
					TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, tickPriority);
				}
			}
		} else if (!shouldExtend && extended && (!PistonSettings.looseHead(sticky) || PistonHelper.hasPistonHead(world, pos, sticky, facing))) {
			int type = MotionType.RETRACT_A;
			
			if (sticky && PistonSettings.canMoveSelf(sticky)) {
				if (!PistonHelper.isPushingBlock(world, frontPos, frontState, facing) && PistonHelper.canPull(frontState) && !(PistonBlock.isMovable(frontState, world, frontPos, facing.getOpposite(), false, facing) && createPistonHandler(world, pos, facing, false, sticky).calculatePush())) {
					type = MotionType.RETRACT_FORWARDS;
				}
			}
			
			if (onScheduledTick) {
				if (PistonHelper.doDoubleRetraction(sticky)) {
					state = getPiston(sticky, facing, false);
					
					world.setBlockState(pos, state, 16);
				}
				
				world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
			} else if (delay == 0 || Tweaks.Global.DELAY_MULTIPLIER.get() == 0 || !((RTIServerWorld)world).hasBlockEvent(pos, state.getBlock())) {
				TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, tickPriority);
			}
		}
		
		if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
			updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
		}
	}
	
	public static void updateAdjacentRedstoneTorches(World world, BlockPos pos, Block block) {
		for (Direction direction : Direction.values()) {
			BlockPos neighborPos = pos.offset(direction);
			
			if (world.getBlockState(neighborPos).getBlock() instanceof RedstoneTorchBlock) {
				world.updateNeighbor(neighborPos, block, pos);
			}
		}
	}
	
	public static boolean doDoubleRetraction(boolean sticky) {
		return Tweaks.Global.DOUBLE_RETRACTION.get() && !PistonSettings.looseHead(sticky);
	}
}