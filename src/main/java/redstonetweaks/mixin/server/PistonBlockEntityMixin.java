package redstonetweaks.mixin.server;

import java.util.List;

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
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.RedstoneTweaks;
import redstonetweaks.block.piston.MovedBlock;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.mixin.RTIEntity;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;
import redstonetweaks.util.BoxUtils;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin extends BlockEntity implements RTIPistonBlockEntity {
	
	@Shadow private boolean extending;
	@Shadow private float lastProgress;
	@Shadow private float progress;
	@Shadow private BlockState pushedBlock;
	@Shadow private boolean source;
	@Shadow private Direction facing;
	
	private BlockEntity movedBlockEntity;
	// The block state and block entity that the moved block state and block entity merge into
	private BlockState mergingState;
	private BlockEntity mergingBlockEntity;
	private PistonBlockEntity parentPistonBlockEntity;
	
	private boolean sticky;
	private boolean isMerging;
	private boolean sourceIsMoving;
	private boolean hasChildPistonBlockEntity;
	private int speed;
	// G4mespeed compatibility
	// Make sure pistons animate at the correct speed
	private float numberOfSteps;
	private float amountPerStep;
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Shadow protected abstract float getAmountExtended(float progress);
	@Shadow protected abstract BlockState getHeadBlockState();
	@Shadow public abstract void finish();
	@Shadow public abstract Direction getMovementDirection();
	@Shadow protected native boolean method_23671(Box box, Entity entity);
	
	@Redirect(method = "getAmountExtended", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;extending:Z"))
	private boolean onGetAmountExtendedRedirectExtending(PistonBlockEntity pistonBlockEntity, float tickDelta) {
		return sourceIsMoving ? !extending : extending;
	}
	
	@Redirect(method = "pushEntities", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onPushEntitiesRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedMovingState();
	}
	
	@Redirect(method = "method_23674", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onMethod_23674RedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedMovingState();
	}
	
	@Redirect(method = "isPushingHoneyBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onIsPushingHoneyBlockRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedMovingState();
	}
	
	@Redirect(method = "getMovementDirection", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;extending:Z"))
	private boolean onGetMovementDirectionRedirectExtending(PistonBlockEntity pistonBlockEntity) {
		return extending != sourceIsMoving;
	}
	
	@Inject(method = "finish", cancellable = true, at = @At(value = "HEAD"))
	private void onFinishInjectAtHead(CallbackInfo ci) {
		if (parentPistonBlockEntity != null) {
			prepareBlockPlacement();
			
			if (isMerging) {
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingState(pushedBlock);
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingBlockEntity(movedBlockEntity);
			} else {
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedState(pushedBlock);
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
				
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingState(mergingState);
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingBlockEntity(mergingBlockEntity);
			}
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "finish", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onFinishInjectBeforePostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		prepareBlockPlacement();
		
		return Block.postProcessState(pushedBlock, world, pos);
	}
	
	@Inject(method = "finish", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onFinishInjectBeforeSetBlockState(CallbackInfo ci) {
		if (pushedBlock.isOf(Blocks.MOVING_PISTON)) {
			// This makes sure the block entity is placed properly
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 16);
		}
		
		queueMovedBlockEntityPlacement();
	}
	
	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void onTickInjectAtHead(CallbackInfo ci) {
		if (movedBlockEntity instanceof Tickable) {
			((Tickable)movedBlockEntity).tick();
		}
		if (mergingBlockEntity instanceof Tickable) {
			((Tickable)mergingBlockEntity).tick();
		}
		
		if (!isMerging() && lastProgress >= 0.5F && PistonHelper.isPistonHead(pushedBlock) && pushedBlock.get(Properties.SHORT)) {
			setMovedState(pushedBlock.with(Properties.SHORT, false));
		}
	}
	
	@Inject(method = "tick", cancellable = true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;isClient:Z"))
	private void onTickInjectBeforeIsClient(CallbackInfo ci) {
		if (parentPistonBlockEntity != null) {
			prepareBlockPlacement();
			
			if (isMerging) {
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingState(pushedBlock);
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingBlockEntity(movedBlockEntity);
			} else {
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedState(pushedBlock);
				((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(movedBlockEntity);
				
				if (mergingState != null) {
					((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingState(mergingState);
					((RTIPistonBlockEntity)parentPistonBlockEntity).setMergingBlockEntity(mergingBlockEntity);
				}
			}
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onTickInjectBeforePostProcessState(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos) {
		prepareBlockPlacement();
		
		return Block.postProcessState(pushedBlock, world, pos);
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onTickInjectBeforeSetBlockState(CallbackInfo ci) {
		if (pushedBlock.isOf(Blocks.MOVING_PISTON)) {
			// This makes sure the block entity is placed properly
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 16);
		}
		
		queueMovedBlockEntityPlacement();
		
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onTickInjectAfterSetBlockState(CallbackInfo ci) {
		// When a piston pushes itself backwards a short piston arm is placed so the rod does not poke out
		// of the back of the piston base. When the extension is finished the long arm needs to be placed.
		if (sourceIsMoving && extending) {
			Direction facing = pushedBlock.get(Properties.FACING);
			BlockPos headPos = pos.offset(facing);
			BlockState pistonHead = world.getBlockState(headPos);
			
			if (PistonHelper.isPistonHead(pistonHead, facing)) {
				world.setBlockState(headPos, pistonHead.with(Properties.SHORT, false), 18);
			}
		}
	}
	
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.5F))
	private float tickIncrementProgress(float oldIncrementValue) {
		return amountPerStep;
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE",  target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushEntities(F)V"))
	private void onTickRedirectPushEntities(PistonBlockEntity pistonBlockEntity, float nextProgress) {
		moveEntities();
	}
	
	@Redirect(method = "tick", at = @At(value = "INVOKE",  target = "Lnet/minecraft/block/entity/PistonBlockEntity;method_23674(F)V"))
	private void onTickRedirectMethod_23674(PistonBlockEntity pistonBlockEntity, float nextProgress) {
		// Replaced by the redirect above
	}
	
	@Inject(method = "fromTag", at = @At(value = "RETURN"))
	private void onFromTagInjectAtReturn(BlockState state, CompoundTag tag, CallbackInfo ci) {
		sticky = tag.contains("sticky") ? tag.getBoolean("sticky") : false;
		sourceIsMoving = tag.contains("sourceIsMoving") ? tag.getBoolean("sourceIsMoving") : false;
		speed = tag.contains("speed") ? tag.getInt("speed") : 2;
		numberOfSteps = (speed == 0) ? 1.0F : speed;
		amountPerStep = 1.0F / numberOfSteps;

		if (tag.contains("movedBlockEntity")) {
			Block movedBlock = pushedBlock.getBlock();
			
			if (movedBlock.hasBlockEntity()) {
				movedBlockEntity = (movedBlock == Blocks.MOVING_PISTON) ? new PistonBlockEntity() : ((BlockEntityProvider)movedBlock).createBlockEntity(world);
				
				if (movedBlockEntity != null) {
					movedBlockEntity.fromTag(pushedBlock, tag.getCompound("movedBlockEntity"));
					
					setMovedBlockEntity(movedBlockEntity);
				}
			}
		}
		if (tag.contains("mergingState")) {
			mergingState = NbtHelper.toBlockState(tag.getCompound("mergingState"));
		}
		if (tag.contains("mergingBlockEntity")) {
			Block mergingBlock = mergingState.getBlock();
			
			if (mergingBlock.hasBlockEntity()) {
				mergingBlockEntity = (mergingBlock == Blocks.MOVING_PISTON) ? new PistonBlockEntity() : ((BlockEntityProvider)mergingBlock).createBlockEntity(world);
				
				if (mergingBlockEntity != null) {
					mergingBlockEntity.fromTag(mergingState, tag.getCompound("mergingBlockEntity"));
					
					setMergingBlockEntity(mergingBlockEntity);
				}
			}
		}
	}
	
	@Inject(method = "toTag", at = @At(value = "RETURN"))
	private void onToTagInjectAtReturn(CompoundTag tag, CallbackInfoReturnable<?> cir) {
		tag.putBoolean("sticky", sticky);
		tag.putBoolean("sourceIsMoving", sourceIsMoving);
		tag.putInt("speed", speed);
		
		if (movedBlockEntity != null) {
			tag.put("movedBlockEntity", movedBlockEntity.toTag(new CompoundTag()));
		}
		if (mergingState != null) {
			tag.put("mergingState", NbtHelper.fromBlockState(mergingState));
		}
		if (mergingBlockEntity != null) {
			tag.put("mergingBlockEntity", mergingBlockEntity.toTag(new CompoundTag()));
		}
	}
	
	@Inject(method = "getCollisionShape", cancellable = true, at = @At(value = "HEAD"))
	private void onGetCollisionShapeInjectAtHead(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
		cir.setReturnValue(getTotalCollisionShape(VoxelShapes.empty(), world, pos));
		cir.cancel();
	}
	
	@Redirect(method = "getCollisionShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onGetCollisionShapeRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedMovingState();
	}
	
	@Override
	public void setLocation(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos.toImmutable();
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setLocation(this.world, this.pos);
		}
		if (mergingBlockEntity != null) {
			mergingBlockEntity.setLocation(this.world, this.pos);
		}
	}
	
	@Override
	public void setPos(BlockPos pos) {
		this.pos = pos.toImmutable();
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setPos(this.pos);
		}
		if (mergingBlockEntity != null) {
			mergingBlockEntity.setPos(this.pos);
		}
	}
	
	@Override
	public void init() {
		speed = PistonSettings.speed(sticky, extending);
		
		if (speed == 0) {
			numberOfSteps = 1.0F;
			
			// This ensures the block entity finishes the first time it is ticked
			// Otherwise the behavior would be the same as if the speed was set to 1
			progress = 1.0F;
		} else {
			numberOfSteps = speed;
			amountPerStep = 1.0F / numberOfSteps;
		}
	}
	
	@Override
	public void setSource(boolean source) {
		this.source = source;
	}
	
	@Override
	public void finishSource() {
		// With various settings it is possible for a piston to try to drop a source piston block entity
		// and we do not want those to be set to air.
		if (source) {
			// We have to set source to false so that the pushed block is not replaced by air
			source = false;
			finish();
			source = true;
		} else {
			finish();
		}
	}
	
	@Override
	public boolean isSticky() {
		return sticky;
	}
	
	@Override
	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}
	
	@Override
	public Vec3d getTotalAmountExtended(Vec3d amountExtended, boolean ignore) {
		if (!ignore) {
			double s = getAmountExtended(progress);
			
			amountExtended = amountExtended.add(s * facing.getOffsetX(), s * facing.getOffsetY(), s * facing.getOffsetZ());
		}
		
		if (parentPistonBlockEntity == null) {
			return amountExtended;
		}
		
		return ((RTIPistonBlockEntity)parentPistonBlockEntity).getTotalAmountExtended(amountExtended, isMerging);
	}
	
	@Override
	public Vec3d getTotalStepAmount(Vec3d stepAmount, boolean ignore) {
		if (!ignore) {
			double s = amountPerStep;
			
			stepAmount = stepAmount.add(s * facing.getOffsetX(), s * facing.getOffsetY(), s * facing.getOffsetZ());
		}
		
		if (parentPistonBlockEntity == null) {
			return stepAmount;
		}
		
		return ((RTIPistonBlockEntity)parentPistonBlockEntity).getTotalStepAmount(stepAmount, isMerging);
	}
	
	@Override
	public VoxelShape getTotalCollisionShape(VoxelShape collisionShape, BlockView world, BlockPos pos) {
		VoxelShape mainShape = getMainCollisionShape(world, pos);
		VoxelShape additionalShape = getAdditionalCollisionShape(world, pos);
		
		if (!mainShape.isEmpty()) {
			collisionShape = VoxelShapes.union(collisionShape, mainShape);
		}
		if (!additionalShape.isEmpty()) {
			collisionShape = VoxelShapes.union(collisionShape, additionalShape);
		}
		
		return collisionShape;
	}
	
	@Override
	public boolean sourceIsMoving() {
		return sourceIsMoving;
	}
	
	@Override
	public void setSourceIsMoving(boolean sourceIsMoving) {
		this.sourceIsMoving = source && sourceIsMoving;
	}
	
	@Override
	public void setIsMerging(boolean isMerging) {
		this.isMerging = isMerging;
	}
	
	@Override
	public boolean isMerging() {
		return isMerging || (parentPistonBlockEntity != null && ((RTIPistonBlockEntity)parentPistonBlockEntity).isMerging());
	}
	
	@Override
	public void setMovedState(BlockState state) {
		pushedBlock = state;
	}
	
	@Override
	public BlockEntity getMovedBlockEntity() {
		return movedBlockEntity;
	}
	
	@Override
	public void setMovedBlockEntity(BlockEntity blockEntity) {
		hasChildPistonBlockEntity = false;
		movedBlockEntity = blockEntity;
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setLocation(world, pos);
			
			if (movedBlockEntity instanceof PistonBlockEntity) {
				hasChildPistonBlockEntity = true;
				
				((RTIPistonBlockEntity)movedBlockEntity).setParentPistonBlockEntity((PistonBlockEntity)(BlockEntity)this);
				((RTIPistonBlockEntity)movedBlockEntity).setIsMerging(false);
			}
		}
	}
	
	@Override
	public BlockState getMergingState() {
		return mergingState;
	}
	
	@Override
	public void setMergingState(BlockState state) {
		mergingState = state;
	}
	
	@Override
	public BlockEntity getMergingBlockEntity() {
		return mergingBlockEntity;
	}
	
	@Override
	public void setMergingBlockEntity(BlockEntity blockEntity) {
		mergingBlockEntity = blockEntity;
		
		if (mergingBlockEntity != null) {
			mergingBlockEntity.setLocation(world, pos);
			
			if (mergingBlockEntity instanceof PistonBlockEntity) {
				((RTIPistonBlockEntity)mergingBlockEntity).setParentPistonBlockEntity((PistonBlockEntity)(BlockEntity)this);
				((RTIPistonBlockEntity)mergingBlockEntity).setIsMerging(true);
			}
		}
	}
	
	@Override
	public PistonBlockEntity getParent() {
		return parentPistonBlockEntity == null ? (PistonBlockEntity)(BlockEntity)this : ((RTIPistonBlockEntity)parentPistonBlockEntity).getParent();
	}
	
	@Override
	public void setParentPistonBlockEntity(PistonBlockEntity pistonBlockEntity) {
		parentPistonBlockEntity = pistonBlockEntity;
	}
	
	@Override
	public BlockState getMovedMovingState() {
		if (hasChildPistonBlockEntity) {
			return ((RTIPistonBlockEntity)movedBlockEntity).getMovedMovingState();
		}
		
		return pushedBlock;
	}
	
	@Override
	public void setMovedMovingState(BlockState state) {
		if (hasChildPistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setMovedMovingState(state);
		} else {
			pushedBlock = state;
		}
	}
	
	@Override
	public BlockEntity getMovedMovingBlockEntity() {
		if (hasChildPistonBlockEntity) {
			BlockEntity movingBlockEntity = ((RTIPistonBlockEntity)movedBlockEntity).getMovedMovingBlockEntity();
			
			if (movingBlockEntity != null) {
				return movingBlockEntity;
			}
		}
		
		return movedBlockEntity;
	}
	
	@Override
	public void setMovedMovingBlockEntity(BlockEntity blockEntity) {
		if (hasChildPistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setMovedMovingBlockEntity(blockEntity);
		} else if (blockEntity.getType().supports(pushedBlock.getBlock())) {
			setMovedBlockEntity(blockEntity);
		}
	}
	
	@Override
	public BlockState getStateForMovement() {
		if (mergingState == null) {
			if (hasChildPistonBlockEntity) {
				return ((RTIPistonBlockEntity)movedBlockEntity).getStateForMovement();
			}
			
			return pushedBlock;
		}
		
		BlockState state = getMovedMovingState();
		
		// If two blocks are merging they should be treated as already merged
		if (SlabHelper.isSlab(state)) {
			state = state.with(Properties.SLAB_TYPE, SlabType.DOUBLE);
		} else
		if (PistonHelper.isPiston(state)) {
			state = state.with(Properties.EXTENDED, false);
		} else
		if (PistonHelper.isPistonHead(state)) {
			state = PistonHelper.getPiston(PistonHelper.isStickyHead(state), state.get(Properties.FACING), false);
		}
		
		return state;
	}
	
	@Override
	public PistonBlockEntity copy() {
		PistonBlockEntity copy = new PistonBlockEntity();
		
		copy.fromTag(pushedBlock, toTag(new CompoundTag()));
		copy.setLocation(world, pos);
		
		return copy;
	}
	
	@Override
	public MovedBlock splitDoubleSlab(SlabType keepType) {
		if (mergingState != null) {
			BlockState movingState = getMovedMovingState();
			
			if (SlabHelper.isSlab(movingState)) {
				if (movingState.get(Properties.SLAB_TYPE) != keepType) {
					return undoMerge();
				}
				
				setMergingState(null);
				setMergingBlockEntity(null);
			}
		} else if (SlabHelper.isSlab(pushedBlock)) {
			setMovedState(pushedBlock.with(Properties.SLAB_TYPE, keepType));
		} else if (hasChildPistonBlockEntity) {
			return ((RTIPistonBlockEntity)movedBlockEntity).splitDoubleSlab(keepType);
		}
		
		PistonBlockEntity parent = getParent();
		
		return new MovedBlock(parent.getCachedState(), parent);
	}
	
	@Override
	public MovedBlock detachPistonHead(Direction motionDir, boolean returnMovingPart) {
		if (mergingState != null) {
			BlockState movingState = getMovedMovingState();
			
			boolean isPiston = PistonHelper.isPiston(movingState);
			
			if (isPiston || PistonHelper.isPistonHead(movingState)) {
				if (!returnMovingPart == isPiston == (movingState.get(Properties.FACING) == motionDir)) {
					return undoMerge();
				}
				
				setMergingState(null);
				setMergingBlockEntity(null);
			}
		} else if (PistonHelper.isPiston(pushedBlock)) {
			Direction facing = pushedBlock.get(Properties.FACING);
			boolean headMoving = (facing == motionDir);
			
			BlockState state;
			if (returnMovingPart == headMoving) {
				state = PistonHelper.getPistonHead(PistonHelper.isSticky(pushedBlock), facing).with(Properties.SHORT, headMoving);
			} else {
				state = pushedBlock.with(Properties.EXTENDED, true);
			}
			
			if (source) {
				if (parentPistonBlockEntity == null) {
					return new MovedBlock(state, null);
				} else {
					((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedState(state);
					((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(null);
				}
			} else {
				setMovedState(state);
			}
		} else if (hasChildPistonBlockEntity) {
			return ((RTIPistonBlockEntity)movedBlockEntity).detachPistonHead(motionDir, returnMovingPart);
		}
		
		PistonBlockEntity parent = getParent();
		
		return new MovedBlock(parent.getCachedState(), parent);
	}
	
	@Override
	public boolean isSideSolid(BlockView world, BlockPos pos, Direction face, SideShapeType shapeType) {
		if (mergingState != null && !mergingState.isOf(Blocks.MOVING_PISTON)) {
			return mergingState.isSideSolid(world, pos, face, shapeType);
		}
		
		return false;
	}
	
	private MovedBlock undoMerge() {
		if (parentPistonBlockEntity == null) {
			return new MovedBlock(mergingState, mergingBlockEntity);
		}
		
		((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedState(mergingState);
		((RTIPistonBlockEntity)parentPistonBlockEntity).setMovedBlockEntity(mergingBlockEntity);
		
		PistonBlockEntity parent = getParent();
		
		return new MovedBlock(parent.getCachedState(), parent);
	}
	
	private void prepareBlockPlacement() {
		if (Tweaks.Observer.DELAY_RISING_EDGE.get() == 0 && pushedBlock.isOf(Blocks.OBSERVER) && !pushedBlock.get(Properties.POWERED)) {
			// This fixes observers not having the proper delay when their rising edge delay is set to 0
			world.setBlockState(pos, pushedBlock.cycle(Properties.FACING), 16);
		}
		if (mergingState != null) {
			if (SlabHelper.isSlab(pushedBlock)) {
				if (mergingState.isOf(pushedBlock.getBlock())) {
					completeMerge(pushedBlock.with(Properties.SLAB_TYPE, SlabType.DOUBLE));
				} else if (mergingState.isOf(Blocks.MOVING_PISTON) && mergingBlockEntity instanceof PistonBlockEntity) {
					continueMerge();
				} else {
					RedstoneTweaks.LOGGER.warn("Cannot merge " + pushedBlock + " with " + movedBlockEntity + " into " + mergingState + " with " + mergingBlockEntity);
				}
			} else if (PistonHelper.isPiston(pushedBlock) || PistonHelper.isPistonHead(pushedBlock)) {
				if (PistonHelper.isPiston(mergingState) || PistonHelper.isPistonHead(mergingState)) {
					boolean sticky = PistonHelper.isPistonHead(pushedBlock, true) || PistonHelper.isPistonHead(mergingState, true);
					
					completeMerge(PistonHelper.getPiston(sticky, pushedBlock.get(Properties.FACING), false));
				} else if (mergingState.isOf(Blocks.MOVING_PISTON) && mergingBlockEntity instanceof PistonBlockEntity) {
					continueMerge();
				}
			} else if (hasChildPistonBlockEntity) {
				RTIPistonBlockEntity pistonBlockEntity = (RTIPistonBlockEntity)movedBlockEntity;
				
				pistonBlockEntity.setMergingState(mergingState);
				pistonBlockEntity.setMergingBlockEntity(mergingBlockEntity);
				
				setMergingState(null);
				setMergingBlockEntity(null);
			} else {
				RedstoneTweaks.LOGGER.warn("Cannot merge " + pushedBlock + " with " + movedBlockEntity + " into " + mergingState + " with " + mergingBlockEntity);
			}
		}
		
		if (hasChildPistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setParentPistonBlockEntity(parentPistonBlockEntity);
		}
	}
	
	private void queueMovedBlockEntityPlacement() {
		if (movedBlockEntity != null) {
			((RTIWorld)world).queueBlockEntityPlacement(pos, movedBlockEntity);
		}
	}
	
	private void completeMerge(BlockState state) {
		setMovedState(state);
		
		setMergingState(null);
		setMergingBlockEntity(null);
	}
	
	private void continueMerge() {
		RTIPistonBlockEntity pistonBlockEntity = (RTIPistonBlockEntity)mergingBlockEntity;
		
		pistonBlockEntity.setIsMerging(false);
		pistonBlockEntity.setMergingState(pushedBlock);
		pistonBlockEntity.setMergingBlockEntity(movedBlockEntity);
		
		setMovedState(mergingState);
		setMovedBlockEntity(mergingBlockEntity);
		
		setMergingState(null);
		setMergingBlockEntity(null);
	}
	
	private BlockState getStateForMainShape() {
		if (source && !sourceIsMoving && !extending) {
			boolean sticky = PistonHelper.isSticky(pushedBlock);
			Direction facing = pushedBlock.get(Properties.FACING);
			boolean shortArm = (progress >= 0.5F);
			
			return PistonHelper.getPistonHead(sticky, facing, shortArm);
		}
		
		return pushedBlock;
	}
	
	private BlockState getStateForAdditionalShape() {
		if (source) {
			if (!extending) {
				if (sourceIsMoving) {
					boolean sticky = PistonHelper.isSticky(pushedBlock);
					Direction facing = pushedBlock.get(Properties.FACING);
					boolean shortArm = (progress >= 0.5F);
					
					return PistonHelper.getPistonHead(sticky, facing, shortArm);
				}
				
				return pushedBlock;
			}
		} else if (mergingState != null) {
			return mergingState;
		}
		
		return Blocks.AIR.getDefaultState();
	}
	
	private Vec3d getAmountExtended() {
		double s = getAmountExtended(progress);
		
		return new Vec3d(s * facing.getOffsetX(), s * facing.getOffsetY(), s * facing.getOffsetZ());
	}
	
	private Vec3d getTotalAmountExtended() {
		return getTotalAmountExtended(Vec3d.ZERO, isMerging);
	}
	
	private Vec3d getStepAmount() {
		double s = amountPerStep;
		
		return new Vec3d(s * facing.getOffsetX(), s * facing.getOffsetY(), s * facing.getOffsetZ());
	}
	
	private Vec3d getTotalStepAmount() {
		return getTotalStepAmount(Vec3d.ZERO, isMerging);
	}
	
	private boolean shouldLaunchEntities() {
		return PistonHelper.launchesEntities(getMovedMovingState().getBlock());
	}
	
	// The axial velocity of an entity launched by a slime block
	private double getLaunchVelocity(double velocity, double stepAmount) {
		return (stepAmount > 0.0D) ? 1.0D : -1.0D;
	}
	
	// the amount an entity will be moved by the moving block
	private double updateMoveAmount(double moveAmount, double stepAmount, Direction.Axis axis, Box entityBox, Box box) {
		boolean positive = stepAmount > 0.0D;
		
		if (positive ? (moveAmount >= stepAmount) : (moveAmount <= stepAmount)) {
			return moveAmount;
		}
		
		switch (axis) {
		case X:
			return positive ? Math.max(moveAmount, (box.maxX - entityBox.minX)) : Math.min(moveAmount, (box.minX - entityBox.maxX));
		case Y:
			return positive ? Math.max(moveAmount, (box.maxY - entityBox.minY)) : Math.min(moveAmount, (box.minY - entityBox.maxY));
		case Z:
			return positive ? Math.max(moveAmount, (box.maxZ - entityBox.minZ)) : Math.min(moveAmount, (box.minZ - entityBox.maxZ));
		default:
			return moveAmount;
		}
	}
	
	private double clampMoveAmount(double moveAmount, double stepAmount) {
		if (moveAmount == 0.0D) {
			return moveAmount;
		}
		
		double offset = 0.02D * stepAmount;
		
		return (stepAmount > 0.0D) ? (Math.min(moveAmount, stepAmount) + offset) : (Math.max(moveAmount, stepAmount) + offset);
	}
	
	// Check if the entity should be pulled along by moved honey
	private boolean shouldPullEntity(Box box, Entity entity) {
		return method_23671(box, entity);
	}
	
	private void moveEntities() {
		BlockState mainState = getStateForMainShape();
		BlockState additionalState = getStateForAdditionalShape();
		
		Vec3d totalAmountExtended = getTotalAmountExtended();
		Vec3d totalStepAmount = getTotalStepAmount();
		
		if (!mainState.isOf(Blocks.MOVING_PISTON)) {
			VoxelShape mainShape = mainState.getCollisionShape(world, pos);
			
			if (!mainShape.isEmpty()) {
				moveEntities(mainShape, totalAmountExtended, totalStepAmount);
			}
			
			if (mainState.isOf(Blocks.HONEY_BLOCK)) {
				pullEntities(mainShape, totalAmountExtended, totalStepAmount);
			}
		}
		if (!additionalState.isOf(Blocks.MOVING_PISTON)) {
			VoxelShape additionalShape = additionalState.getCollisionShape(world, pos);
			
			if (!additionalShape.isEmpty()) {
				Vec3d amountExtended = totalAmountExtended.subtract(getAmountExtended());
				Vec3d stepAmount = totalStepAmount.subtract(getStepAmount());
				
				moveEntities(additionalShape, amountExtended, stepAmount);
			}
		}
	}
	
	private void moveEntities(VoxelShape shape, Vec3d amountExtended, Vec3d stepAmount) {
		Vec3d offset = Vec3d.of(pos).add(amountExtended);
		Box boundingBox = shape.getBoundingBox().offset(offset);
		
		double stepX = stepAmount.x;
		double stepY = stepAmount.y;
		double stepZ = stepAmount.z;
		
		List<Entity> entities = world.getOtherEntities(null, boundingBox.stretch(stepX, stepY, stepZ));
		
		if (!entities.isEmpty()) {
			List<Box> boundingBoxes = shape.getBoundingBoxes();
			boolean launchEntities = shouldLaunchEntities();
			
			boolean moveX = (stepX != 0.0D);
			boolean moveY = (stepY != 0.0D);
			boolean moveZ = (stepZ != 0.0D);
			
			for (Entity entity : entities) {
				if (entity.getPistonBehavior() == PistonBehavior.IGNORE) {
					continue;
				}
				
				if (launchEntities && !(entity instanceof ServerPlayerEntity)) {
					Vec3d velocity = entity.getVelocity();
					
					double velocityX = moveX ? getLaunchVelocity(velocity.x, stepX) : velocity.x;
					double velocityY = moveY ? getLaunchVelocity(velocity.y, stepY) : velocity.y;
					double velocityZ = moveZ ? getLaunchVelocity(velocity.z, stepZ) : velocity.z;
					
					entity.setVelocity(velocityX, velocityY, velocityZ);
				}
				
				double moveAmountX = 0.0D;
				double moveAmountY = 0.0D;
				double moveAmountZ = 0.0D;
				
				for (Box box : boundingBoxes) {
					Box[] boxes = BoxUtils.getExpansionBoxes(box.offset(offset), stepX, stepY, stepZ);
					Box entityBox = entity.getBoundingBox();
					
					if (moveX && entityBox.intersects(boxes[0])) {
						moveAmountX = updateMoveAmount(moveAmountX, stepX, Direction.Axis.X, entityBox, boxes[0]);
					}
					if (moveY && entityBox.intersects(boxes[1])) {
						moveAmountY = updateMoveAmount(moveAmountY, stepY, Direction.Axis.Y, entityBox, boxes[1]);
					}
					if (moveZ && entityBox.intersects(boxes[2])) {
						moveAmountZ = updateMoveAmount(moveAmountZ, stepZ, Direction.Axis.Z, entityBox, boxes[2]);
					}
				}
				
				moveAmountX = clampMoveAmount(moveAmountX, stepX);
				moveAmountY = clampMoveAmount(moveAmountY, stepY);
				moveAmountZ = clampMoveAmount(moveAmountZ, stepZ);
				
				if (moveAmountX != 0.0D || moveAmountY != 0.0D || moveAmountZ != 0.0D) {
					((RTIEntity)entity).moveByPiston(new Vec3d(moveAmountX, moveAmountY, moveAmountZ), stepAmount);
				}
			}
		}
	}
	
	private void pullEntities(VoxelShape shape, Vec3d amountExtended, Vec3d stepAmount) {
		if (stepAmount.x != 0.0D || stepAmount.z != 0.0D) {
			Vec3d offset = Vec3d.of(pos).add(amountExtended);
			double y = shape.getMax(Direction.Axis.Y);
			
			Box boundingBox = new Box(0.0D, y, 0.0D, 1.0D, 1.5000000999999998D, 1.0D).offset(offset);
			
			List<Entity> entities = world.getOtherEntities(null, boundingBox, (entity) -> {
				return shouldPullEntity(boundingBox, entity);
			});
			
			for (Entity entity : entities) {
				((RTIEntity)entity).moveByPiston(stepAmount, stepAmount);
			}
		}
	}
	
	private VoxelShape getMainCollisionShape(BlockView world, BlockPos pos) {
		BlockState mainState = getStateForMainShape();
		
		Vec3d amountExtended = getAmountExtended();
		
		if (!mainState.isOf(Blocks.MOVING_PISTON)) {
			return mainState.getCollisionShape(world, pos).offset(amountExtended.x, amountExtended.y, amountExtended.z);
		} else if (hasChildPistonBlockEntity) {
			return ((PistonBlockEntity)movedBlockEntity).getCollisionShape(world, pos).offset(amountExtended.x, amountExtended.y, amountExtended.z);
		}
		
		return VoxelShapes.empty();
	}
	
	private VoxelShape getAdditionalCollisionShape(BlockView world, BlockPos pos) {
		BlockState additionalState = getStateForAdditionalShape();
		
		if (!additionalState.isOf(Blocks.MOVING_PISTON)) {
			return additionalState.getCollisionShape(world, pos);
		} else if (mergingBlockEntity != null && mergingBlockEntity instanceof PistonBlockEntity) {
			return ((PistonBlockEntity)movedBlockEntity).getCollisionShape(world, pos);
		}
		
		return VoxelShapes.empty();
	}
}
