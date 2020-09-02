package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;

import redstonetweaks.helper.PistonBlockEntityHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.IntegerProperty;
import redstonetweaks.setting.Setting;
import redstonetweaks.setting.SettingsPack;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements PistonBlockEntityHelper {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private BlockState pushedBlock;
	@Shadow private boolean source;
	
	private boolean isMovedByStickyPiston;
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	// To change the speed of pistons, we allow the maximum progress
	// of the piston block entity to increment beyond 1.
	// That means we need to change the values against which the
	// progress is checked in several places.
	
	@Inject(method = "getProgress", at = @At(value = "HEAD"), cancellable = true)
	private void onGetProgressInjectAtHead(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (!((WorldHelper)world).tickWorldsNormally()) {
			cir.setReturnValue(lastProgress);
			cir.cancel();
		}
	}
	
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
		return pistonDelay == 0 ? 1.0f : pistonDelay;
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.0f), allow = 3)
	private float tickMaxProgress(float maxProgress) {
		return getPistonDelay();
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
	private float tickIncrementProgress(float oldIncrementValue) {
		return 1.0f;
	}
	
	@ModifyConstant(method = "getCollisionShape", constant = @Constant(doubleValue = 1.0D))
	private double getCollisionShapeMaxProgress(double maxProgress) {
		return getPistonDelay();
	}
	
	@Inject(method = "fromTag", at = @At(value = "RETURN"))
	private void onFromTagInjectAtReturn(BlockState state, CompoundTag tag, CallbackInfo ci) {
		isMovedByStickyPiston = tag.contains("isMovedByStickyPiston") ? tag.getBoolean("isMovedByStickyPiston") : false;
	}
	
	@Inject(method = "toTag", at = @At(value = "RETURN"))
	private void onToTagInjectAtReturn(CompoundTag tag, CallbackInfoReturnable<?> cir) {
		tag.putBoolean("isMovedByStickyPiston", isMovedByStickyPiston);
	}
	
	@Override
	public boolean isMovedByStickyPiston() {
		return isMovedByStickyPiston;
	}
	
	@Override
	public void setIsMovedByStickyPiston(boolean newValue) {
		isMovedByStickyPiston = newValue;
	}
	
	private int getPistonDelay() {
		SettingsPack settings = isMovedByStickyPiston ? STICKY_PISTON : NORMAL_PISTON;
		Setting<IntegerProperty> speedSetting = extending ? RISING_SPEED : FALLING_SPEED;
		
		return settings.get(speedSetting);
	}
}
