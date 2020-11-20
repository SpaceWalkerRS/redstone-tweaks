package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import redstonetweaks.setting.Tweaks;

public class StairsHelper {
	
	public static int getReceivedStrongRedstonePower(World world, BlockPos pos, BlockState state) {
		int power = 0;
		
		for (Direction direction : Direction.values()) {
			if (state.isSideSolidFullSquare(world, pos, direction)) {
				power = Math.max(power, world.getStrongRedstonePower(pos.offset(direction), direction));
				if (power >= Tweaks.Global.POWER_MAX.get()) {
					return Tweaks.Global.POWER_MAX.get();
				}
			}
		}
		
		return power;
	}
}
