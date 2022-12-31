package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DispenserOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin implements DispenserOverrides {

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(Level level, BlockPos pos) {
		QuasiConnectivity qc = quasiConnectivity();
		boolean randQC = randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakLazy(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!lazy() && !BlockOverrides.hasSignal(level, pos, quasiConnectivity(), randomizeQuasiConnectivity())) {
			ci.cancel();
		}
	}

	@Override
	public boolean lazy() {
		return Tweaks.Dispenser.lazy();
	}

	@Override
	public QuasiConnectivity quasiConnectivity() {
		return Tweaks.Dispenser.quasiConnectivity();
	}

	@Override
	public boolean randomizeQuasiConnectivity() {
		return Tweaks.Dispenser.randomizeQuasiConnectivity();
	}
}
