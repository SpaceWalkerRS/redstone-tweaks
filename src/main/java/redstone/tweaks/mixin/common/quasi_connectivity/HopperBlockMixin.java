package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.HopperOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin implements HopperOverrides {

	@Redirect(
		method = "checkPoweredState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivityAndLazy(Level _level, BlockPos _pos, Level level, BlockPos pos, BlockState state) {
		boolean enabled = state.getValue(HopperBlock.ENABLED);

		if (isTicking() && lazy(enabled)) {
			return enabled;
		}

		QuasiConnectivity qc = Tweaks.Hopper.quasiConnectivity();
		boolean randQC = Tweaks.Hopper.randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}

	private boolean lazy(boolean enabled) {
		return enabled ? Tweaks.Hopper.lazyRisingEdge() : Tweaks.Hopper.lazyFallingEdge();
	}
}
