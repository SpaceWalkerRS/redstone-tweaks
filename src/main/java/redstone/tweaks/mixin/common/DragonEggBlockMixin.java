package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.block.DragonEggBlock;

import redstone.tweaks.Tweaks;

@Mixin(DragonEggBlock.class)
public class DragonEggBlockMixin {

	@Inject(
		method = "getDelayAfterPlace",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void rtTweakDelay(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(Tweaks.DragonEgg.delay());
	}
}
