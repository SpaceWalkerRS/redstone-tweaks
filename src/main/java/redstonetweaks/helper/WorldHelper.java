package redstonetweaks.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;
import redstonetweaks.setting.types.DirectionalBooleanSetting;
import redstonetweaks.util.RelativePos;

public class WorldHelper {
	
	public static boolean isPowered(World world, BlockPos pos, BlockState state, boolean forceCheckQC, DirectionalBooleanSetting qc, boolean randQC) {
		return world.isReceivingRedstonePower(pos) || isQCPowered(world, pos, state, forceCheckQC, qc, randQC);
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
	
	public static void updateNeighborsExcept(World world, BlockPos notifierPos, Block sourceBlock, RelativePos except) {
		Settings.Global.BLOCK_UPDATE_ORDER.get().dispatchBlockUpdatesExcept(world, notifierPos, sourceBlock, except);
	}
}
