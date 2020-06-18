package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.LavaFluid;

import redstonetweaks.setting.Settings;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int getLavaNetherDelay(int oldDelay) {
		return (int)Settings.lavaNetherDelay.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 30))
	private int getLavaDefaultDelay(int oldDelay) {
		return (int)Settings.lavaDefaultDelay.get();
	}
}
