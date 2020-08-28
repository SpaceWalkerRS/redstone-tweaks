package redstonetweaks.helper;

import static redstonetweaks.setting.SettingsManager.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import redstonetweaks.piston.BlockEventHandler;
import redstonetweaks.setting.SettingsPack;

public interface WorldHelper {
	
	public BlockEventHandler getPistonBlockEventHandler();
	
	public void startTickingBlockEntities(boolean startIterating);
	
	public boolean tryContinueTickingBlockEntities();
	
	public void finishTickingBlockEntities(Profiler profiler);
	
	public void tickBlockEntity(BlockEntity blockEntity, Profiler profiler);
	
	public boolean shouldSeparateWorldTick();
	
	public boolean shouldSeparateUpdates();
	
	public static BlockState getStateForPower(World world, BlockPos pos, Direction direction) {
		BlockState state = world.getBlockState(pos);
		if (MAGENTA_GLAZED_TERRACOTTA.get(IS_POWER_DIODE)) {
			if (state.isOf(Blocks.REDSTONE_WIRE)) {
				BlockState downState = world.getBlockState(pos.down());
				if (downState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
					return downState.get(Properties.HORIZONTAL_FACING).getOpposite() == direction ? state : Blocks.AIR.getDefaultState();
				}
			}
		}
		return state;
	}
	
	public static boolean isQCPowered(World world, BlockPos pos, BlockState state, boolean forceCheck) {
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(state.getBlock());
		if (settings != null) {
			boolean randQC = settings.get(RANDOMIZE_QC);
			for (Direction direction : Direction.values()) {
				if (settings.get(DIRECTION_TO_QC_SETTING.get(direction))) {
					if (forceCheck || !randQC || world.random.nextBoolean()) {
						if (world.isReceivingRedstonePower(pos.offset(direction))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
