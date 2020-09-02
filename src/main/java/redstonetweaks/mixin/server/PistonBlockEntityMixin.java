package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import redstonetweaks.helper.PistonBlockEntityHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.setting.IntegerProperty;
import redstonetweaks.setting.Setting;
import redstonetweaks.setting.SettingsPack;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements PistonBlockEntityHelper {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private float progress;
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
	
	@Inject(method = "getProgress", at = @At(value = "RETURN"), cancellable = true)
	private void onGetProgressInjectAtReturn(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (((WorldHelper)world).tickWorldsNormally()) {
			float progress = cir.getReturnValueF();
			float speed = getPistonSpeed();
			
			cir.setReturnValue(speed == 0 ? 1.0f : progress / speed);
		} else {
			cir.setReturnValue(lastProgress);
		}
		cir.cancel();
	}
	
	@Redirect(method = "pushEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;progress:F"))
	private float onPushEntitiesRedirectProgress(PistonBlockEntity pistonBlockEntity, float nextProgress) {
		float speed = getPistonSpeed();
		return speed == 0 ? 1.0f : progress / speed;
	}
	
	@Redirect(method = "method_23674", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;progress:F"))
	private float onMethod_23674RedirectProgress(PistonBlockEntity pistonBlockEntity, float f) {
		float speed = getPistonSpeed();
		return speed == 0 ? 1.0f : progress / speed;
	}
	
	@Redirect(method = "offsetHeadBox", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;progress:F"))
	private float onOffsetHeadBoxRedirectProgress(PistonBlockEntity pistonBlockEntity, Box box) {
		float speed = getPistonSpeed();
		return speed == 0 ? 1.0f : progress / speed;
	}
	
	@ModifyConstant(method = "finish", constant = @Constant(floatValue = 1.0f), allow = 2)
	private float finishMaxProgress(float maxProgress) {
		int speed = getPistonSpeed();
		return speed == 0 ? 1.0f : speed;
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.0f), allow = 3)
	private float tickMaxProgress(float maxProgress) {
		return getPistonSpeed();
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
	private float tickIncrementProgress(float oldIncrementValue) {
		return 1.0f;
	}
	
	@Redirect(method = "getCollisionShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;progress:F"))
	private float onGetCollisionShapeRedirectProgress(PistonBlockEntity pistonBlockEntity, BlockView world, BlockPos pos) {
		float speed = getPistonSpeed();
		return speed == 0 ? 1.0f : progress / speed;
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
	
	private int getPistonSpeed() {
		SettingsPack settings = isMovedByStickyPiston ? STICKY_PISTON : NORMAL_PISTON;
		Setting<IntegerProperty> speedSetting = extending ? RISING_SPEED : FALLING_SPEED;
		
		return settings.get(speedSetting);
	}
}
