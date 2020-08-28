package redstonetweaks.helper;

import net.minecraft.block.BlockState;

import redstonetweaks.setting.SettingsPack;

public interface PressurePlateHelper {
	
	public SettingsPack getSettings(BlockState state);
	
	public int getWeakPower(BlockState state);
	
	public int getStrongPower(BlockState state);
	
}
