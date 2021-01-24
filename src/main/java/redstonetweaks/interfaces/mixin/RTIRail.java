package redstonetweaks.interfaces.mixin;

import net.minecraft.world.TickPriority;

import redstonetweaks.setting.types.DirectionToBooleanSetting;

public interface RTIRail {
	
	public DirectionToBooleanSetting getQC();
	
	public boolean randQC();
	
	public int getDelay();
	
	public TickPriority getTickPriority();
	
}
