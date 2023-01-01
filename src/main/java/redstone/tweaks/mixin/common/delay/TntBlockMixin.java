package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.TntOverrides;

@Mixin(TntBlock.class)
public class TntBlockMixin implements TntOverrides {

	private boolean ticking;

	@Shadow private static void explode(Level level, BlockPos pos) { }

	@Inject(
		method = "onPlace",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
		tweakDelayAndTickPriority(level, pos, state, ci);
	}

	@Inject(
		method = "neighborChanged",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/TntBlock;explode(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		tweakDelayAndTickPriority(level, pos, state, ci);
	}

	@ModifyArg(
		method = "wasExploded",
		index = 0,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"
		)
	)
	private int rtTweakFuseTime(int bound) {
		return bound < 1 ? 1 : bound;
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		state.neighborChanged(level, pos, block(), pos, false);
		ticking = false;

		return true;
	}

	@Override
	public boolean isTicking() {
		return ticking;
	}

	private void tweakDelayAndTickPriority(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (!ticking) {
			int delay = Tweaks.TNT.delay();

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.TNT.tickPriority());

				ci.cancel();
			}
		}
	}
}
