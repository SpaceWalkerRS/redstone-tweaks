package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.world.level.block.QuasiConnectivity;

public interface DispenserOverrides extends BlockOverrides {

	int delay();

	boolean lazy();

	QuasiConnectivity quasiConnectivity();

	boolean randomizeQuasiConnectivity();

	TickPriority tickPriority();

}
