package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.state.property.Properties;

import redstonetweaks.helper.PressurePlateHelper;

@Mixin(PressurePlateBlock.class)
public class PressurePlateBlockMixin implements PressurePlateHelper {
	
	@Override
	public int getWeakPower(BlockState state) {
		return state.get(Properties.POWERED) ? BLOCK_TO_SETTINGS_PACK.get(state.getBlock()).get(WEAK_POWER) : 0;
	}

	@Override
	public int getStrongPower(BlockState state) {
		return state.get(Properties.POWERED) ? BLOCK_TO_SETTINGS_PACK.get(state.getBlock()).get(STRONG_POWER) : 0;
	}
}
