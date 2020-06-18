package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.PistonBlockEntity;
import redstonetweaks.setting.Settings;

@Mixin(PistonBlockEntity.class)
public class PistonBlockEntityMixin {
	
	@Shadow private boolean extending;
	
	// To change the speed of pistons, we allow the maximum progress
	// of the piston block entity to increment beyond 1.
	// That means we need to change the values against which the
	// progress is checked in several places.
	
	@Inject(method = "getAmountExtended", at = @At(value = "HEAD"), cancellable = true)
	private void onGetAmountExtended(float progress, CallbackInfoReturnable<Float> cir) {
		int pistonSpeed = getPistonDelay();
		float newProgress = pistonSpeed == 0 ? 1.0f : progress / pistonSpeed;
		cir.setReturnValue(extending ? newProgress - 1.0f : 1.0f - newProgress);
		cir.cancel();
	}
	
	@ModifyConstant(method = "finish", constant = @Constant(floatValue = 1.0f), allow = 2)
	private float finishMaxProgress(float maxProgress) {
		int pistonDelay = getPistonDelay();
		return (float) (pistonDelay == 0? 1 : pistonDelay);
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.0f), allow = 3)
	private float tickMaxProgress(float maxProgress) {
		return (float) getPistonDelay();
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
	private float tickIncrementProgress(float oldIncrementValue) {
		return 1.0f;
	}
	
	@ModifyConstant(method = "getCollisionShape", constant = @Constant(doubleValue = 1.0D), allow = 3)
	private double getCollisionShapeMaxProgress(double maxProgress) {
		return (double) getPistonDelay();
	}
	
	@ModifyConstant(method = "getCollisionShape", constant = @Constant(floatValue = 4.0f))
	private float getCollisionShapePistonHeadMaxProgress(float maxProgress) {
		return (float) getPistonDelay();
	}
	
	private int getPistonDelay() {
		int delay = (int)Settings.pistonDelay.get();
		int delayMultiplier =(int)Settings.delayMultiplier.get();
		return delay * delayMultiplier;
	}
}
