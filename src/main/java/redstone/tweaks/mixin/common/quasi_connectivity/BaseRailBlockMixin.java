package redstone.tweaks.mixin.common.quasi_connectivity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.world.level.block.QuasiConnectivity;

@Mixin(BaseRailBlock.class)
public class BaseRailBlockMixin implements BlockOverrides {
	
	@Redirect(
		method = "neighborChanged",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakQuasiConnectivity(Level _level, BlockPos _pos, BlockState _state, boolean force, BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (block() == Blocks.RAIL) {
			QuasiConnectivity qc = Tweaks.Rail.quasiConnectivity();
			boolean randQC = Tweaks.Rail.randomizeQuasiConnectivity();

			return BlockOverrides.hasSignal(level, pos, qc, randQC);
		}

		return level.hasNeighborSignal(pos);
	}
}
