package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;
import redstonetweaks.block.piston.MovedBlock;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;
import redstonetweaks.setting.types.DirectionToBooleanSetting;
import redstonetweaks.world.common.WorldTickOptions;

public class WorldHelper {
	
	public static void setBlockWithEntity(World world, BlockPos pos, MovedBlock movedBlock, int flags) {
		setBlockWithEntity(world, pos, movedBlock.getBlockState(), movedBlock.getBlockEntity(), flags);
	}
	
	public static void setBlockWithEntity(World world, BlockPos pos, BlockState state, BlockEntity blockEntity, int flags) {
		if (blockEntity != null) {
			// This ensures the block entity is placed, as it prevents an identical state being placed
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), flags & 64 | 16);
			
			((RTIWorld)world).queueBlockEntityPlacement(pos, blockEntity);
		}
		
		world.setBlockState(pos, state, flags);
	}
	
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
	
	public static void createSpontaneousExplosion(World world, BlockPos pos) {
		world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5.0F, false, DestructionType.DESTROY);
	}
}
