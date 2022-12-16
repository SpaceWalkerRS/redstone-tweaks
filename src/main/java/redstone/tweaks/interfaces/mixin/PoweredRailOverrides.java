package redstone.tweaks.interfaces.mixin;

import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;

public interface PoweredRailOverrides extends BlockOverrides {

	boolean isTicking();

	default boolean isActivatorRail() {
		return block() == Blocks.ACTIVATOR_RAIL;
	}

	default int powerLimit() {
		return isActivatorRail() ? Tweaks.ActivatorRail.powerLimit() : Tweaks.PoweredRail.powerLimit();
	}

	default Map<Direction, Boolean> quasiConnectivity() {
		return isActivatorRail() ? Tweaks.ActivatorRail.quasiConnectivity() : Tweaks.PoweredRail.quasiConnectivity();
	}

	default boolean randomizeQuasiConnectivity() {
		return isActivatorRail() ? Tweaks.ActivatorRail.randomizeQuasiConnectivity() : Tweaks.PoweredRail.randomizeQuasiConnectivity();
	}

	default boolean lazyRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.lazyRisingEdge() : Tweaks.PoweredRail.lazyRisingEdge();
	}

	default boolean lazyFallingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.lazyFallingEdge() : Tweaks.PoweredRail.lazyFallingEdge();
	}

	default int delayRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.delayRisingEdge() : Tweaks.PoweredRail.delayRisingEdge();
	}

	default int delayFallingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.delayFallingEdge() : Tweaks.PoweredRail.delayFallingEdge();
	}

	default TickPriority tickPriorityRisingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.tickPriorityRisingEdge() : Tweaks.PoweredRail.tickPriorityRisingEdge();
	}

	default TickPriority tickPriorityFallingEdge() {
		return isActivatorRail() ? Tweaks.ActivatorRail.tickPriorityFallingEdge() : Tweaks.PoweredRail.tickPriorityFallingEdge();
	}
}
