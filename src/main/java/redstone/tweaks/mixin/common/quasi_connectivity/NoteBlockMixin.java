package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.NoteBlockOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin implements NoteBlockOverrides {

	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivityAndLazy(Level _level, BlockPos _pos, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (isTicking() && Tweaks.NoteBlock.lazy()) {
			return !state.getValue(NoteBlock.POWERED);
		}

		QuasiConnectivity qc = Tweaks.NoteBlock.quasiConnectivity();
		boolean randQC = Tweaks.NoteBlock.randomizeQuasiConnectivity();

		return BlockOverrides.hasSignal(level, pos, qc, randQC);
	}
}
