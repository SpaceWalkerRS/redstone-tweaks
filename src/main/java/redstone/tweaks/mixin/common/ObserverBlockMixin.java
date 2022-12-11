package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin implements BlockOverrides {

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakFallingEdgeDelayAndTickPriority(ServerLevel level, BlockPos pos, Block block, int delay) {
		scheduleOff(level, pos);
	}

	@Inject(
		method = "updateShape",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtObserveBlockUpdates(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
		if (!level.isClientSide() && Tweaks.Observer.observeBlockUpdates()) {
			cir.setReturnValue(state);
		}
	}

	@Inject(
		method = "startSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtDisable(LevelAccessor level, BlockPos pos, CallbackInfo ci) {
		if (!level.isClientSide() && Tweaks.Observer.disable()) {
			ci.cancel();
		}
	}

	@Redirect(
		method = "startSignal",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/LevelAccessor;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V"
		)
	)
	private void rtTweakRisingEdgeDelayAndTickPriority(LevelAccessor level, BlockPos pos, Block block, int delay) {
		if (level instanceof Level) {
			scheduleOn((Level)level, pos);
		}
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (level instanceof Level) {
			cir.setReturnValue(getSignal((Level)level, pos, state, dir, true));
		}
	}

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		if (level instanceof Level) {
			cir.setReturnValue(getSignal((Level)level, pos, state, dir, false));
		}
	}

	@Override
	public boolean overrideNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (level.isClientSide()) {
			return false;
		}
		if (Tweaks.Observer.disable() || !Tweaks.Observer.observeBlockUpdates()) {
			return false;
		}
		if (state.getValue(ObserverBlock.POWERED)) {
			return false;
		}

		if (neighborPos.equals(pos) || neighborPos.equals(pos.relative(state.getValue(ObserverBlock.FACING)))) {
			scheduleOn(level, pos);
		}

		return false;
	}

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		return BlockOverrides.microTick(level, pos, state, type, data);
	}

	private void scheduleOn(Level level, BlockPos pos) {
		int delay = Tweaks.Observer.delayRisingEdge();
		TickPriority priority = Tweaks.Observer.tickPriorityRisingEdge();

		schedule(level, pos, delay, priority);
	}

	private void scheduleOff(Level level, BlockPos pos) {
		int delay = Tweaks.Observer.delayFallingEdge();
		TickPriority priority = Tweaks.Observer.tickPriorityFallingEdge();

		schedule(level, pos, delay, priority);
	}

	private void schedule(Level level, BlockPos pos, int delay, TickPriority priority) {
		if (Tweaks.Observer.microTickMode()) {
			level.blockEvent(pos, block(), delay, 0);
		} else {
			level.scheduleTick(pos, block(), delay, priority);
		}
	}

	private int getSignal(Level level, BlockPos pos, BlockState state, Direction dir, boolean direct) {
		if (level.isClientSide() || !state.getValue(ObserverBlock.POWERED)) {
			return Redstone.SIGNAL_MIN;
		}

		Direction facing = state.getValue(ObserverBlock.FACING);

		if (facing != dir) {
			return Redstone.SIGNAL_MIN;
		}

		return direct ? Tweaks.Observer.signalDirect() : Tweaks.Observer.signal();
	}
}
