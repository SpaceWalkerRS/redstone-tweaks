package redstone.tweaks.mixin.common.soft_inversion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(RedstoneWallTorchBlock.class)
public class RedstoneWallTorchBlockMixin {
	
	@Inject(
		method = "hasNeighborSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void shouldUnpower(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (Tweaks.RedstoneTorch.softInversion()) {
			Direction facing = state.getValue(RedstoneWallTorchBlock.FACING);
			BlockPos behindPos = pos.relative(facing.getOpposite());
			BlockState behindState = level.getBlockState(behindPos);

			if (PistonOverrides.isBase(behindState)) {
				PistonOverrides piston = (PistonOverrides)behindState.getBlock();
				Direction pistonFacing = behindState.getValue(PistonBaseBlock.FACING);

				if (piston.hasSignal(level, behindPos, pistonFacing)) {
					cir.setReturnValue(true);
				}
			}
		}
	}
}
