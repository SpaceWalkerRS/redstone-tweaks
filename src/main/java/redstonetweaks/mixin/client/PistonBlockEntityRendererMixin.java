package redstonetweaks.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;

import redstonetweaks.setting.Settings;

@Mixin(PistonBlockEntityRenderer.class)
public class PistonBlockEntityRendererMixin {
	
	// To change the speed of pistons, we allow the maximum progress
	// of the piston block entity to increment beyond 1.
	// That means we need to change the values against which the
	// progress is checked in several places.
	
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 1.0f))
	private float renderMaxProgress(float maxProgress) {
		return (float) getPistonDelay();
	}
	
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 4.0f))
	private float renderShortPistonHead1(float maxrogress) {
		return (float) (getPistonDelay() / 2);
	}
	
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 0.5f))
	private float renderShortPistonHead2(float maxProgress) {
		return (float) (getPistonDelay() / 2);
	}
	
	private int getPistonDelay() {
		int delay = (int)Settings.pistonDelay.get();
		int delayMultiplier = (int)Settings.delayMultiplier.get();
		return delay * delayMultiplier;
	}
}
