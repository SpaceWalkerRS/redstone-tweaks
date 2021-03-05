package redstonetweaks.interfaces.mixin;

import net.minecraft.world.TickPriority;

public interface RTIAbstractBlockState {
	
	public int delayOverride(int delay);
	
	public TickPriority tickPriorityOverride(TickPriority tickPriority);
	
	public boolean forceMicroTickMode();
	
}
