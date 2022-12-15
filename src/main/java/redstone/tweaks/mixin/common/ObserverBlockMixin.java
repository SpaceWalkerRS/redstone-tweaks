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
import net.minecraft.util.RandomSource;
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
	private void rtTweakFallingEdgeDelayAndTickPriority(ServerLevel _level, BlockPos _pos, Block block, int delay, BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
		scheduleOrDoTick(level, pos, state, true);
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
			scheduleOrDoTick((Level)level, pos, level.getBlockState(pos), false);
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
			scheduleOrDoTick(level, pos, state, false);
		}

		return false;
	}

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		return BlockOverrides.scheduleOrDoTick(level, pos, state, type, TickPriority.NORMAL, Tweaks.Observer::microTickMode);
	}

	private static void scheduleOrDoTick(LevelAccessor level, BlockPos pos, BlockState state, boolean powered) {
		int delay = powered ? Tweaks.Observer.delayFallingEdge() : Tweaks.Observer.delayRisingEdge();
		TickPriority priority = powered ? Tweaks.Observer.tickPriorityFallingEdge() : Tweaks.Observer.tickPriorityRisingEdge();

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, Tweaks.Observer::microTickMode);
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
