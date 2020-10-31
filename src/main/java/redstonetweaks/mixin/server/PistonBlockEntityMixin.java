package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.setting.Settings;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements RTIPistonBlockEntity {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private float progress;
	@Shadow private BlockState pushedBlock;
	@Shadow private boolean source;
	
	private BlockEntity movedBlockEntity;
	private BlockState stationaryState;
	private boolean isMovedByStickyPiston;
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Inject(method = "getProgress", cancellable = true, at = @At(value = "HEAD"))
	private void onGetProgressInjectAtReturn(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (!((RTIWorld)world).tickWorldsNormally()) {
			int speed = getSpeed();
			
			cir.setReturnValue(MathHelper.clamp(lastProgress + 0.2F / speed, 0, speed));
			cir.cancel();
		}
	}
	
	@Redirect(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onFinishRedirectPostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		mergeStationaryState();
		
		return Block.postProcessState(pushedBlock, world, pos);
	}
	
	@Inject(method = "finish", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onFinishInjectAfterSetBlockState(CallbackInfo ci) {
		placeMovedBlockEntity();
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onTickRedirectPostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		mergeStationaryState();
		
		return Block.postProcessState(pushedBlock, world, pos);
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onTickInjectAfterSetBlockState(CallbackInfo ci) {
		placeMovedBlockEntity();
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5f))
	private float tickIncrementProgress(float oldIncrementValue) {
		int speed = getSpeed();
		return speed == 0 ? 1.0f : 1.0f / speed;
	}
	
	@Inject(method = "fromTag", at = @At(value = "RETURN"))
	private void onFromTagInjectAtReturn(BlockState state, CompoundTag tag, CallbackInfo ci) {
		isMovedByStickyPiston = tag.contains("isMovedByStickyPiston") ? tag.getBoolean("isMovedByStickyPiston") : false;

		if (tag.contains("movedBlockEntity")) {
			if (pushedBlock.getBlock() instanceof BlockEntityProvider) {
				movedBlockEntity = ((BlockEntityProvider)pushedBlock.getBlock()).createBlockEntity(world);
			}
			if (movedBlockEntity != null) {
				movedBlockEntity.fromTag(pushedBlock, tag.getCompound("movedBlockEntity"));
			}
		}
	}
	
	@Inject(method = "toTag", at = @At(value = "RETURN"))
	private void onToTagInjectAtReturn(CompoundTag tag, CallbackInfoReturnable<?> cir) {
		tag.putBoolean("isMovedByStickyPiston", isMovedByStickyPiston);
		
		if (movedBlockEntity != null) {
			tag.put("movedBlockEntity", movedBlockEntity.toTag(new CompoundTag()));
		}
	}
	
	@Override
	public boolean isMovedByStickyPiston() {
		return isMovedByStickyPiston;
	}
	
	@Override
	public void setIsMovedByStickyPiston(boolean isMovedByStickyPiston) {
		this.isMovedByStickyPiston = isMovedByStickyPiston;
	}
	
	@Override
	public void setMovedBlockEntity(BlockEntity blockEntity) {
		movedBlockEntity = blockEntity;
	}
	
	@Override
	public BlockEntity getMovedBlockEntity() {
		return movedBlockEntity;
	}
	
	@Override
	public void setStationaryState(BlockState state) {
		stationaryState = state;
	}
	
	@Override
	public BlockState getStationaryState() {
		return stationaryState;
	}
	
	private int getSpeed() {
		return extending ? PistonHelper.speedRisingEdge(isMovedByStickyPiston) : PistonHelper.speedFallingEdge(isMovedByStickyPiston);
	}
	
	private void mergeStationaryState() {
		if (Settings.Global.MERGE_SLABS.get() && stationaryState != null) {
			if (SlabHelper.isSlab(pushedBlock) && stationaryState.isOf(pushedBlock.getBlock())) {
				pushedBlock = pushedBlock.with(Properties.SLAB_TYPE, SlabType.DOUBLE);
			}
		}
	}
	
	private void placeMovedBlockEntity() {
		if (movedBlockEntity != null) {
			((RTIWorld)world).addMovedBlockEntity(pos, movedBlockEntity);
		}
	}
}
