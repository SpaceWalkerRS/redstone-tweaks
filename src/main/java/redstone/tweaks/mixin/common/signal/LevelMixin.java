package redstone.tweaks.mixin.common.signal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

import redstone.tweaks.Tweaks;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelReader {

	private BlockState cachedState = Blocks.AIR.defaultBlockState();

	@Inject(
		method = "getDirectSignalTo",
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakStairsConductRedstone(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
		if (Tweaks.Stairs.conductRedstone()) {
			cachedState = getBlockState(pos);
		} else {
			cachedState = Blocks.AIR.defaultBlockState();
		}
	}

	@Redirect(
		method = "getDirectSignalTo",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getDirectSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
		)
	)
	private int rtTweakStairsConductRedstone(Level level, BlockPos sidePos, Direction side, BlockPos pos) {
		if (Tweaks.Stairs.conductRedstone() && cachedState.getBlock() instanceof StairBlock) {
			if (!cachedState.isFaceSturdy(this, pos, side)) {
				return Redstone.SIGNAL_MIN;
			}
		}

		return getDirectSignal(sidePos, side);
	}

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
		if (state.getBlock() instanceof StairBlock && Tweaks.Stairs.conductRedstone()) {
			return state.isFaceSturdy(level, pos, dir.getOpposite());
		}

		return state.isRedstoneConductor(level, pos);
	}
}
