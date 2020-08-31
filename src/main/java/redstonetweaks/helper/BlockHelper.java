package redstonetweaks.helper;

import static redstonetweaks.setting.SettingsManager.*;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class BlockHelper extends AbstractBlock {
	
	public BlockHelper(Settings settings) {
		super(settings);
	}

	public static BlockState postProcessState(World world, BlockState state, BlockPos pos) {
		BlockState blockState = state;
		
		for (Direction direction : FACINGS) {
			BlockPos neighborPos = pos.offset(direction);
			blockState = blockState.getStateForNeighborUpdate(direction, world.getBlockState(neighborPos), world, pos, neighborPos);
		}
		return blockState;
	}
	
	public static Direction[] getFacings() {
		return FACINGS;
	}
	
	public static boolean isRigidPistonBase(BlockView world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof PistonBlock) {
			return isPistonRigid(state.isOf(Blocks.STICKY_PISTON));
		}
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
					BlockState piston = pistonBlockEntity.getPushedBlock();
					return isPistonRigid(piston.isOf(Blocks.STICKY_PISTON));
				}
			}
		}
		
		return false;
	}
	
	public static boolean isPistonRigid(boolean sticky) {
		return sticky ? STICKY_PISTON.get(SUPPORTS_BRITTLE_BLOCKS) : NORMAL_PISTON.get(SUPPORTS_BRITTLE_BLOCKS);
	}
}
