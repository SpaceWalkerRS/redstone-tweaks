package redstone.tweaks.mixin.common.movable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Shadow private boolean isSticky;

	@Inject(
		method = "checkIfExtend",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtIgnoreRetractionWithoutHead(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (Tweaks.Piston.looseHead(isSticky) && state.getValue(PistonBaseBlock.EXTENDED)) {
			Direction facing = state.getValue(PistonBaseBlock.FACING);
			BlockPos frontPos = pos.relative(facing);

			if (!PistonOverrides.isHead(level, frontPos, facing, isSticky)) {
				ci.cancel();
			}
		}
	}

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/piston/PistonStructureResolver;"
		)
	)
	private PistonStructureResolver rtNewStructureResolver1(Level level, BlockPos pos, Direction facing, boolean extending) {
		return PistonOverrides.newStructureResolver(this, level, pos, facing, extending);
	}

	@Inject(
		method = "isPushable",
		cancellable = true,
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;EXTENDED:Lnet/minecraft/world/level/block/state/properties/BooleanProperty;"
			)
		),
		at = @At(
			value = "RETURN",
			ordinal = 0
		)
	)
	private static void rtTweakPistonMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (isMovable(state)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(
		method = "isPushable",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;getDestroySpeed(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
		)
	)
	private static void rtTweakMovable(BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
		if (state.is(Blocks.BARRIER) && Tweaks.Barrier.movable()) {
			cir.setReturnValue(true);
		}
		if (state.is(Blocks.MOVING_PISTON) && Tweaks.Global.movableMovingBlocks()) {
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
	private static PushReaction rtTweakMovable(BlockState _state, BlockState state, Level level, BlockPos pos, Direction moveDir, boolean allowDestroy, Direction pistonFacing) {
		if (state.is(Blocks.HAY_BLOCK) && Tweaks.Hay.blockMisalignedPistonMove()) {
			if (state.getValue(HayBlock.AXIS) != moveDir.getAxis()) {
				return PushReaction.BLOCK;
			}
		}
		if (state.hasBlockEntity() && !Tweaks.Global.movableBlockEntities()) {
			// this fixes an issue where block entites ignore
			// the push reaction property alltogether
			return PushReaction.BLOCK;
		}

		return state.getPistonPushReaction();
	}

	@Redirect(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"
		)
	)
	private static boolean rtTweakMovableBlockEntities(BlockState state) {
		return false; // replaced by inject above
	}

	@Inject(
		method = "triggerEvent",
		cancellable = true,
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private void rtIgnoreRetractionWithoutHead(BlockState state, Level level, BlockPos pos, int i, int j, CallbackInfoReturnable<Boolean> cir) {
		if (!level.isClientSide()) {
			if (Tweaks.Piston.looseHead(isSticky) && state.getValue(PistonBaseBlock.EXTENDED)) {
				Direction facing = state.getValue(PistonBaseBlock.FACING);
				BlockPos frontPos = pos.relative(facing);

				if (!PistonOverrides.isHead(level, frontPos, facing, isSticky)) {
					cir.setReturnValue(false);
				}
			}
		}
	}

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston) {
		return PistonOverrides.newMovingBlockEntity(this, pos, state, movedState, null, facing, extending, isSourcePiston);
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/piston/PistonStructureResolver;"
		)
	)
	private PistonStructureResolver rtNewStructureResolver2(Level level, BlockPos pos, Direction facing, boolean extending) {
		return PistonOverrides.newStructureResolver(this, level, pos, facing, extending);
	}

	@Redirect(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	private BlockEntity rtNewMovingBlockEntity(BlockPos pos, BlockState state, BlockState movedState, Direction facing, boolean extending, boolean isSourcePiston, Level level, BlockPos pistonPos, Direction pistonFacing, boolean extend) {
		BlockEntity movedBlockEntity = null;

		if (!isSourcePiston) {
			Direction moveDir = extend ? pistonFacing : pistonFacing.getOpposite();
			BlockPos movedPos = pos.relative(moveDir.getOpposite());

			movedBlockEntity = level.getBlockEntity(movedPos);

			if (movedBlockEntity != null) {
				level.removeBlockEntity(movedPos);

				// fixes disappearing block entities on the client
				if (level.isClientSide()) {
					movedBlockEntity.setChanged();
				}
			}
		}

		return PistonOverrides.newMovingBlockEntity(this, pos, state, movedState, movedBlockEntity, facing, extending, isSourcePiston);
	}

	private static boolean isMovable(BlockState state) {
		boolean isSticky = ((PistonOverrides)state.getBlock()).isSticky();

		if (Tweaks.Piston.looseHead(isSticky)) {
			return true;
		}
		if (Tweaks.Piston.movableWhenExtended(isSticky)) {
			return true;
		}

		return false;
	}
}
