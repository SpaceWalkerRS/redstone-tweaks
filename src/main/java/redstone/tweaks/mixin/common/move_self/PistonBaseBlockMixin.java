package redstone.tweaks.mixin.common.move_self;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.PistonOverrides;
import redstone.tweaks.util.MotionType;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin extends Block implements PistonOverrides {

	private PistonBaseBlockMixin(Properties properties) {
		super(properties);
	}

	@Shadow private boolean getNeighborSignal(Level level, BlockPos pos, Direction facing) { return false; }
	@Shadow private boolean moveBlocks(Level level, BlockPos pos, Direction facing, boolean extending) { return false; }

	@Redirect(
		method = "checkIfExtend",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"
		)
	)
	private boolean rtQueueExtendBackwards(PistonStructureResolver structureResolver, Level level, BlockPos pos, BlockState state) {
		if (structureResolver.resolve()) {
			return true;
		}

		if (Tweaks.Piston.canMoveSelf(isSticky())) {
			Direction facing = state.getValue(PistonBaseBlock.FACING);
			BlockPos headPos = pos.relative(facing);

			structureResolver = PistonOverrides.newStructureResolver(this, level, pos, facing.getOpposite(), true);

			if (structureResolver.resolve() && !PistonOverrides.isHead(level, headPos, facing, isSticky())) {
				queueBlockEvent(level, pos, state, MotionType.EXTEND_BACKWARDS, facing.get3DDataValue());
			}
		}

		return false;
	}

	@Inject(
		method = "checkIfExtend",
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
		)
	)
	private void rtQueueRetractForwards(Level level, BlockPos pos, BlockState state, CallbackInfo ci, Direction facing, boolean hasSignal, BlockPos frontPos, BlockState frontState) {
		if (!Tweaks.Piston.canMoveSelf(isSticky())) {
			return;
		}
		if (frontState.isAir() || frontState.is(Blocks.MOVING_PISTON)) {
			return;
		}

		PistonStructureResolver structureResolver = PistonOverrides.newStructureResolver(this, level, pos, facing, false);

		if (structureResolver.resolve()) {
			return;
		}
		if (PistonBaseBlock.isPushable(frontState, level, frontPos, facing.getOpposite(), true, facing)) {
			return;
		}

		queueBlockEvent(level, pos, state, MotionType.RETRACT_FORWARDS, facing.get3DDataValue());

		ci.cancel();
	}

	@Inject(
		method = "triggerEvent",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtHandleMoveSelf(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (type == MotionType.EXTEND_BACKWARDS) {
			cir.setReturnValue(handleExtendBackwards(level, pos, state, data));
		}
		if (type == MotionType.RETRACT_FORWARDS) {
			cir.setReturnValue(handleRetractForwards(level, pos, state, data));
		}
	}

	private boolean handleExtendBackwards(Level level, BlockPos pos, BlockState state, int data) {
		Direction facing = state.getValue(PistonBaseBlock.FACING);

		if (!level.isClientSide() && !getNeighborSignal(level, pos, facing)) {
			return false;
		}

		Direction moveDir = facing.getOpposite();

		if (!moveBlocks(level, pos, moveDir, true)) {
			return false;
		}

		PistonType type = isSticky() ? PistonType.STICKY : PistonType.DEFAULT;
		BlockPos behindPos = pos.relative(moveDir);

		BlockState baseState = defaultBlockState().
			setValue(PistonBaseBlock.FACING, Direction.from3DDataValue(data & 0x7)).
			setValue(PistonBaseBlock.EXTENDED, true);
		BlockState movingState = Blocks.MOVING_PISTON.defaultBlockState().
			setValue(MovingPistonBlock.FACING, moveDir).
			setValue(MovingPistonBlock.TYPE, type);
		BlockEntity movingBlockEntity = PistonOverrides.newMovingBlockEntity(this, behindPos, movingState, baseState, null, moveDir, true, true);

		level.setBlock(behindPos, movingState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);
		level.setBlockEntity(movingBlockEntity);

		BlockState headState = Blocks.PISTON_HEAD.defaultBlockState().
			setValue(PistonHeadBlock.FACING, facing).
			setValue(PistonHeadBlock.TYPE, type).
			setValue(PistonHeadBlock.SHORT, true);
		level.setBlock(pos, headState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);

		return true;
	}

	private boolean handleRetractForwards(Level level, BlockPos pos, BlockState state, int data) {
		Direction facing = state.getValue(PistonBaseBlock.FACING);

		if (!level.isClientSide() && getNeighborSignal(level, pos, facing)) {
			if (Tweaks.Piston.doDoubleRetraction()) {
				level.setBlock(pos, state.setValue(PistonBaseBlock.EXTENDED, true), Block.UPDATE_KNOWN_SHAPE);
			}

			return false;
		}

		BlockPos frontPos = pos.relative(facing, 2);
		BlockState frontState = level.getBlockState(frontPos);

		if (frontState.isAir()) {
			return false;
		}
		if (PistonBaseBlock.isPushable(frontState, level, frontPos, facing.getOpposite(), true, facing)) {
			return false;
		}

		BlockPos headPos = pos.relative(facing);
		BlockEntity blockEntity = level.getBlockEntity(headPos);

		if (blockEntity instanceof PistonMovingBlockEntity) {
			((PistonMovingBlockEntity)blockEntity).finalTick();
		}

		PistonType type = isSticky() ? PistonType.STICKY : PistonType.DEFAULT;

		BlockState baseState = defaultBlockState().
			setValue(PistonBaseBlock.FACING, Direction.from3DDataValue(data & 0x7)).
			setValue(PistonBaseBlock.EXTENDED, false);
		BlockState movingState = Blocks.MOVING_PISTON.defaultBlockState().
			setValue(MovingPistonBlock.FACING, facing).
			setValue(MovingPistonBlock.TYPE, type);
		BlockEntity movingBlockEntity = PistonOverrides.newMovingBlockEntity(this, headPos, movingState, baseState, null, facing, true, true);

		level.setBlock(headPos, movingState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);
		level.setBlockEntity(movingBlockEntity);

		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL);

		return true;
	}
}
