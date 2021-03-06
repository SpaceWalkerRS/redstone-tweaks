package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import redstonetweaks.setting.settings.Tweaks;

public class RedstoneWireHelper {
	
	public static boolean emitsPowerTo(BlockView world, BlockPos pos, Direction dir) {
		if (Tweaks.MagentaGlazedTerracotta.IS_POWER_DIODE.get() && dir.getAxis().isHorizontal()) {
			BlockPos belowPos = pos.down();
			BlockState belowState = world.getBlockState(belowPos);
			
			if (belowState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
				return dir == belowState.get(Properties.HORIZONTAL_FACING);
			}
		}
		
		return true;
	}
}
