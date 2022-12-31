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
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.NoteBlockOverrides;

@Mixin(NoteBlock.class)
public class NoteBlockMixin implements NoteBlockOverrides {

	private boolean ticking;

	@Inject(
		method = "playNote",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelayAndTickPriority(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		if (!ticking) {
			int delay = Tweaks.NoteBlock.delay();

			if (delay > 0) {
				BlockOverrides.scheduleOrDoTick(level, pos, state, delay, Tweaks.NoteBlock.tickPriority());

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

	@Override
	public boolean isTicking() {
		return ticking;
	}
}
