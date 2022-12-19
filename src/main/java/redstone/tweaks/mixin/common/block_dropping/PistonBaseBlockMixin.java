package redstone.tweaks.mixin.common.block_dropping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;isExtending()Z"
		)
	)
	private boolean rtCancelBlockDropping(PistonMovingBlockEntity movingBlockEntity, Level level, BlockPos pos, BlockState state) {
		// If this check succeeds the block event type is set to 2,
		// which will prevent the piston from pulling the blocks in
		// front of it.
		return movingBlockEntity.isExtending() && Tweaks.Piston.doBlockDropping();
	}

	@Redirect(
		method = "triggerEvent",
		slice = @Slice(
			from = @At(
				value = "FIELD",
				target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;isSticky:Z"
			)
		),
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
		)
	)
	private BlockState rtDoBlockDropping(Level _level, BlockPos frontPos, BlockState state, Level level, BlockPos pos, int type, int data) {
		Direction facing = state.getValue(PistonBaseBlock.FACING);
		BlockState frontState = level.getBlockState(frontPos);

		doBlockDropping(level, pos, facing, false);

		// The state that is return is used to determine whether the
		// piston should try to retract the blocks in front of it.
		return Tweaks.Piston.doBlockDropping() ? frontState : level.getBlockState(frontPos);
	}

	@Redirect(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/block/piston/PistonMovingBlockEntity;finalTick()V"
		)
	)
	private void rtCancelBlockDropping(PistonMovingBlockEntity movingBlockEntity) {
		// replaced by the method below
	}

	@Override
	public void doBlockDropping(Level level, BlockPos pos, Direction facing, boolean extending) {
		if (Tweaks.Piston.doBlockDropping()) {
			if (Tweaks.Piston.doFastBlockDropping()) {
				// "fast" block dropping: moved structure is
				// placed but not retracted again
				if (Tweaks.Piston.doSuperBlockDropping()) {
					// drop entire moving structure
					PistonOverrides.dropMovingStructure(this, level, pos, facing, !extending);
				} else {
					// drop only the moving block directly in front
					PistonOverrides.dropMovingBlock(this, level, pos, facing);
				}
			} else {
				// "slow" block dropping: moved structure keeps
				// moving and is not retracted back
			}
		} else {
			// no block dropping: place the entire moved
			// structure so it can be retracted again
			PistonOverrides.dropMovingStructure(this, level, pos, facing, !extending);
		}
	}
}
