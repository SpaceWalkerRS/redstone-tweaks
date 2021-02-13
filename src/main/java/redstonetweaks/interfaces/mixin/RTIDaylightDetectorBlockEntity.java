package redstonetweaks.interfaces.mixin;

import net.minecraft.block.BlockState;

public interface RTIDaylightDetectorBlockEntity {
	
	public void setPower(int newPower);
	
	public int getPower();
	
	public void ensureCorrectPower(BlockState state);
	
}
