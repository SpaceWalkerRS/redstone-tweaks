package redstone.tweaks.interfaces.mixin;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
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
import redstone.tweaks.util.Directions;

public interface PistonOverrides extends BlockOverrides {

	boolean isSticky();

	void queueBlockEvent(Level level, BlockPos pos, BlockState state, int type, int data);

	default boolean hasSignal(Level level, BlockPos pos, Direction facing) {
		boolean ignoreFront = Tweaks.Piston.ignorePowerFromFront(isSticky());

		for (Direction dir : Directions.ALL) {
			if (ignoreFront && dir == facing) {
				continue;
			}
			if (level.hasSignal(pos.relative(dir), dir)) {
				return true;
			}
		}

		return BlockOverrides.hasQuasiSignal(level, pos, Tweaks.Piston.quasiConnectivity(isSticky()), Tweaks.Piston.randomizeQuasiConnectivity(isSticky()));
	}

	public static boolean isBase(Level level, BlockPos pos) {
		return isBase(level.getBlockState(pos));
	}

	public static boolean isBase(BlockState state) {
		return state.getBlock() instanceof PistonBaseBlock;
	}

	public static boolean isBaseSticky(BlockState state) {
		return ((PistonOverrides)state.getBlock()).isSticky();
	}

	public static boolean isHead(Level level, BlockPos pos, Direction facing, boolean isSticky) {
		return isHead(level, pos, level.getBlockState(pos), facing, isSticky);
	}

	public static boolean isHead(Level level, BlockPos pos, BlockState state, Direction facing, boolean isSticky) {
		return isStaticHead(state, facing, isSticky) || isExtendingHead(level, pos, facing, isSticky);
	}

	public static boolean isStaticHead(BlockState state, Direction facing, boolean isSticky) {
		return state.is(Blocks.PISTON_HEAD) && state.getValue(PistonHeadBlock.FACING) == facing && isHeadSticky(state) == isSticky;
	}

	public static boolean isExtendingHead(Level level, BlockPos pos, Direction facing, boolean isSticky) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof PistonMovingBlockEntity) {
			PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;

			if (mbe.isSourcePiston() && mbe.isExtending()) {
				return isStaticHead(mbe.getMovedState(), facing, isSticky);
			}
		}

		return false;
	}

	public static boolean isHeadSticky(BlockState state) {
		return state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY;
	}

	public static PistonStructureResolver newStructureResolver(PistonOverrides source, Level level, BlockPos pos, Direction facing, boolean extending) {
		PistonStructureResolver structureResolver = new PistonStructureResolver(level, pos, facing, extending);
		((IPistonStructureResolver)structureResolver).init(source);

		return structureResolver;
	}

	public static BlockEntity newMovingBlockEntity(PistonOverrides source, BlockPos pos, BlockState state, BlockState movedState, BlockEntity movedBlockEntity, Direction facing, boolean extending, boolean isSourcePiston) {
		BlockEntity blockEntity = MovingPistonBlock.newMovingBlockEntity(pos, state, movedState, facing, extending, isSourcePiston);
		((IPistonMovingBlockEntity)blockEntity).setMovedBlock(movedState, movedBlockEntity);
		((IPistonMovingBlockEntity)blockEntity).init(source);

		return blockEntity;
	}

	public static void dropMovingBlock(PistonOverrides piston, Level level, BlockPos pos, Direction facing, boolean extending) {
		dropMovingBlock(level, pos.relative(facing, extending ? 2 : 1), facing, extending);
	}

	public static void dropMovingStructure(PistonOverrides piston, Level level, BlockPos pos, Direction facing, boolean extending) {
		if (extending) {
			pos = pos.relative(facing);
		} else {
			pos = pos.relative(facing.getOpposite());
		}

		PistonStructureResolver structureResolver = PistonOverrides.newStructureResolver(piston, level, pos, facing, extending);

		if (!((IPistonStructureResolver)structureResolver).resolveMoving()) {
			return;
		}

		List<BlockPos> structure = structureResolver.getToPush();

		for (int i = 0; i < structure.size(); i++) {
			dropMovingBlock(level, structure.get(i), facing, extending);
		}
	}

	private static void dropMovingBlock(Level level, BlockPos pos, Direction facing, boolean extending) {
		BlockState state = level.getBlockState(pos);

		if (!state.is(Blocks.MOVING_PISTON)) {
			return;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof PistonMovingBlockEntity)) {
			return;
		}

		PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;

		if (mbe.isExtending() == extending && mbe.getDirection() == facing) {
			mbe.finalTick();
		}
	}
}
