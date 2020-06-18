package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.setting.Settings;

@Mixin(WallRedstoneTorchBlock.class)
public class WallRedstoneTorchBlockMixin {
	
	@Shadow @Final private static DirectionProperty FACING;
	
	@Overwrite
	public boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
		Direction attached = state.get(FACING).getOpposite();
		BlockPos blockPos = pos.offset(attached);
		
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
		return world.isEmittingRedstonePower(blockPos, attached);
	}
}
