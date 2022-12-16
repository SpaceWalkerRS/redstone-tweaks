package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PistonOverrides;
import redstone.tweaks.util.MotionType;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements PistonOverrides {

	@Shadow private boolean isSticky;

	private boolean ticking;

	@Shadow private void checkIfExtend(Level level, BlockPos pos, BlockState state) { }
	@Shadow private boolean getNeighborSignal(Level level, BlockPos pos, Direction facing) { return false; }

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;getNeighborSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
		)
	)
	private boolean rtTweakLazy(PistonBaseBlock piston, Level _level, BlockPos _pos, Direction facing, Level level, BlockPos pos, BlockState state) {
		if (ticking) {
			boolean extend = !state.getValue(PistonBaseBlock.EXTENDED);
			boolean lazy = Tweaks.Piston.lazy(extend, isSticky);

			if (lazy) {
				return extend;
			}
		}

		return getNeighborSignal(level, pos, facing);
	}

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V"
		)
	)
	private void rtTweakDelayAndTickPriority(Level _level, BlockPos _pos, Block piston, int type, int data, Level level, BlockPos pos, BlockState state) {
		if (ticking) {
			level.blockEvent(pos, piston, type, data);
		} else {
			boolean extend = !state.getValue(PistonBaseBlock.EXTENDED);

			int delay = Tweaks.Piston.delay(extend, isSticky);
			TickPriority priority = Tweaks.Piston.tickPriority(extend, isSticky);

			BlockOverrides.scheduleOrDoTick(level, pos, state, delay, priority);
		}
	}

	@Inject(
		method = "getNeighborSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakQuasiConnectivity(Level level, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(PistonOverrides.hasSignal(level, pos, this, facing, Tweaks.Piston.quasiConnectivity(isSticky), Tweaks.Piston.randomizeQuasiConnectivity(isSticky)));
	}

	@Inject(
		method = "isPushable",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
		)
	)
	private static void rtTweakBarrierMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(Blocks.BARRIER) && Tweaks.Barrier.movable()) {
			cir.setReturnValue(true);
		}
	}

	@Redirect(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"
		)
	)
	private static PushReaction rtTweakHayMovable(BlockState _state, BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
		if (state.is(Blocks.HAY_BLOCK) && Tweaks.Hay.blockMisalignedPistonMove()) {
			if (state.getValue(HayBlock.AXIS) != moveDir.getAxis()) {
				return PushReaction.BLOCK;
			}
		}

		return state.getPistonPushReaction();
	}

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;getNeighborSignal(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
		)
	)
	private boolean rtTweakLazy(PistonBaseBlock piston, Level _level, BlockPos _pos, Direction facing, BlockState state, Level level, BlockPos pos, int type, int data) {
		boolean extend = MotionType.isExtend(type);
		boolean lazy = Tweaks.Piston.lazy(extend, isSticky);

		return lazy ? extend : getNeighborSignal(level, pos, facing);
	}

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity1(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston) {
		return PistonOverrides.newMovingBlockEntity(block(), pos, state, movedState, facing, extending, isSourcePiston);
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity2(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston) {
		return PistonOverrides.newMovingBlockEntity(block(), pos, state, movedState, facing, extending, isSourcePiston);
	}

	@Override
	public boolean overrideTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		ticking = true;
		checkIfExtend(level, pos, state);
		ticking = false;

		return true;
	}

	@Override
	public boolean isSticky() {
		return isSticky;
	}
}
