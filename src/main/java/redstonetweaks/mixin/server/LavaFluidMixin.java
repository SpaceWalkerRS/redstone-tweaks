package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.fluid.LavaFluid;
import net.minecraft.world.TickPriority;
import redstonetweaks.interfaces.mixin.RTIFluid;
import redstonetweaks.setting.Tweaks;

@Mixin(LavaFluid.class)
public class LavaFluidMixin implements RTIFluid {
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 10))
	private int onGetTickRateNetherDelay(int oldValue) {
		return Tweaks.Lava.DELAY_NETHER.get();
	}
	
	@ModifyConstant(method = "getTickRate", constant = @Constant(intValue = 30))
	private int onGetTickRateDefaultDelay(int oldValue) {
		return Tweaks.Lava.DELAY_DEFAULT.get();
	}
	
	@Override
	public TickPriority getTickPriority() {
		return Tweaks.Lava.TICK_PRIORITY.get();
	}
}
