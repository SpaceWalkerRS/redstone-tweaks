package redstonetweaks.helper;

import static redstonetweaks.setting.SettingsManager.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface AbstractBlockHelper {
	
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type);
	
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
		return sticky ? STICKY_PISTON.get(HAS_FULL_SOLID_SIDES) : NORMAL_PISTON.get(HAS_FULL_SOLID_SIDES);
	}
}
