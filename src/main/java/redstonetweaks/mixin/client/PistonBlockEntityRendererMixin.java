package redstonetweaks.mixin.client;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

import redstonetweaks.helper.PistonBlockEntityHelper;
import redstonetweaks.setting.IntegerProperty;
import redstonetweaks.setting.Setting;
import redstonetweaks.setting.SettingsPack;

@Mixin(PistonBlockEntityRenderer.class)
public class PistonBlockEntityRendererMixin {
	
	private boolean sticky;
	private boolean extending;
	
	@Inject(method = "render", at = @At(value = "HEAD"))
	private void atRenderInjectAtHead(PistonBlockEntity pistonBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
		sticky = ((PistonBlockEntityHelper)pistonBlockEntity).isMovedByStickyPiston();
		extending = pistonBlockEntity.isExtending();
	}
	
	// To change the speed of pistons, we allow the progress
	// of the piston block entity to increment beyond 1.
	// That means we need to change the values against which the
	// progress is checked in several places.
	
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 4.0f))
	private float renderShortPistonHead1(float maxrogress) {
		return getPistonDelay() / 2;
	}
	
	@ModifyConstant(method = "render", constant = @Constant(floatValue = 0.5f))
	private float renderShortPistonHead2(float maxProgress) {
		return getPistonDelay() / 2;
	}
	
	private int getPistonDelay() {
		SettingsPack settings = sticky ? STICKY_PISTON : NORMAL_PISTON;
		Setting<IntegerProperty> speedSetting = extending ? RISING_SPEED : FALLING_SPEED;
		return GLOBAL.get(DELAY_MULTIPLIER) * settings.get(speedSetting);
	}
}
