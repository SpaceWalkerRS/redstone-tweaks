package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.TickPriority;
import redstonetweaks.helper.FluidHelper;

@Mixin(WaterFluid.class)
public class WaterFluidMixin implements FluidHelper {

	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 5))
	private int getWaterDelay(int oldDelay) {
		return WATER.get(DELAY);
	}
	
	@Override
	public TickPriority getTickPriority() {
		return WATER.get(TICK_PRIORITY);
	}
}
