package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.WaterFluid;

import redstonetweaks.setting.Settings;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getTickRateDelay(int oldDelay) {
		return (int)Settings.waterDelay.get();
	}
}
