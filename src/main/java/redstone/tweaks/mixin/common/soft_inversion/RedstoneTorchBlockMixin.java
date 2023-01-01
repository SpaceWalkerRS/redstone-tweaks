package redstone.tweaks.mixin.common.soft_inversion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {
	
	@Inject(
		method = "hasNeighborSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void shouldUnpower(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (Tweaks.RedstoneTorch.softInversion()) {
			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);

			if (PistonOverrides.isBase(belowState)) {
				PistonOverrides piston = (PistonOverrides)belowState.getBlock();
				Direction facing = belowState.getValue(PistonBaseBlock.FACING);

				if (piston.hasSignal(level, belowPos, facing)) {
					cir.setReturnValue(true);
				}
			}
		}
	}
}
