package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.state.property.Properties;

import redstonetweaks.helper.PressurePlateHelper;
import redstonetweaks.setting.SettingsPack;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements PressurePlateHelper {
	
	@Override
	public SettingsPack getSettings(BlockState state) {
		return state.getMaterial() == Material.STONE ? STONE_PRESSURE_PLATE : WOODEN_PRESSURE_PLATE;
	}
	
	@Override
	public int getWeakPower(BlockState state) {
		return state.get(Properties.POWERED) ? getSettings(state).get(WEAK_POWER) : 0;
	}

	@Override
	public int getStrongPower(BlockState state) {
		return state.get(Properties.POWERED) ? getSettings(state).get(STRONG_POWER) : 0;
	}
}
