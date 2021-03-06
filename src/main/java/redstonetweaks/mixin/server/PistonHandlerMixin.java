package redstonetweaks.mixin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.mixin.RTIPistonBlockEntity;
import redstonetweaks.interfaces.mixin.RTIPistonHandler;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements RTIPistonHandler {
	
	@Shadow @Final private World world;
	@Shadow @Final private boolean retracted;
	@Shadow @Final private BlockPos posFrom;
	@Shadow @Final private BlockPos posTo;
	@Shadow @Final private Direction motionDirection;
	@Shadow @Final private Direction pistonDirection;
	@Shadow @Final private List<BlockPos> movedBlocks;
	@Shadow @Final private List<BlockPos> brokenBlocks;

	private boolean sticky = false;
	private BlockPos headPos;
	// A map of positions where 'looseHead' behavior occurs, mapped to a boolean
	// which is true if the piston head detaches, false if it re-attaches
	private Map<BlockPos, Boolean> loosePistonHeads;
	private List<BlockEntity> movedBlockEntities;
	// A map of positions where two slabs will merge, mapped to the type of slab that will move in
	private Map<BlockPos, SlabType> mergedSlabTypes;
	// A map of positions where a double slab is split, mapped to the type of slab that will move out
	private Map<BlockPos, SlabType> splitSlabTypes;
	
	@Shadow protected abstract boolean tryMove(BlockPos pos, Direction dir);
	@Shadow protected abstract void setMovedBlocks(int from, int to);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(World world, BlockPos pos, Direction pistonDir, boolean extending, CallbackInfo ci) {
		headPos = posFrom.offset(pistonDir);
		
		mergedSlabTypes = new HashMap<>();
		splitSlabTypes = new HashMap<>();
		loosePistonHeads = new HashMap<>();
		movedBlockEntities = new ArrayList<>();
	}
	
	@Override
	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}
	
	@Override
	public List<BlockEntity> getMovedBlockEntities() {
		return movedBlockEntities;
	}
	
	@Inject(method = "isBlockSticky", cancellable = true, at = @At(value = "HEAD"))
	private static void onIsBlockStickyInjectAtHead(Block block, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(PistonHelper.isPotentiallySticky(block));
		cir.cancel();
	}
	
	@Redirect(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"))
	private boolean onCalculatePushRedirectIsMovable(BlockState state, World world, BlockPos blockPos, Direction direction, boolean canBreak, Direction pistonDir) {
		// If a slab is not touching the side it is pulled from, it is not pulled along
		return PistonBlock.isMovable(state, world, blockPos, direction, canBreak, pistonDir) && (retracted || !SlabHelper.isSlab(state) || PistonHelper.canSlabStickTo(state, direction));
	}
	
	@Redirect(method = "calculatePush", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onCalculatePushRedirectGetBlockState1(World world, BlockPos pos) {
		return PistonHelper.getStateForMovement(world, pos);
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onTryMoveRedirectGetBlockState(World world, BlockPos pos) {
		return PistonHelper.getStateForMovement(world, pos);
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/math/BlockPos;equals(Ljava/lang/Object;)Z"))
	private boolean onTryMoveRedirectBlockPosEquals0(BlockPos pos, Object obj) {
		return isPistonPos(pos);
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/math/BlockPos;equals(Ljava/lang/Object;)Z"))
	private boolean onTryMoveRedirectBlockPosEquals1(BlockPos pos, Object obj) {
		return isPistonPos(pos);
	}
	
	private boolean alreadyMoved;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private void onTryMoveInjectBeforeListContains(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState movedState, Block movedBlock) {
		alreadyMoved = movedBlocks.contains(pos);
		
		// Usually the direction given is the opposite of the side from which the block is pulled along,
		// but when tryMove is called inside calculatePush for a retraction event, it is not
		boolean initialMove = pos.equals(posTo);
		Direction pulledFrom = (initialMove && !retracted) ? dir : dir.getOpposite();
		
		if (alreadyMoved) {
			// If the piston is pulled along from a side instead of its front or back, the head will not detach
			if (loosePistonHeads.get(pos) == Boolean.TRUE && pulledFrom.getAxis() != motionDirection.getAxis()) {
				loosePistonHeads.remove(pos);
			}
		} else if (pulledFrom == motionDirection && (!retracted || !initialMove)) {
			// Don't try to detach if the block is pushed by the piston directly
			tryDetachPistonHead(pos, movedState);
		}
		
		if (Tweaks.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedBlock)) {
			if (alreadyMoved) {
				SlabType movedType = splitSlabTypes.get(pos);
				
				// Check if the block is a splitting double slab and if the stationary half is now pulled along as well
				if (movedType != null && pulledFrom != SlabHelper.getDirectionFromType(movedType)) {
					// We need to re-evaluate the movement of this block
					movedBlocks.remove(pos);
					splitSlabTypes.remove(pos);
					mergedSlabTypes.remove(pos);
					
					alreadyMoved = false;
				}
			} else if (pulledFrom.getAxis().isVertical() && (pos != posTo || !retracted)) {
				trySplitDoubleSlab(pos, movedState, SlabHelper.getTypeFromDirection(pulledFrom));
			}
			
			// This slab cannot be merged into if not at least one half stays behind
			if (pulledFrom.getAxis().isHorizontal() || !splitSlabTypes.containsKey(pos)) {
				mergedSlabTypes.remove(pos);
			}
		}
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private boolean onTryMoveRedirectListContains(List<Object> list, Object object) {
		return alreadyMoved;
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsBlockSticky0(Block block) {
		// Replaced by the inject below
		return true;
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/BlockState;isAir()Z"))
	private boolean onTryMoveRedirectIsAir1(BlockState state) {
		// Replaced by the inject below
		return false;
	}
	
	private boolean isAdjacentBlockStuck;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onTryMoveInjectBeforeIsAdjacentBlockStuck(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState behindState, Block block, int distance, BlockPos behindPos) {
		BlockPos blockPos = behindPos.offset(motionDirection);
		BlockState blockState = PistonHelper.getStateForMovement(world, blockPos);
		
		if (loosePistonHeads.containsKey(blockPos)) {
			isAdjacentBlockStuck = false;
		} else {
			isAdjacentBlockStuck = isAdjacentBlockStuck(blockPos, blockState, behindPos, behindState, motionDirection.getOpposite());
			
			if (isAdjacentBlockStuck) {
				if (!movedBlocks.contains(behindPos)) {
					tryDetachPistonHead(behindPos, behindState);
				}
				
				if (Tweaks.Global.MERGE_SLABS.get() && SlabHelper.isSlab(behindState)) {
					if (motionDirection.getAxis().isVertical() && !movedBlocks.contains(behindPos)) {
						trySplitDoubleSlab(behindPos, behindState, SlabHelper.getTypeFromDirection(motionDirection));
					}
					
					// If the slab is pulled in the direction of the movement, it cannot be merged into!
					mergedSlabTypes.remove(behindPos);
				}
			}
		}
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsAdjacentBlockStuck(Block block1, Block block2) {
		return isAdjacentBlockStuck;
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"))
	private int onTryMoveRedirectIndexOf(List<Object> list, Object pos) {
		if (Tweaks.Global.MERGE_SLABS.get() && splitSlabTypes.remove(pos) != null) {
			// If a half slab is pushed that was previously the remaining part of a splitting double slab
			// then that double slab will no longer split and/or merge. 
			// We then also remove it from the movedBlocks list so it can push blocks in front of it
			
			mergedSlabTypes.remove(pos);
			list.remove(pos);
			
			return -1;
		}
		
		return list.indexOf(pos);
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 2, shift = Shift.BEFORE, target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"))
	private void onTryMoveInjectBeforeIsMovable(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState frontState, Block block, int i, int j, int distance, BlockPos frontPos) {
		if (!retracted && frontPos.equals(headPos)) {
			cir.setReturnValue(true);
			cir.cancel();
			
			return;
		}
		
		// We break out of the loop to prevent the front block from being added to the movedBlocks list multiple times
		// or when it should not be added at all
		boolean breakLoop = false;
		
		BlockPos blockPos = pos.offset(motionDirection, distance - 1);
		BlockState pushingState = PistonHelper.getStateForMovement(world, blockPos);
		
		if (tryAttachPistonHead(blockPos, pushingState, frontPos, frontState)) {
			breakLoop = true;
		} else if (Tweaks.Global.MERGE_SLABS.get()) {
			// If the pushing state is a split double slab only one half of it will be moving
			SlabType pushingType = splitSlabTypes.get(blockPos);
			if (pushingType != null) {
				pushingState = pushingState.with(Properties.SLAB_TYPE, pushingType);
			}
			
			if (tryMergeSlabs(pushingState, frontPos, frontState)) {
				// tryMergeSlabs will also return true if the pushing state merges into a double slab,
				// in which case one half will be pushed over. In this case the front position should
				// be added to the movedBlocksList, otherwise we break out of the loop
				if (!trySplitDoubleSlab(frontPos, frontState, mergedSlabTypes.get(frontPos))) {
					// If the position that is merged into does not split we do not need to add it
					// to the movedBlocks list
					breakLoop = true;
				}
			}
			
			if (PistonHelper.isPotentiallySticky(frontState)) {
				// If a splitting double slab pushes a sticky block, the double slab should move as a whole after all!
				if (isAdjacentBlockStuck(frontPos, frontState, blockPos, pushingState, motionDirection.getOpposite())) {
					splitSlabTypes.remove(blockPos);
					mergedSlabTypes.remove(blockPos);
				}
			}
		}
		
		if (breakLoop) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Redirect(
			method = "tryMove",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"
			)
	)
	private PistonBehavior onTryMoveRedirectGetPistonBehavior(BlockState state) {
		return PistonHelper.getPistonBehavior(state);
	}
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return PistonSettings.moveLimit(sticky, retracted);
	}
	
	@Redirect(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onCanMoveAdjacentBlockRedirectGetBlockState(World world, BlockPos pos) {
		return PistonHelper.getStateForMovement(world, pos);
	}

	@Inject(method = "canMoveAdjacentBlock", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onCanMoveAdjacentBlockInjectBeforeIsAdjacentBlockStuck(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState pullingState, Direction[] directions, int len, int index, Direction dir, BlockPos adjacentPos, BlockState adjacentState) {
		if (isAdjacentBlockStuck(pos, pullingState, adjacentPos, adjacentState, dir) && !tryMove(adjacentPos, dir)) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}

	@Redirect(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean onCanMoveAdjacentBlockRedirectIsAdjacentBlockStuck(Block block1, Block block2) {
		// This is replaced by the injection above.
		return false;
	}
	
	private boolean isPistonPos(BlockPos pos) {
		return pos.equals(posFrom) || (!retracted && pos.equals(headPos));
	}
	
	// dir is the direction from pos towards adjacentPos
	private boolean isAdjacentBlockStuck(BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		return PistonHelper.isAdjacentBlockStuck(world, pos, state, adjacentPos, adjacentState, dir, retracted, motionDirection, posFrom, headPos);
	}
	
	@Override
	public Map<BlockPos, Boolean> getLoosePistonHeads() {
		return loosePistonHeads;
	}
	
	@Override
	public Map<BlockPos, SlabType> getMergedSlabTypes() {
		return mergedSlabTypes;
	}
	
	@Override
	public Map<BlockPos, SlabType> getSplitSlabTypes() {
		return splitSlabTypes;
	}
	
	private void tryDetachPistonHead(BlockPos pos, BlockState state) {
		if (PistonHelper.isPiston(state) && !state.get(Properties.EXTENDED) && PistonSettings.looseHead(PistonHelper.isSticky(state))) {
			Direction facing = state.get(Properties.FACING);
			
			if (facing.getAxis() == motionDirection.getAxis()) {
				loosePistonHeads.put(pos, true);
			}
		}
	}
	
	private boolean tryAttachPistonHead(BlockPos pos, BlockState state, BlockPos frontPos, BlockState frontState) {
		if (loosePistonHeads.containsKey(pos)) {
			return false;
		}
		if (loosePistonHeads.containsKey(frontPos)) {
			if (loosePistonHeads.get(frontPos)) {
				// If the piston is now pushed the head will not detach
				loosePistonHeads.remove(frontPos);
			}
			return false;
		}
		if (!PistonHelper.isPiston(state, motionDirection) && !PistonHelper.isPiston(frontState, motionDirection.getOpposite())) {
			return false;
		}
		
		for (boolean sticky : new boolean[] {false, true}) {
			if (PistonSettings.looseHead(sticky)) {
				if (PistonHelper.isPistonHead(state, sticky, motionDirection.getOpposite()) || PistonHelper.isPistonHead(frontState, sticky, motionDirection)) {
					loosePistonHeads.put(frontPos, false);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Check if a slab block is a double slab block that should split
	// if a pulling force is applied to the given half
	private boolean trySplitDoubleSlab(BlockPos pos, BlockState movedState, SlabType half) {
		if (movedState.get(Properties.SLAB_TYPE) == SlabType.DOUBLE) {
			SlabType oldHalf = splitSlabTypes.get(pos);
			
			if (oldHalf == null) {
				splitSlabTypes.put(pos, half);
				
				return true;
			} else if (oldHalf != half) {
				// Both halves are moved
				splitSlabTypes.remove(pos);
				mergedSlabTypes.remove(pos);
			}
		}
		
		return false;
	}
	
	// Check if the two given block states are slabs that should merge when pushed into each other
	private boolean tryMergeSlabs(BlockState movedState, BlockPos toPos, BlockState toState) {
		// Check if the two block states are slabs of the same material
		if (SlabHelper.isSlab(movedState) && toState.isOf(movedState.getBlock())) {
			SlabType movedType = movedState.get(Properties.SLAB_TYPE);
			
			// No merging can occur if the moved state is a double slab block
			if (movedType == SlabType.DOUBLE) {
				return false;
			}
			
			SlabType toType = toState.get(Properties.SLAB_TYPE);
			
			// If the two slabs are both tops or both bottoms, no merging can occur
			if (toType == movedType) {
				return false;
			}
			// If the movement is horizontal, merging will occur (given the slabs are different types)
			// If the movement is vertical, merging will only occur if the two slabs are not already touching
			if (motionDirection.getAxis().isHorizontal() || toType == SlabHelper.getTypeFromDirection(motionDirection)) {
				mergedSlabTypes.put(toPos, movedType);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<BlockPos> getMovingStructure() {
		movedBlocks.clear();
		
		BlockPos initPos = posFrom.offset(pistonDirection, retracted ? 2 : 1);
		
		tryBuildStructure(initPos, motionDirection);
		for (int i = 0; i < movedBlocks.size(); i++) {
			BlockPos movingPos = movedBlocks.get(i);
			BlockState movingState = getMovingState(movingPos);
			
			if (PistonHelper.isPotentiallySticky(movingState.getBlock())) {
				tryExpandStructure(movingPos, movingState);
			}
		}
		
		return movedBlocks;
	}
	
	private void tryBuildStructure(BlockPos pos, Direction dir) {
		if (pos.equals(posFrom) || (retracted && pos.equals(headPos)) || movedBlocks.contains(pos)) {
			return;
		}
		
		BlockState movingState = getMovingState(pos);
		
		if (movingState == null || !PistonBlock.isMovable(movingState, world, pos, motionDirection, false, dir)) {
			return;
		}
		
		int behindDistance = 1;
		BlockPos pullingPos = pos;
		
		while (PistonHelper.isPotentiallySticky(movingState.getBlock())) {
			BlockPos behindPos = pos.offset(motionDirection.getOpposite(), behindDistance);
			
			BlockState pullingState = movingState;
			movingState = getMovingState(behindPos);
			
			if (movingState == null || !PistonBlock.isMovable(movingState, world, behindPos, motionDirection, false, pistonDirection) || !isAdjacentBlockStuck(pullingPos, pullingState, behindPos, movingState, motionDirection.getOpposite())) {
				break;
			}
			
			behindDistance++;
			pullingPos = behindPos;
		}
		
		for (int i = behindDistance - 1; i >= 0; i--) {
			movedBlocks.add(pos.offset(motionDirection.getOpposite(), i));
		}
		
		int frontDistance = 1;
		
		while (true) {
			BlockPos frontPos = pos.offset(motionDirection, frontDistance);
			
			int index = movedBlocks.indexOf(frontPos);
			
			if (index > -1) {
				setMovedBlocks(behindDistance, index);
				
				for (int i = 0; i <= index + behindDistance; i++) {
					BlockPos movingPos = movedBlocks.get(i);
					movingState = getMovingState(movingPos);
					
					if (PistonHelper.isPotentiallySticky(movingState.getBlock())) {
						tryExpandStructure(movingPos, movingState);
					}
				}
				
				return;
			}
			
			movingState = getMovingState(frontPos);
			
			if (movingState == null || !PistonBlock.isMovable(movingState, world, frontPos, motionDirection, true, motionDirection)) {
				return;
			}
			
			movedBlocks.add(frontPos);
			
			behindDistance++;
			frontDistance++;
		}
	}
	
	private void tryExpandStructure(BlockPos pos, BlockState state) {
		for (Direction dir : Direction.values()) {
			if (dir.getAxis() == motionDirection.getAxis()) {
				continue;
			}
			
			BlockPos adjacentPos = pos.offset(dir);
			BlockState adjacentState = getMovingState(adjacentPos);
			
			if (adjacentState != null && isAdjacentBlockStuck(pos, state, adjacentPos, adjacentState, dir)) {
				tryBuildStructure(adjacentPos, dir);
			}
		}
	}
	
	private BlockState getMovingState(BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		
		if (!state.isOf(Blocks.MOVING_PISTON)) {
			return null;
		}
		
		BlockEntity blockEntity = world.getBlockEntity(pos);
		
		if (!(blockEntity instanceof PistonBlockEntity)) {
			return null;
		}
		
		PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
		
		if (pistonBlockEntity.isExtending() != retracted || pistonBlockEntity.getFacing() != pistonDirection) {
			return null;
		}
		
		return ((RTIPistonBlockEntity)pistonBlockEntity).getMovedMovingState();
	}
}
