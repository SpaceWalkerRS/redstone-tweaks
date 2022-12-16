package redstone.tweaks.interfaces.mixin;

import net.minecraft.world.ticks.TickPriority;

public interface PressurePlateOverrides extends BlockOverrides {

	int delayRisingEdge();

	int delayFallingEdge();

	TickPriority tickPriorityRisingEdge();

	TickPriority tickPriorityFallingEdge();

}
