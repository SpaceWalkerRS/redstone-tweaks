package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.RedstoneOreOverrides;

@Mixin(RedStoneOreBlock.class)
public class RedStoneOreBlockMixin implements RedstoneOreOverrides {

	private boolean ticking;

	@Shadow private static void interact(BlockState state, Level level, BlockPos pos) { }

	@Inject(
		method = "interact",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
		if (!((RedstoneOreOverrides)state.getBlock()).interact(level, pos, state)) {
			ci.cancel();
		}
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		interact(state, level, pos);
		ticking = false;

		return true;
	}

	@Override
	public boolean interact(Level level, BlockPos pos, BlockState state) {
		if (!ticking) {
			int delay = Tweaks.RedstoneOre.delay();

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.RedstoneOre.tickPriority());
			}
		}

		return !ticking;
	}
}
