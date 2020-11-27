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
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;

public class PistonHelper {
	
	private static final List<Block> IMMOVABLE_BLOCK_ENTITIES = Arrays.asList(
		Blocks.MOVING_PISTON,
		Blocks.BEACON,
		Blocks.ENCHANTING_TABLE,
		Blocks.ENDER_CHEST,
		Blocks.END_GATEWAY,
		Blocks.END_PORTAL
	);
	
	// Notify clients of any pistons that are about to be "double retracted"
	public static void prepareDoubleRetraction(World world, BlockPos pos, BlockState state) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			if (state.getBlock() instanceof PistonBlock && !state.get(Properties.EXTENDED)) {
				BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, pos);
				((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.getRegistryKey(), packet);
			}
		}
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		return createPistonBlockEntity(pushedBlockState, null, pistonDir, extending, isSource, isMovedByStickyPiston);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, BlockEntity pushedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		return createPistonBlockEntity(pushedBlockState, pushedBlockEntity, pistonDir, extending, isSource, isMovedByStickyPiston, false);
	}
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState movedState, BlockEntity movedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston, boolean isMergingSlabs) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(movedState, pistonDir, extending, isSource);
		
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(isMovedByStickyPiston);
		((RTIPistonBlockEntity)pistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMergingSlabs(isMergingSlabs);
		
		return pistonBlockEntity;
	}
	
	public static boolean isSticky(BlockState state) {
		return state.isOf(Blocks.STICKY_PISTON);
	}
	
	public static boolean isExtended(World world, BlockPos pos, BlockState state) {
		return isExtended(world, pos, state, state.get(Properties.FACING));
	}
	
	public static boolean isExtended(World world, BlockPos pos, BlockState state, Direction facing) {
		boolean isExtended = state.get(Properties.EXTENDED) && !isExtending(world, pos, state, facing);
		if (!isExtended && Tweaks.Global.DOUBLE_RETRACTION.get()) {
			BlockState frontState = world.getBlockState(pos.offset(facing));
			isExtended = frontState.isOf(Blocks.PISTON_HEAD) && frontState.get(Properties.FACING) == facing;
		}
		
		return isExtended;
	}
	
	// The base of an extending piston is a piston block with the EXTENDED property set to true,
	// the same as an extended piston. So to determine whether the piston is extending, we need
	// to look at the block in front of the piston. If that block is a moving block that is
	// extending and facing the same direction as the piston, then we can conclude that the piston
	// is extending.
	public static boolean isExtending(World world, BlockPos pos, BlockState state, Direction facing) {
		BlockPos frontPos = pos.offset(facing);
		BlockState frontState = world.getBlockState(frontPos);
		
		if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
			BlockEntity blockEntity = world.getBlockEntity(frontPos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				return pistonBlockEntity.isExtending() && pistonBlockEntity.isSource();
			}
		}
		return false;
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
	
	public static float getSoundPitch(World world, boolean extending, boolean sticky) {
		float basePitch = 0.6F + world.getRandom().nextFloat() * (extending ? 0.25F : 0.15F);
		
		return adjustSoundPitch(basePitch, extending, sticky);
	}
	
	public static float adjustSoundPitch(float pitch, boolean extending, boolean sticky) {
		int speed = extending ? speedRisingEdge(sticky) : speedFallingEdge(sticky);
		
		return speed == 0 ? Float.POSITIVE_INFINITY : pitch * (2.0F / speed);
	}
	
	public static PistonBehavior getPistonBehavior(BlockState state) {
		if (redstonetweaks.setting.Tweaks.Barrier.IS_MOVABLE.get() && state.isOf(Blocks.BARRIER)) {
			return PistonBehavior.NORMAL;
		}
		if (movableWhenExtended(false) && state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.DEFAULT) {
			return PistonBehavior.NORMAL;
		}
		if (movableWhenExtended(true) && state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.STICKY) {
			return PistonBehavior.NORMAL;
		}
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			return PistonBehavior.NORMAL;
		}
		
		return state.getPistonBehavior();
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
	
	public static boolean doBlockDropping() {
		return Tweaks.StickyPiston.DO_BLOCK_DROPPING.get();
	}
	
	public static boolean fastBlockDropping() {
		return doBlockDropping() && Tweaks.StickyPiston.FAST_BLOCK_DROPPING.get();
	}
	
	public static DirectionToBooleanSetting getQC(BlockState state) {
		return isSticky(state) ? Tweaks.StickyPiston.QC : Tweaks.NormalPiston.QC;
	}
	
	public static boolean randQC(BlockState state) {
		return isSticky(state) ? Tweaks.StickyPiston.RANDOMIZE_QC.get() : Tweaks.NormalPiston.RANDOMIZE_QC.get();
	}
	
	public static boolean connectsToWire(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.CONNECTS_TO_WIRE.get() : Tweaks.NormalPiston.CONNECTS_TO_WIRE.get();
	}
	
	public static int delayRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.DELAY_RISING_EDGE.get() : Tweaks.NormalPiston.DELAY_RISING_EDGE.get();
	}
	
	public static int delayFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.DELAY_FALLING_EDGE.get() : Tweaks.NormalPiston.DELAY_FALLING_EDGE.get();
	}
	
	public static boolean ignoreUpdatesWhileExtending(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_EXTENDING.get() : Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_EXTENDING.get();
	}
	
	public static boolean ignoreUpdatesWhileRetracting(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.IGNORE_UPDATES_WHILE_RETRACTING.get() : Tweaks.NormalPiston.IGNORE_UPDATES_WHILE_RETRACTING.get();
	}
	
	public static boolean lazyRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.LAZY_RISING_EDGE.get() : Tweaks.NormalPiston.LAZY_RISING_EDGE.get();
	}
	
	public static boolean lazyFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.LAZY_FALLING_EDGE.get() : Tweaks.NormalPiston.LAZY_FALLING_EDGE.get();
	}
	
	public static boolean movableWhenExtended(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.get() : Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.get();
	}
	
	public static int speedRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.SPEED_RISING_EDGE.get() : Tweaks.NormalPiston.SPEED_RISING_EDGE.get();
	}
	
	public static int speedFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.SPEED_FALLING_EDGE.get() : Tweaks.NormalPiston.SPEED_FALLING_EDGE.get();
	}
	
	public static boolean headUpdatesOnExtension(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.HEAD_UPDATES_ON_EXTENSION.get() : Tweaks.NormalPiston.HEAD_UPDATES_ON_EXTENSION.get();
	}
	
	public static TickPriority tickPriorityRisingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.TICK_PRIORITY_RISING_EDGE.get() : Tweaks.NormalPiston.TICK_PRIORITY_RISING_EDGE.get();
	}
	
	public static TickPriority tickPriorityFallingEdge(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.NormalPiston.TICK_PRIORITY_FALLING_EDGE.get();
	}
	
	public static boolean updateSelfWhilePowered(boolean sticky) {
		return sticky ? Tweaks.StickyPiston.UPDATE_SELF_WHILE_POWERED.get() : Tweaks.NormalPiston.UPDATE_SELF_WHILE_POWERED.get();
	}
}