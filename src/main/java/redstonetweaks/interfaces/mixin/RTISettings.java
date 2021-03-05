package redstonetweaks.interfaces.mixin;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.world.TickPriority;

public interface RTISettings {
	
	public Settings delayOverride(Function<Integer, Integer> delayOverride);
	
	public Function<Integer, Integer> getDelayOverride();
	
	public Settings tickPriorityOverride(Function<TickPriority, TickPriority> tickPriorityOverride);
	
	public Function<TickPriority, TickPriority> getTickPriorityOverride();
	
	public Settings forceMicroTickMode(Supplier<Boolean> forceMicroTickMode);
	
	public Supplier<Boolean> getForceMicroTickMode();
	
}
