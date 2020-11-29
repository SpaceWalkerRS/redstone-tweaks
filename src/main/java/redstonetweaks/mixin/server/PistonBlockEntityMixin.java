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
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.interfaces.RTIWorld;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements RTIPistonBlockEntity {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private float progress;
	@Shadow private BlockState pushedBlock;
	@Shadow private boolean source;
	
	private BlockEntity movedBlockEntity;
	private PistonBlockEntity parentPistonBlockEntity;
	
	private boolean isMovedByStickyPiston;
	private boolean isMergingSlabs;
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Shadow public abstract void finish();
	
	@Inject(method = "getProgress", cancellable = true, at = @At(value = "HEAD"))
	private void onGetProgressInjectAtReturn(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (!((RTIWorld)world).normalWorldTicks()) {
			int speed = getSpeed();
			
			cir.setReturnValue(MathHelper.clamp(lastProgress + 0.2F / speed, 0, speed));
			cir.cancel();
		}
	}
	
	@Redirect(method = "pushEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onPushEntitiesRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedState();
	}
	
	@Redirect(method = "method_23674", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onMethod_23674RedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedState();
	}
	
	@Redirect(method = "isPushingHoneyBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onIsPushingHoneyBlockRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedState();
	}
	
	@Inject(method = "finish", cancellable = true, at = @At(value = "HEAD"))
	private void onFinishInjectAtHead(CallbackInfo ci) {
		if (parentPistonBlockEntity != null) {
			((RTIPistonBlockEntity)parentPistonBlockEntity).setPushedBlock(pushedBlock);
			((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onFinishRedirectPostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		mergeSlabs();
		
		return Block.postProcessState(pushedBlock, world, pos);
	}
	
	@Inject(method = "finish", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onFinishInjectAfterSetBlockState(CallbackInfo ci) {
		placeMovedBlockEntity();
	}
	
	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void onTickInjectAtHead(CallbackInfo ci) {
		if (movedBlockEntity instanceof PistonBlockEntity) {
			((PistonBlockEntity)movedBlockEntity).tick();
		}
	}
	
	@Inject(method = "tick", cancellable = true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;isClient:Z"))
	private void onTickInjectBeforeIsClient(CallbackInfo ci) {
		if (parentPistonBlockEntity != null) {
			((RTIPistonBlockEntity)parentPistonBlockEntity).setPushedBlock(pushedBlock);
			((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onTickRedirectPostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		mergeSlabs();
		
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
			if (pushedBlock.getBlock().hasBlockEntity()) {
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
	
	@Inject(method = "getCollisionShape", cancellable = true, at = @At(value = "HEAD"))
	private void onGetCollisionShapeInjectAtHead(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
		if (getMovedState().isOf(Blocks.MOVING_PISTON)) {
			cir.setReturnValue(VoxelShapes.empty());
			cir.cancel();
		}
	}
	
	@Redirect(method = "getCollisionShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onGetCollisionShapeRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedState();
	}
	
	@Override
	public void setLocation(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos.toImmutable();
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setLocation(getWorld(), getPos());
		}
	}
	
	@Override
	public void setPos(BlockPos pos) {
		this.pos = pos.toImmutable();
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setPos(getPos());
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
	public void setPushedBlock(BlockState state) {
		pushedBlock = state;
	}
	
	@Override
	public BlockState getMovedState() {
		if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
			return ((RTIPistonBlockEntity)movedBlockEntity).getMovedState();
		}
		return pushedBlock;
	}
	
	@Override
	public void setMovedBlockEntity(BlockEntity blockEntity) {
		movedBlockEntity = blockEntity;
		
		if (movedBlockEntity instanceof PistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setParentPistonBlockEntity((PistonBlockEntity)(Object)this);
		}
	}
	
	@Override
	public void setParentPistonBlockEntity(PistonBlockEntity pistonBlockEntity) {
		parentPistonBlockEntity = pistonBlockEntity;
	}
	
	@Override
	public BlockEntity getMovedBlockEntity() {
		return movedBlockEntity;
	}
	
	@Override
	public void setIsMergingSlabs(boolean isMergingSlabs) {
		this.isMergingSlabs = isMergingSlabs;
	}
	
	@Override
	public boolean isMergingSlabs() {
		return isMergingSlabs;
	}
	
	@Override
	public void finishSource() {
		if (source) {
			// We have to set source to false so that the pushed block is not replaced by air
			source = false;
			finish();
			source = true;
		}
	}
	
	private int getSpeed() {
		return extending ? PistonHelper.speedRisingEdge(isMovedByStickyPiston) : PistonHelper.speedFallingEdge(isMovedByStickyPiston);
	}
	
	private void mergeSlabs() {
		if (isMergingSlabs() && SlabHelper.isSlab(pushedBlock)) {
			pushedBlock = pushedBlock.with(Properties.SLAB_TYPE, SlabType.DOUBLE);
		}
	}
	
	private void placeMovedBlockEntity() {
		if (movedBlockEntity != null) {
			((RTIWorld)world).addMovedBlockEntity(pos, movedBlockEntity);
		}
	}
}
