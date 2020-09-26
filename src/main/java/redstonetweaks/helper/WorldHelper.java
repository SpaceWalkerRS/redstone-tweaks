package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import redstonetweaks.piston.BlockEventHandler;
import redstonetweaks.settings.Settings;
import redstonetweaks.settings.types.DirectionalBooleanSetting;

public interface WorldHelper {
	
	public BlockEventHandler getPistonBlockEventHandler();
	
	public void startTickingBlockEntities(boolean startIterating);
	
	public boolean tryContinueTickingBlockEntities();
	
	public void finishTickingBlockEntities(Profiler profiler);
	
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler);
	
	public boolean tickWorldsNormally();
	
	public boolean updateNeighborsNormally();
	
	public static BlockState getStateForPower(World world, BlockPos pos, Direction direction) {
		BlockState state = world.getBlockState(pos);
		if (Settings.MagentaGlazedTerracotta.IS_POWER_DIODE.get()) {
			if (state.isOf(Blocks.REDSTONE_WIRE)) {
				BlockState downState = world.getBlockState(pos.down());
				if (downState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
					if (downState.get(Properties.HORIZONTAL_FACING).getOpposite() != direction) {
						return Blocks.AIR.getDefaultState();
					}
				}
			}
		}
		return state;
	}
	
	public static boolean isQCPowered(World world, BlockPos pos, BlockState state, boolean forceCheck, DirectionalBooleanSetting qc, boolean randQC) {
		for (Direction dir : Direction.values()) {
			if (qc.get(dir)) {
				if (forceCheck || !randQC || world.getRandom().nextBoolean()) {
					if (world.isReceivingRedstonePower(pos.offset(dir))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
