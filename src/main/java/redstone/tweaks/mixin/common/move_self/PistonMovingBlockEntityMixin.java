package redstone.tweaks.mixin.common.move_self;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin {

	@Inject(
		method = "tick",
		at = @At(
			value = "HEAD"
		)
	)
	private static void rtPlaceLongArm(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity mbe, CallbackInfo ci) {
		if (mbe.isSourcePiston() && mbe.isExtending() && mbe.getProgress(0.0F) > 0.25F) {
			BlockState movedState = mbe.getMovedState();

			if (PistonOverrides.isBase(movedState) && movedState.getValue(PistonBaseBlock.EXTENDED)) {
				Direction facing = movedState.getValue(PistonBaseBlock.FACING);
				boolean isSticky = PistonOverrides.isBaseSticky(movedState);
				BlockPos frontPos = pos.relative(facing);
				BlockState frontState = level.getBlockState(frontPos);

				if (frontState.is(Blocks.PISTON_HEAD)) {
					Direction headFacing = frontState.getValue(PistonHeadBlock.FACING);
					boolean headSticky = PistonOverrides.isHeadSticky(frontState);

					if (headFacing == facing && headSticky == isSticky) {
						level.setBlock(frontPos, frontState.setValue(PistonHeadBlock.SHORT, false), Block.UPDATE_KNOWN_SHAPE);
					}
				}
			}
		}
	}
}
