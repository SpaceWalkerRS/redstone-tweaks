package redstone.tweaks.interfaces.mixin;

import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.ticks.TickPriority;

public interface DispenserOverrides extends BlockOverrides {

	int delay();

	boolean lazy();

	Map<Direction, Boolean> quasiConnectivity();

	boolean quasiConnectivity(Direction dir);

	boolean randomizeQuasiConnectivity();

	TickPriority tickPriority();

}
