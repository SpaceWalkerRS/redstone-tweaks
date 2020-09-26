package redstonetweaks.helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.settings.Settings;

public class StairsHelper {
	
	public static int getReceivedStrongRedstonePower(World world, BlockPos pos, BlockState state) {
		int power = 0;
		
		for (Direction direction : Direction.values()) {
			if (state.isSideSolidFullSquare(world, pos, direction)) {
				power = Math.max(power, world.getStrongRedstonePower(pos.offset(direction), direction));
				if (power >= Settings.Global.POWER_MAX.get()) {
					return Settings.Global.POWER_MAX.get();
				}
			}
		}
		
		return power;
	}
}
