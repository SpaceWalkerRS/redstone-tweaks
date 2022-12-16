package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Shadow private boolean isSticky;

	@Inject(
		method = "getNeighborSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakQuasiConnectivity(Level level, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(hasSignal(level, pos, facing, Tweaks.Piston.quasiConnectivity(isSticky), Tweaks.Piston.randomizeQuasiConnectivity(isSticky)));
	}
}
