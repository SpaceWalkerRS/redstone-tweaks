package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.DiodeOverrides;
import redstone.tweaks.interfaces.mixin.ILevel;
import redstone.tweaks.interfaces.mixin.PropertyOverrides;

@Mixin(DiodeBlock.class)
public abstract class DiodeBlockMixin implements DiodeOverrides {

	private boolean rt_receivingPower;

	@Shadow private boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) { return false; }

	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/DiodeBlock;shouldTurnOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean rtTweakShouldTurnOn(DiodeBlock diode, Level level, BlockPos pos, BlockState state) {
		rt_receivingPower = shouldTurnOn(level, pos, state);

		if (rt_receivingPower) {
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
		if (rt_receivingPower) {
			int delay = Tweaks.Repeater.delayRisingEdge();
			TickPriority priority = Tweaks.Repeater.tickPriorityRisingEdge();

			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);

			delay = PropertyOverrides.overrideDelay(belowState, delay);
			priority = PropertyOverrides.overrideTickPriority(belowState, priority);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, () -> PropertyOverrides.overrideMicrotickMode(belowState, microtickMode()));
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
		if (!rt_receivingPower) {
			int delay = Tweaks.Repeater.delayFallingEdge();
			TickPriority priority = Tweaks.Repeater.tickPriorityFallingEdge();

			BlockPos belowPos = pos.below();
			BlockState belowState = level.getBlockState(belowPos);

			delay = PropertyOverrides.overrideDelay(belowState, delay);
			priority = PropertyOverrides.overrideTickPriority(belowState, priority);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, () -> PropertyOverrides.overrideMicrotickMode(belowState, microtickMode()));
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

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/ticks/LevelTickAccess;willTickThisTick(Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Z"
		)
	)
	private <T> boolean rtTweakMicroTickMode(LevelTickAccess<T> ticks, BlockPos _pos, T block, Level level, BlockPos pos, BlockState state) {
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);

		return PropertyOverrides.overrideMicrotickMode(belowState, microtickMode()) ? ((ILevel)level).hasBlockEvent(pos, block()) : ticks.willTickThisTick(pos, block);
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakRisingEdgeTickPriority() {
		return Tweaks.Repeater.tickPriorityRisingEdge();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;VERY_HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakFallingEdgeTickPriority() {
		return Tweaks.Repeater.tickPriorityFallingEdge();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/ticks/TickPriority;EXTREMELY_HIGH:Lnet/minecraft/world/ticks/TickPriority;"
		)
	)
	private TickPriority rtTweakPrioritizedTickPriority() {
		return Tweaks.Repeater.tickPriorityPrioritized();
	}

	@Redirect(
		method = "checkTickOnNeighbor",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;scheduleTick(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;ILnet/minecraft/world/ticks/TickPriority;)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block block, int delay, TickPriority priority, Level level, BlockPos pos, BlockState state) {
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);

		delay = PropertyOverrides.overrideDelay(belowState, delay);
		priority = PropertyOverrides.overrideTickPriority(belowState, priority);

		BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority, () -> PropertyOverrides.overrideMicrotickMode(belowState, microtickMode()));
	}

	@Override
	public Boolean overrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
		return BlockOverrides.scheduleOrDoTick(level, pos, state, type, TickPriority.NORMAL, this::microtickMode);
	}
}
