package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.world.TickPriority;
import redstonetweaks.helper.FluidHelper;

@Mixin(FlowableFluid.class)
public class FlowableFluidMixin implements FluidHelper {
	
	@Override
	public TickPriority getTickPriority() {
		return TickPriority.NORMAL;
	}
}
