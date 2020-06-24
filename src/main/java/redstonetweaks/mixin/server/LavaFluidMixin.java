package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.lavaDefaultDelay;
import static redstonetweaks.setting.Settings.lavaNetherDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.LavaFluid;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int onGetTickRateNetherDelay(int oldValue) {
		return lavaNetherDelay.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 30))
	private int onGetTickRateDefaultDelay(int oldValue) {
		return lavaDefaultDelay.get();
	}
}
