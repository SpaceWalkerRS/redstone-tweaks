package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import redstone.tweaks.interfaces.mixin.BlockOverrides;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements BlockOverrides {

	@Inject(
		method = "onPlace",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
		boolean override = overrideOnPlace(state, level, pos, oldState, movedByPiston);

		if (override) {
			ci.cancel();
		}
	}

	@Inject(
		method = "onRemove",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
		boolean override = overrideOnRemove(state, level, pos, newState, movedByPiston);

		if (override) {
			ci.cancel();
		}
	}

	@Inject(
		method = "neighborChanged",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideNeighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
		boolean override = overrideNeighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

		if (override) {
			ci.cancel();
		}
	}

	@Inject(
		method = "triggerEvent",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		Boolean override = overrideTriggerEvent(state, level, pos, type, data);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}

	@Inject(
		method = "tick",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand, CallbackInfo ci) {
		boolean override = overrideTick(state, level, pos, rand);

		if (override) {
			ci.cancel();
		}
	}

	@Inject(
		method = "isSignalSource",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideIsSignalSource(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		Boolean override = overrideIsSignalSource(state);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}

	@Inject(
		method = "getSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideGetSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		Integer override = overrideGetDirectSignal(state, level, pos, dir);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}

	@Inject(
		method = "getDirectSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideGetDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		Integer override = overrideGetDirectSignal(state, level, pos, dir);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}

	@Inject(
		method = "getPistonPushReaction",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtOverrideGetPistonPushReaction(BlockState state, CallbackInfoReturnable<PushReaction> cir) {
		PushReaction override = overrideGetPistonPushReaction(state);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}
}
