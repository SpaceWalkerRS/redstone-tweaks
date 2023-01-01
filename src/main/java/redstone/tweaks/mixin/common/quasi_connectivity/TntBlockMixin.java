package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.TntOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin implements TntOverrides {

	@Redirect(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity1(Level level, BlockPos pos) {
		return hasNeighborSignal(level, pos);
	}

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity2(Level level, BlockPos pos) {
		return (isTicking() && Tweaks.TNT.lazy()) || hasNeighborSignal(level, pos);
	}

	private boolean hasNeighborSignal(Level level, BlockPos pos) {
		QuasiConnectivity qc = Tweaks.TNT.quasiConnectivity();
		boolean randQC = Tweaks.TNT.randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}
}
