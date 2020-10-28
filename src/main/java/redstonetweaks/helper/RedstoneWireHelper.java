package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.setting.Settings;

public class RedstoneWireHelper {
	
	public static boolean emitsPowerTo(World world, BlockPos pos, Direction dir) {
		if (Settings.MagentaGlazedTerracotta.IS_POWER_DIODE.get()) {
			BlockState belowState = world.getBlockState(pos.down());
			if (belowState.isOf(Blocks.MAGENTA_GLAZED_TERRACOTTA)) {
				if (belowState.get(Properties.HORIZONTAL_FACING).getOpposite() != dir) {
					return false;
				}
			}
		}
		return true;
	}
}
