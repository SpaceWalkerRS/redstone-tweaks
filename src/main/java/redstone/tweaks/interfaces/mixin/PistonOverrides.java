package redstone.tweaks.interfaces.mixin;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

import redstone.tweaks.Tweaks;
import redstone.tweaks.util.Directions;

public interface PistonOverrides extends BlockOverrides {

	boolean isSticky();

	default boolean hasSignal(Level level, BlockPos pos, Direction facing, Map<Direction, Boolean> qc, boolean randQC) {
		boolean ignoreFront = Tweaks.Piston.ignorePowerFromFront(isSticky());

		for (Direction dir : Directions.ALL) {
			if (ignoreFront && dir == facing) {
				continue;
			}
			if (level.hasSignal(pos.relative(dir), dir)) {
				return true;
			}
		}

		return BlockOverrides.hasQuasiSignal(level, pos, qc, randQC);
	}

	public static boolean isHead(Level level, BlockPos pos, Direction facing, boolean isSticky) {
		BlockState state = level.getBlockState(pos);

		if (state.is(Blocks.PISTON_HEAD)) {
			return state.getValue(PistonHeadBlock.FACING) == facing && (state.getValue(PistonHeadBlock.TYPE) == PistonType.STICKY) == isSticky;
		}
		if (state.is(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof PistonMovingBlockEntity) {
				PistonMovingBlockEntity mbe = (PistonMovingBlockEntity)blockEntity;
				return mbe.isSourcePiston() && mbe.isExtending() && mbe.getDirection() == facing;
			}
		}

		return false;
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
}
