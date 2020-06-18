package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.setting.Settings;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 2))
	private int getRedstoneTorchDelay(int oldDelay) {
		return (int)Settings.redstoneTorchDelay.get();
	}
	
	@ModifyConstant(method = "update", constant = @Constant(longValue = 60L))
	private static long updateBurnoutTimerDelay(long oldDelay) {
		return (long)(int)Settings.redstoneTorchBurnoutTimerDelay.get();
	}
	
	@ModifyConstant(method = "update", constant = @Constant(intValue = 160))
	private static int updateBurnoutDelay(int oldDelay) {
		return (int)Settings.redstoneTorchBurnoutDelay.get();
	}
	
	@Overwrite
	public boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.down();
		
		// If the pistonsPowerRedstoneTorches setting is enabled,
		// return true if the torch is attached to a piston that is
		// receiving redstone power.
		if ((boolean)Settings.pistonsPowerRedstoneTorches.get()) {
			BlockState blockState = world.getBlockState(blockPos);
			
			if (blockState.getBlock() instanceof PistonBlock) {
				if (PistonHelper.shouldExtend(world, blockPos, blockState.get(PistonBlock.FACING))) {
					return true;
				}
			}
		}
		return world.isEmittingRedstonePower(blockPos, Direction.DOWN);
	}
}
