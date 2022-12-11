package redstone.tweaks.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ObserverBlock;

import redstone.tweaks.Tweaks;

@Mixin(ObserverBlock.class)
public class ObserverBlockMixin {

	@Inject(
		method = "startSignal",
		cancellable = true,
		at = @At(
			value = "HEAD"
		)
	)
	private void disableObservers(LevelAccessor level, BlockPos pos, CallbackInfo ci) {
		if (Tweaks.Observer.disable()) {
			ci.cancel();
		}
	}
}
