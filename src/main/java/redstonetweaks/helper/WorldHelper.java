package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldHelper {
	
	public static boolean isPowered(World world, BlockPos pos, BlockState state, boolean forceCheckQC, DirectionToBooleanSetting qc, boolean randQC) {
		return world.isReceivingRedstonePower(pos) || isQCPowered(world, pos, state, forceCheckQC, qc, randQC);
	}
	
	public static boolean isQCPowered(World world, BlockPos pos, BlockState state, boolean forceCheck, DirectionToBooleanSetting qc, boolean randQC) {
		for (Direction dir : Direction.values()) {
			if (qc.get(dir) && (forceCheck || !randQC || world.getRandom().nextBoolean())) {
				if (world.isReceivingRedstonePower(pos.offset(dir))) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean stepByStepFilter(World world) {
		return Tweaks.Global.WORLD_TICK_OPTIONS.get().getDimensionFilter() == WorldTickOptions.DimensionFilter.ACTIVE && world.getPlayers().isEmpty();
	}
}
