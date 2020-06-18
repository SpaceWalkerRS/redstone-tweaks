package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.WeightedPressurePlateBlock;

import redstonetweaks.setting.Settings;

@Mixin(WeightedPressurePlateBlock.class)
public class WeightedPressurePlateBlockMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int getWeightedPressurePlateDelay(int oldDelay) {
		return (int)Settings.weightedPressurePlateDelay.get();
	}
}
