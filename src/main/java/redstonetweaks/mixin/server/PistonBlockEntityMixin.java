package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import redstonetweaks.helper.PistonBlockEntityHelper;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.WorldHelper;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements PistonBlockEntityHelper {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private float progress;
	@Shadow private BlockState pushedBlock;
	@Shadow private boolean source;
	
	private BlockEntity pushedBlockEntity;
	private boolean isMovedByStickyPiston;
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(method = "getProgress", at = @At(value = "RETURN"), cancellable = true)
	private void onGetProgressInjectAtReturn(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (!((WorldHelper)world).tickWorldsNormally()) {
			int pistonSpeed = getPistonSpeed();
			
			cir.setReturnValue(MathHelper.clamp(lastProgress + 0.2F / pistonSpeed, 0, pistonSpeed));
			cir.cancel();
		}
	}
	
	@Inject(method = "finish", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onFinishInjectAfterSetBlockState(CallbackInfo ci) {
		setPushedBlockEntity();
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onTickInjectAfterSetBlockState(CallbackInfo ci) {
		setPushedBlockEntity();
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
	private float tickIncrementProgress(float oldIncrementValue) {
		int speed = getPistonSpeed();
		return speed == 0 ? 1.0f : 1.0f / speed;
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
	public void setPushedBlockEntity(BlockEntity pushedBlockEntity) {
		this.pushedBlockEntity = pushedBlockEntity;
	}
	
	@Override
	public void setIsMovedByStickyPiston(boolean isMovedByStickyPiston) {
		this.isMovedByStickyPiston = isMovedByStickyPiston;
	}
	
	private int getPistonSpeed() {
		return extending ? PistonHelper.speedRisingEdge(isMovedByStickyPiston) : PistonHelper.speedFallingEdge(isMovedByStickyPiston);
	}
	
	private void setPushedBlockEntity() {
		if (pushedBlockEntity != null) {
			pushedBlockEntity.cancelRemoval();
			
			// We need to remove the current block entity that
			// the block will have created itself upon being placed.
			world.removeBlockEntity(pos);
			world.setBlockEntity(pos, pushedBlockEntity);
			
			world.updateComparators(pos, pushedBlock.getBlock());
		}
	}
}
