package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.ILevel;

@Mixin(Level.class)
public class LevelMixin implements ILevel {

	@Redirect(
		method = "getSignal",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
		)
	)
	private boolean rtTweakRedstoneConductors(BlockState state, BlockGetter level, BlockPos _pos, BlockPos pos, Direction dir) {
		if (state.is(Blocks.MAGENTA_GLAZED_TERRACOTTA) && Tweaks.MagentaGlazedTerracotta.signalDiode()) {
			return state.getValue(GlazedTerracottaBlock.FACING) == dir;
		}

		return state.isRedstoneConductor(level, pos);
	}

	@Override
	public boolean hasBlockEvent(BlockPos pos, Block block) {
		return false;
	}
}
