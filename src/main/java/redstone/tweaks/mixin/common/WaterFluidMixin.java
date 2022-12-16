package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.ticks.TickPriority;

import redstone.tweaks.Tweaks;
import redstone.tweaks.interfaces.mixin.FluidOverrides;

@Mixin(WaterFluid.class)
public class WaterFluidMixin implements FluidOverrides {

	@Inject(
		method = "getTickDelay",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelay(LevelReader level, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(Tweaks.Water.delay());
	}

	@Override
	public TickPriority tickPriority() {
		return Tweaks.Lava.tickPriority();
	}
}
