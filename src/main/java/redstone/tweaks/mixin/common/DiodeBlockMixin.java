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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;
import redstone.tweaks.interfaces.mixin.ILevel;

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

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, this::microTickMode);
		}
	}

	@Inject(
		method = "tick",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakRisingEdgeLazy(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci, boolean powered, boolean shouldBePowered) {
		if (!shouldBePowered) {
			ci.cancel();
		}
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			ordinal = 1,
			target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private void rtTweakFallingEdgeDelayAndTickPriority(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		if (!rtReceivingPower) {
			int delay = Tweaks.Repeater.delayFallingEdge();
			TickPriority priority = Tweaks.Repeater.tickPriorityFallingEdge();

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, this::microTickMode);
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
		// replaced by inject above
	}

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getSignal(level, pos, state, dir, false));
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignalDirect(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getSignal(level, pos, state, dir, true));
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ticks/LevelTickAccess;willTickThisTick(Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Z"
		)
	)
	private <T> boolean rtTweakMicroTickMode(LevelTickAccess<T> ticks, BlockPos _pos, T block, Level level, BlockPos pos, BlockState state) {
		return microTickMode() ? ((ILevel)level).hasBlockEvent(pos, (Block) block) : ticks.willTickThisTick(pos, block);
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakRisingEdgeTickPriority() {
		return Tweaks.Comparator.tickPriority();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;VERY_HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakFallingEdgeTickPriority() {
		return Tweaks.Comparator.tickPriority();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakPrioritizedTickPriority() {
		return Tweaks.Comparator.tickPriority();
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

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		if (microTickMode()) {
			return BlockOverrides.scheduleOrDoTick(level, pos, state, type, TickPriority.NORMAL, this::microTickMode);
		}

		return null;
	}

	private int getSignal(BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean direct) {
		if (state.getValue(DiodeBlock.POWERED) && state.getValue(DiodeBlock.FACING) == dir) {
			return direct ? signalDirect(level, pos, state) : signal(level, pos, state);
		}

		return Redstone.SIGNAL_MIN;
	}
}
