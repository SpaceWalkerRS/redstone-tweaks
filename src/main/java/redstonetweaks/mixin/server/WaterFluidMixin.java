package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.TickPriority;
import redstonetweaks.interfaces.RTIFluid;
import redstonetweaks.setting.Tweaks;

@Mixin(WaterFluid.class)
public class WaterFluidMixin implements RTIFluid {

	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getWaterDelay(int oldDelay) {
		return Tweaks.Water.DELAY.get();
	}
	
	@Override
	public TickPriority getTickPriority() {
		return Tweaks.Water.TICK_PRIORITY.get();
	}
}
