package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {

	@Shadow private static void signalPageChange(Level level, BlockPos pos, BlockState state) { }

	@Redirect(
		method = "signalPageChange",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private static void rtTweakFallingEdgeDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, Level level, BlockPos pos, BlockState state) {
		delay = Tweaks.Lectern.delayFallingEdge();
		TickPriority priority = Tweaks.Lectern.tickPriorityFallingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakRisingEdgeDelay(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!state.getValue(LecternBlock.POWERED)) {
			signalPageChange(level, pos, state);

			ci.cancel();
		}
	}

	@ModifyConstant(
		method = "getSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakSignal(int signal) {
		return Tweaks.Lectern.signal();
	}

	@ModifyConstant(
		method = "getDirectSignal",
		constant = @Constant(
			intValue = Redstone.SIGNAL_MAX
		)
	)
	private int rtTweakDirectSignal(int signal) {
		return Tweaks.Lectern.signalDirect();
	}
}
