package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import redstonetweaks.setting.Tweaks;

public class RedstoneWireHelper {
	
	public static boolean emitsPowerTo(BlockView world, BlockPos pos, BlockState state, Direction dir) {
		return state.isOf(Blocks.REDSTONE_WIRE) ? emitsPowerTo(world, pos, dir) : false;
	}
	
	public static boolean emitsPowerTo(BlockView world, BlockPos pos, Direction dir) {
		if (Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.get() && dir.getAxis().isHorizontal()) {
			BlockState belowState = world.getBlockState(pos.down());
			
			if (belowState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
				return belowState.get(Properties.HORIZONTAL_FACING) == dir;
			}
		}
		return true;
	}
}
