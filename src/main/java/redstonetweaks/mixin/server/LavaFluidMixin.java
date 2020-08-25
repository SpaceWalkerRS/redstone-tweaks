package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.LavaFluid;
import net.minecraft.world.TickPriority;
import redstonetweaks.helper.FluidHelper;

@Mixin(LavaFluid.class)
public class LavaFluidMixin implements FluidHelper {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int onGetTickRateNetherDelay(int oldValue) {
		return LAVA.get(NETHER_DELAY);
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 30))
	private int onGetTickRateDefaultDelay(int oldValue) {
		return LAVA.get(DELAY);
	}
	
	@Override
	public TickPriority getTickPriority() {
		return LAVA.get(TICK_PRIORITY);
	}
}
