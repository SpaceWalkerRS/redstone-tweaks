package redstonetweaks.helper;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.mixinterfaces.RTIPistonBlockEntity;
import redstonetweaks.setting.Tweaks;

public class PistonHelper {
	
	private static final List<Block> IMMOVABLE_BLOCK_ENTITIES = Arrays.asList(
		Blocks.MOVING_PISTON,
		Blocks.BEACON,
		Blocks.ENCHANTING_TABLE,
		Blocks.ENDER_CHEST,
		Blocks.END_GATEWAY,
		Blocks.END_PORTAL
	);
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		return createPistonBlockEntity(pushedBlockState, null, pistonDir, extending, isSource, isMovedByStickyPiston, false ,false);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, BlockEntity pushedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston, boolean isMergingSlabs, boolean sourceIsMoving) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlockState, pistonDir, extending, isSource);
		
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(isMovedByStickyPiston);
		((RTIPistonBlockEntity)pistonBlockEntity).setPushedBlockEntity(pushedBlockEntity);
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMergingSlabs(isMergingSlabs);
		((RTIPistonBlockEntity)pistonBlockEntity).setSourceIsMoving(sourceIsMoving);
		
		return pistonBlockEntity;
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
				return isPiston(((RTIPistonBlockEntity)blockEntity).getMovedState(), sticky, facing);
			}
		}
		
		return false;
	}
	
	public static boolean isMovedPistonHead(World world, BlockPos pos, BlockState state, boolean sticky, Direction facing) {
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				return isPistonHead(((RTIPistonBlockEntity)blockEntity).getMovedState(), sticky, facing);
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
				
				if (pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && !((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving() && (sticky == null || sticky == ((RTIPistonBlockEntity)pistonBlockEntity).isMovedByStickyPiston()) && (facing == null || facing == pistonBlockEntity.getFacing())) {
					return true;
				}
				
				blockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedBlockEntity();
				
				if (blockEntity instanceof PistonBlockEntity) {
					pistonBlockEntity = (PistonBlockEntity)blockEntity;
					
					return pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && !((RTIPistonBlockEntity)pistonBlockEntity).sourceIsMoving() && (sticky == null || sticky == ((RTIPistonBlockEntity)pistonBlockEntity).isMovedByStickyPiston()) && (facing == null || facing == pistonBlockEntity.getFacing());
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
				
				blockEntity = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedBlockEntity();
				
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
		for (Direction direction : Direction.values()) {
			if (direction != facing && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
				return true;
			}
		}
		
		boolean sticky = isSticky(state);
		
		return WorldHelper.isQCPowered(world, pos, state, onBlockEvent, PistonSettings.getQC(sticky), PistonSettings.randQC(sticky));
	}
	
	public static boolean canMoveBlockEntityOf(Block block) {
		return block.hasBlockEntity() && !IMMOVABLE_BLOCK_ENTITIES.contains(block);
	}
	
	public static float getSoundPitch(World world, boolean extending, boolean sticky) {
		float basePitch = 0.6F + world.getRandom().nextFloat() * (extending ? 0.25F : 0.15F);
		
		return adjustSoundPitch(basePitch, extending, sticky);
	}
	
	public static float adjustSoundPitch(float pitch, boolean extending, boolean sticky) {
		int speed = extending ? PistonSettings.speedRisingEdge(sticky) : PistonSettings.speedFallingEdge(sticky);
		
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
	
	public static BlockState getStateToMove(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				state = ((RTIPistonBlockEntity)blockEntity).getMovedState();
			}
		}
		
		return state;
	}

	// Notify clients of any pistons that are about to be "double retracted"
	public static void prepareDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			if (isPiston(state) && !state.get(Properties.EXTENDED)) {
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
}