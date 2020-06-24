package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.waterDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.WaterFluid;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getWaterDelay(int oldDelay) {
		return waterDelay.get();
	}
}
