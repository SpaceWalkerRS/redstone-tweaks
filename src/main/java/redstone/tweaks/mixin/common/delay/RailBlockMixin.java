package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(RailBlock.class)
public class RailBlockMixin implements BlockOverrides {

	private boolean ticking;

	@Inject(
		method = "updateState",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/RailBlock;updateDir(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Block neighborBlock, CallbackInfo ci) {
		if (!ticking) {
			int delay = Tweaks.Rail.delay();

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.Rail.tickPriority());

				ci.cancel();
			}
		}
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return true;
	}
}
