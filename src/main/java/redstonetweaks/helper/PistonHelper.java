package redstonetweaks.helper;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

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
	
	public static PistonBlockEntity createPistonBlockEntity(BlockState pushedBlockState, BlockEntity pushedBlockEntity, Direction pistonDir, boolean extending, boolean isSource, boolean isMovedByStickyPiston) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlockState, pistonDir, extending, isSource);
		
		((PistonBlockEntityHelper)pistonBlockEntity).setIsMovedByStickyPiston(isMovedByStickyPiston);
		((PistonBlockEntityHelper)pistonBlockEntity).setPushedBlockEntity(pushedBlockEntity);
		
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
}