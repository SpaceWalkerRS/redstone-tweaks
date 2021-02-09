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
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIWorld;

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
	
	public PistonBlockEntityMixin(BlockEntityType<?> type) {
		super(type);
	}
	
	@Shadow public abstract void finish();
	
	@Inject(method = "getProgress", cancellable = true, at = @At(value = "HEAD"))
	private void onGetProgressInjectAtReturn(float tickDelta, CallbackInfoReturnable<Float> cir) {
		if (!((RTIWorld)world).normalWorldTicks()) {
			int speed = PistonSettings.speed(sticky, extending);
			
			cir.setReturnValue(MathHelper.clamp(lastProgress + 0.2F / speed, 0, speed));
			cir.cancel();
		}
	}
	
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
		int speed = PistonSettings.speed(sticky, extending);
		
		return speed > 0 ? 1.0F / speed : 1.0F;
	}
	
	@Inject(method = "fromTag", at = @At(value = "RETURN"))
	private void onFromTagInjectAtReturn(BlockState state, CompoundTag tag, CallbackInfo ci) {
		sticky = tag.contains("sticky") ? tag.getBoolean("sticky") : false;
		sourceIsMoving = tag.contains("sourceIsMoving") ? tag.getBoolean("sourceIsMoving") : false;

		if (tag.contains("movedBlockEntity")) {
			Block movedBlock = pushedBlock.getBlock();
			
			if (movedBlock.hasBlockEntity()) {
				movedBlockEntity = movedBlock == Blocks.MOVING_PISTON ? new PistonBlockEntity() : ((BlockEntityProvider)movedBlock).createBlockEntity(world);
				
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
				mergingBlockEntity = mergingBlock == Blocks.MOVING_PISTON ? new PistonBlockEntity() : ((BlockEntityProvider)mergingBlock).createBlockEntity(world);
				
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
		if (getMovedMovingState().isOf(Blocks.MOVING_PISTON)) {
			cir.setReturnValue(VoxelShapes.empty());
			cir.cancel();
		}
	}
	
	@Redirect(method = "getCollisionShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/entity/PistonBlockEntity;pushedBlock:Lnet/minecraft/block/BlockState;"))
	private BlockState onGetCollisionShapeRedirectPushedBlock(PistonBlockEntity pistonBlockEntity) {
		return getMovedMovingState();
	}
	
	@Override
	public void init() {
		if (PistonSettings.speed(sticky, extending) == 0) {
			// This ensures the block entity finishes the first time it is ticked
			// Otherwise the behavior would be the same as if the speed was set to 1
			this.progress = 1.0F;
		}
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
		movedBlockEntity = blockEntity;
		
		if (movedBlockEntity != null) {
			movedBlockEntity.setLocation(world, pos);
			
			if (movedBlockEntity instanceof PistonBlockEntity) {
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
		if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
			return ((RTIPistonBlockEntity)movedBlockEntity).getMovedMovingState();
		}
		
		return pushedBlock;
	}
	
	@Override
	public void setMovedMovingState(BlockState state) {
		if (movedBlockEntity instanceof PistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setMovedMovingState(state);
		} else {
			pushedBlock = state;
		}
	}
	
	@Override
	public BlockEntity getMovedMovingBlockEntity() {
		if (movedBlockEntity instanceof PistonBlockEntity) {
			BlockEntity movingBlockEntity = ((RTIPistonBlockEntity)movedBlockEntity).getMovedMovingBlockEntity();
			
			if (movingBlockEntity != null) {
				return movingBlockEntity;
			}
		}
		
		return movedBlockEntity;
	}
	
	@Override
	public void setMovedMovingBlockEntity(BlockEntity blockEntity) {
		if (movedBlockEntity instanceof PistonBlockEntity) {
			((RTIPistonBlockEntity)movedBlockEntity).setMovedMovingBlockEntity(blockEntity);
		} else if (blockEntity.getType().supports(pushedBlock.getBlock())) {
			movedBlockEntity = blockEntity;
		}
	}
	
	@Override
	public BlockState getStateToMove() {
		if (source && PistonHelper.isPiston(pushedBlock)) {
			// Both extending and retracting piston bases should be treated as extended
			return pushedBlock.with(Properties.EXTENDED, true);
		}
		
		if (mergingState == null) {
			if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
				return ((RTIPistonBlockEntity)movedBlockEntity).getStateToMove();
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
		} else if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
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
			boolean headMoving = facing == motionDir;
			
			if (returnMovingPart == headMoving) {
				setMovedState(PistonHelper.getPistonHead(PistonHelper.isSticky(pushedBlock), facing).with(Properties.SHORT, headMoving));
			} else {
				setMovedState(pushedBlock.with(Properties.EXTENDED, true));
			}
		} else if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
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
		if (!world.isClient() && pushedBlock.isOf(Blocks.OBSERVER) && !pushedBlock.get(Properties.POWERED)) {
			// This fixes observers with 0 rising edge delay having the falling edge delay when placed after being moved
			world.setBlockState(pos, pushedBlock, 16);
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
			} else if (pushedBlock.isOf(Blocks.MOVING_PISTON) && movedBlockEntity instanceof PistonBlockEntity) {
				RTIPistonBlockEntity pistonBlockEntity = (RTIPistonBlockEntity)movedBlockEntity;
				
				pistonBlockEntity.setMergingState(mergingState);
				pistonBlockEntity.setMergingBlockEntity(mergingBlockEntity);
				
				setMergingState(null);
				setMergingBlockEntity(null);
			} else {
				RedstoneTweaks.LOGGER.warn("Cannot merge " + pushedBlock + " with " + movedBlockEntity + " into " + mergingState + " with " + mergingBlockEntity);
			}
		}
		
		if (movedBlockEntity != null) {
			if (movedBlockEntity instanceof PistonBlockEntity) {
				((RTIPistonBlockEntity)movedBlockEntity).setParentPistonBlockEntity(parentPistonBlockEntity);
			}
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
}
