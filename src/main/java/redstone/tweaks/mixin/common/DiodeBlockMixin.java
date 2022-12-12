package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;

@Mixin(DiodeBlock.class)
public abstract class DiodeBlockMixin implements DiodeOverrides {

	private boolean rtReceivingPower;

	@Shadow private boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) { return false; }

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/DiodeBlock;shouldTurnOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtTweakShouldTurnOn(DiodeBlock diode, Level level, BlockPos pos, BlockState state) {
		rtReceivingPower = shouldTurnOn(level, pos, state);

		if (rtReceivingPower) {
			return true;
		}

		boolean powered = state.getValue(DiodeBlock.POWERED);
		boolean lazy = powered ? Tweaks.Repeater.lazyFallingEdge() : Tweaks.Repeater.lazyRisingEdge();

		return lazy;
	}

	@Inject(
		method = "tick",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			ordinal = 0,
			target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakRisingEdgeDelayAndPriority(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci, boolean powered, boolean shouldBePowered) {
		if (rtReceivingPower) {
			int delay = Tweaks.Repeater.delayRisingEdge();
			TickPriority priority = Tweaks.Repeater.tickPriorityRisingEdge();

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;ILnet/minecraft/world/ticks/TickPriority;)V"
		)
	)
	private void rtTweakFallingEdgeDelayAndTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, TickPriority priority, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		delay = Tweaks.Repeater.delayFallingEdge();
		priority = Tweaks.Repeater.tickPriorityFallingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
	}

	@Inject(
		method = "getAlternateSignal",
		at = @At(
			value = "RETURN"
		)
	)
	private void rtTweakAlternateSignal(LevelReader level, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
		if (invertAlternateSignal(state)) {
			cir.setReturnValue(-cir.getReturnValue());
		}
	}

	@Inject(
		method = "isAlternateInput",
		cancellable = true,
		at = @At(
			value = "RETURN"
		)
	)
	private void rtTweakRedstoneBlockAlternateInput(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (!Tweaks.Comparator.redstoneBlockAlternateInput() && state.is(Blocks.REDSTONE_BLOCK)) {
			cir.setReturnValue(false);
		}
	}
}
