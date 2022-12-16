package redstone.tweaks.mixin.common.delay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(LavaFluid.class)
public class LavaFluidMixin implements FluidOverrides {

	@ModifyConstant(
		method = "getTickDelay",
		constant = @Constant(
			intValue = 30
		)
	)
	private int rtTweakDelay(int delay) {
		return Tweaks.Lava.delay();
	}

	@ModifyConstant(
		method = "getTickDelay",
		constant = @Constant(
			intValue = 10
		)
	)
	private int rtTweakNetherDelay(int delay) {
		return Tweaks.Lava.delayNether();
	}

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Lava.tickPriority();
	}
}
