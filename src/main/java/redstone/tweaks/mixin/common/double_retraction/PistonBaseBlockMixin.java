package redstone.tweaks.mixin.common.double_retraction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.BlockOverrides;
import redstone.tweaks.interfaces.mixin.PistonOverrides;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin implements PistonOverrides {

	@Inject(
		method = "onPlace",
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;checkIfExtend(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
		)
	)
	private void rtSendBlockChange(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
		// make sure the client finishes this moving block
		if (state.getValue(PistonBaseBlock.EXTENDED) && oldState.is(Blocks.MOVING_PISTON)) {
			BlockState newState = level.getBlockState(pos);

			if (newState.is(block()) && !newState.getValue(PistonBaseBlock.EXTENDED)) {
				BlockOverrides.sendBlockChange(level, pos, state);
			}
		}
	}

	@Inject(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;I)Lnet/minecraft/core/BlockPos;"
		)
	)
	private void rtStartDoubleRetraction(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (Tweaks.Piston.doDoubleRetraction()) {
			level.setBlock(pos, state.setValue(PistonBaseBlock.EXTENDED, false), Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_INVISIBLE);
		}
	}

	@Inject(
		method = "triggerEvent",
		locals = LocalCapture.CAPTURE_FAILHARD,
		at = @At(
			value = "INVOKE",
			ordinal = 1,
			target = "Lnet/minecraft/world/level/block/piston/PistonBaseBlock;moveBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Z"
		)
	)
	private void rtSendBlockUpdate(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction facing) {
		if (!level.isClientSide() && Tweaks.Piston.doDoubleRetraction()) {
			BlockPos frontPos = pos.relative(facing, 2);
			BlockState frontState = level.getBlockState(frontPos);

			if (PistonOverrides.isBase(frontState)) {
				BlockOverrides.sendBlockChange(level, frontPos, frontState);
			}
		}
	}

	@ModifyArg(
		method = "triggerEvent",
		index = 2,
		at = @At(
			value = "INVOKE",
			ordinal = 0,
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private int rtCancelDoubleRetraction(int flags) {
		if (Tweaks.Piston.doDoubleRetraction()) {
			flags &= ~Block.UPDATE_CLIENTS;
			flags |= Block.UPDATE_KNOWN_SHAPE;
			flags |= Block.UPDATE_INVISIBLE;
		}

		return flags;
	}

	@Inject(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"
		)
	)
	private void rtRemovePistonHead(Level level, BlockPos pos, Direction facing, boolean extend, CallbackInfoReturnable<Boolean> cir) {
		if (!extend && Tweaks.Piston.doDoubleRetraction()) {
			BlockPos frontPos = pos.relative(facing, 2);
			BlockState frontState = level.getBlockState(frontPos);

			if (PistonOverrides.isBase(frontState)) {
				Direction frontFacing = frontState.getValue(PistonBaseBlock.FACING);
				boolean frontSticky = PistonOverrides.isBaseSticky(frontState);
				BlockPos headPos = frontPos.relative(frontFacing);

				if (PistonOverrides.isHead(level, headPos, frontFacing, frontSticky)) {
					level.setBlock(headPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
				}
			}
		}
	}
}
