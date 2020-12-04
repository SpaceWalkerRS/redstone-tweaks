package redstonetweaks.mixin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.ChainHelper;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.setting.Tweaks;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements RTIPistonHandler {
	
	@Shadow @Final private World world;
	@Shadow @Final private boolean retracted;
	@Shadow @Final private BlockPos posTo;
	@Shadow @Final private Direction motionDirection;
	@Shadow @Final private Direction pistonDirection;
	@Shadow @Final private List<BlockPos> movedBlocks;
	@Shadow @Final private List<BlockPos> brokenBlocks;

	private boolean sticky = false;
	private Set<BlockPos> anchoredChains;
	private List<BlockEntity> movedBlockEntities;
	// A map of positions where two slabs will merge, mapped to the type of slab that will move in
	private Map<BlockPos, SlabType> mergedSlabTypes;
	// A map of positions where a double slab is split, mapped to type of slab that will move out
	private Map<BlockPos, SlabType> splitSlabTypes;
	
	@Shadow protected abstract boolean tryMove(BlockPos pos, Direction dir);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(World world, BlockPos pos, Direction dir, boolean retracted, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		
		if (state.isOf(Blocks.STICKY_PISTON)) {
			sticky = true;
		} else
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				if (pistonBlockEntity.isSource()) {
					BlockState movedState = pistonBlockEntity.getPushedBlock();
					
					if (movedState.isOf(Blocks.STICKY_PISTON) || (movedState.isOf(Blocks.PISTON_HEAD) && movedState.get(Properties.PISTON_TYPE) == PistonType.STICKY)) {
						sticky = true;
					}
				}
			}
		}
		
		if (Tweaks.Global.CHAINSTONE.get()) {
			anchoredChains = new HashSet<>();
		}
		if (Tweaks.Global.MERGE_SLABS.get()) {
			mergedSlabTypes = new HashMap<>();
			splitSlabTypes = new HashMap<>();
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() || Tweaks.Global.MOVABLE_MOVING_BLOCKS.get()) {
			movedBlockEntities = new ArrayList<>();
		}
	}
	
	@Override
	public void addMovedBlockEntity(BlockEntity blockEntity) {
		movedBlockEntities.add(blockEntity);
	}
	
	@Override
	public List<BlockEntity> getMovedBlockEntities() {
		return movedBlockEntities;
	}
	
	@Inject(method = "isBlockSticky", cancellable = true, at = @At(value = "HEAD"))
	private static void onIsBlockStickyInjectAtHead(Block block, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(isPotentiallySticky(block));
		cir.cancel();
	}
	
	@Redirect(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"))
	private boolean onCalculatePushRedirectIsMovable(BlockState state, World world, BlockPos blockPos, Direction direction, boolean canBreak, Direction pistonDir) {
		return PistonBlock.isMovable(state, world, blockPos, direction, canBreak, pistonDir) && (retracted || !SlabHelper.isSlab(state) || PistonHelper.canSlabStickTo(state, direction));
	}
	
	@Redirect(method = "calculatePush", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onCalculatePushRedirectGetBlockState1(World world, BlockPos pos) {
		return PistonHelper.getStateToMove(world, pos);
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onTryMoveRedirectGetBlockState(World world, BlockPos pos) {
		return PistonHelper.getStateToMove(world, pos);
	}
	
	private boolean alreadyMoved;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Ljava/util/List;contains(Ljava/lang/Object;)Z"))
	private void onTryMoveInjectBeforeListContains(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState movedState, Block movedBlock) {
		alreadyMoved = movedBlocks.contains(pos);
		
		if (Tweaks.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedBlock)) {
			// Usually the direction given is the opposite of the side from which the block is pulled along,
			// but when tryMove is called inside calculatePush for a retraction event, it is not
			Direction pulledFrom = (pos == posTo && !retracted) ? dir : dir.getOpposite();
			
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
		return true;
	}
	
	private boolean isAdjacentBlockStuck;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onTryMoveInjectBeforeIsAdjacentBlockStuck(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState behindState, Block block, int i, BlockPos behindPos) {
		BlockPos blockPos = behindPos.offset(motionDirection);
		BlockState blockState = PistonHelper.getStateToMove(world, blockPos);
		isAdjacentBlockStuck = isAdjacentBlockStuck(blockPos, blockState, behindPos, behindState, motionDirection.getOpposite());
		
		if (Tweaks.Global.MERGE_SLABS.get() && isAdjacentBlockStuck && SlabHelper.isSlab(behindState)) {
			if (motionDirection.getAxis().isVertical() && !movedBlocks.contains(behindPos)) {
				trySplitDoubleSlab(behindPos, behindState, SlabHelper.getTypeFromDirection(motionDirection));
			}
			
			// If the slab is pulled in the direction of the movement, it cannot be merged into!
			mergedSlabTypes.remove(behindPos);
		}
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsAdjacentBlockStuck(Block block1, Block block2) {
		return isAdjacentBlockStuck;
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"))
	private int onTryMoveRedirectIndexOf(List<Object> list, Object pos) {
		if (Tweaks.Global.MERGE_SLABS.get() && splitSlabTypes.remove(pos) != null) {
			mergedSlabTypes.remove(pos);
			list.remove(pos);
			
			return -1;
		}
		return list.indexOf(pos);
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private void onTryMoveInjectBeforeGetPistonBehavior(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState frontState, Block block, int i, int j, int distance, BlockPos frontPos) {
		if (Tweaks.Global.MERGE_SLABS.get()) {
			// We break out of the loop to prevent the front block from being added to the movedBlocks list multiple times
			boolean breakLoop = alreadyMoved;
			
			BlockPos blockPos = pos.offset(motionDirection, distance - 1);
			BlockState pushingState = world.getBlockState(blockPos);
			
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
			
			if (isPotentiallySticky(frontState)) {
				// If a splitting double slab pushes a sticky block, the double slab should move as a whole after all!
				if (isAdjacentBlockStuck(frontPos, frontState, blockPos, pushingState, motionDirection.getOpposite())) {
					splitSlabTypes.remove(blockPos);
				}
			}
			
			if (breakLoop) {
				cir.setReturnValue(true);
				cir.cancel();
			}
		}
	}
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return sticky ? Tweaks.StickyPiston.PUSH_LIMIT.get() : Tweaks.NormalPiston.PUSH_LIMIT.get();
	}
	
	@Redirect(method = "canMoveAdjacentBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onCanMoveAdjacentBlockRedirectGetBlockState(World world, BlockPos pos) {
		return PistonHelper.getStateToMove(world, pos);
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
	
	private static boolean isPotentiallySticky(BlockState state) {
		return isPotentiallySticky(state.getBlock());
	}
	
	private static boolean isPotentiallySticky(Block block) {
		if (block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK) {
			return true;
		}
		if (Tweaks.StickyPiston.SUPER_STICKY.get() && (block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		if (Tweaks.Global.CHAINSTONE.get() && block == Blocks.CHAIN) {
			return true;
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() && (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)) {
			return true;
		}
		if (Tweaks.NormalPiston.MOVABLE_WHEN_EXTENDED.get() && (block == Blocks.PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		if (Tweaks.StickyPiston.MOVABLE_WHEN_EXTENDED.get() && (block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD)) {
			return true;
		}
		return false;
	}
	
	// dir is the direction from pos towards adjacentPos
	private boolean isAdjacentBlockStuck(BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		if (SlabHelper.isSlab(adjacentState) && !PistonHelper.canSlabStickTo(adjacentState, dir.getOpposite()))
			return false;
		
		// Default vanilla implementation: slime and honey do not stick to each other
		if (state.isOf(Blocks.SLIME_BLOCK) && !adjacentState.isOf(Blocks.HONEY_BLOCK)) {
			return true;
		}
		if (state.isOf(Blocks.HONEY_BLOCK) && !adjacentState.isOf(Blocks.SLIME_BLOCK)) {
			return true;
		}
		
		if (Tweaks.StickyPiston.SUPER_STICKY.get()) {
			if (state.isOf(Blocks.STICKY_PISTON)) {
				return dir == state.get(Properties.FACING);
			}
			if (state.isOf(Blocks.PISTON_HEAD)) {
				Direction facing = state.get(Properties.FACING);
				
				if (dir == facing) {
					return state.get(Properties.PISTON_TYPE) == PistonType.STICKY;
				}
				if (dir == facing.getOpposite()) {
					return adjacentState.getBlock() instanceof PistonBlock;
				}
			}
		}
		if (Tweaks.Global.CHAINSTONE.get() && state.isOf(Blocks.CHAIN)) {
			Direction.Axis axis = state.get(Properties.AXIS);
			
			if (axis == dir.getAxis()) {
				// Identify situations where the adjacent block is definitely not pulled along
				if (adjacentState.isOf(Blocks.CHAIN)) {
					if (adjacentState.get(Properties.AXIS) != axis) {
						return false;
					}
				} else
				if (!Block.sideCoversSmallSquare(world, adjacentPos, dir.getOpposite())) {
					return false;
				}
				
				// If the chain block moves along its own axis it is always sticky,
				// otherwise it needs to be part of a chain that is anchored on both ends
				if (axis == motionDirection.getAxis()) {
					return true;
				}
				return ChainHelper.isFullyAnchored(world, pos, axis, anchoredChains);
			}
		}
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get() && (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST))) {
			ChestType chestType = state.get(Properties.CHEST_TYPE);
			
			if (chestType == ChestType.SINGLE) {
				return false;
			}
			
			if (adjacentState.isOf(state.getBlock()) && adjacentState.get(Properties.CHEST_TYPE) == chestType.getOpposite()) {
				Direction facing = state.get(Properties.HORIZONTAL_FACING);

				if (chestType == ChestType.LEFT) {
					return (dir == facing.rotateYClockwise());
				}
				return (dir == facing.rotateYCounterclockwise());
			}
		}
		if (PistonHelper.movableWhenExtended(false)) {
			if (state.isOf(Blocks.PISTON) && PistonHelper.isExtended(world, pos, state)) {
				Direction facing = state.get(Properties.FACING);
				
				if (facing == dir) {
					return adjacentState.isOf(Blocks.PISTON_HEAD) && adjacentState.get(Properties.FACING) == facing;
				}
			} else
			if (state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.DEFAULT) {
				Direction facing = state.get(Properties.FACING);
				
				if (facing == dir.getOpposite()) {
					return adjacentState.isOf(Blocks.PISTON);
				}
			}
		}
		if (PistonHelper.movableWhenExtended(true)) {
			if (state.isOf(Blocks.STICKY_PISTON) && PistonHelper.isExtended(world, pos, state)) {
				Direction facing = state.get(Properties.FACING);
				
				if (facing == dir) {
					return adjacentState.isOf(Blocks.PISTON_HEAD) && adjacentState.get(Properties.FACING) == facing;
				}
			} else
			if (state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.STICKY) {
				Direction facing = state.get(Properties.FACING);
				
				if (facing == dir) {
					return Tweaks.StickyPiston.SUPER_STICKY.get();
				}
				if (facing == dir.getOpposite()) {
					return adjacentState.isOf(Blocks.STICKY_PISTON);
				}
			}
		}
	
		return false;
	}
	
	@Override
	public Map<BlockPos, SlabType> getMergedSlabTypes() {
		return mergedSlabTypes;
	}
	
	@Override
	public Map<BlockPos, SlabType> getSplitSlabTypes() {
		return splitSlabTypes;
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
			SlabType fromType = movedState.get(Properties.SLAB_TYPE);
			
			// No merging can occur if the moved state is a double slab block
			if (fromType == SlabType.DOUBLE) {
				return false;
			}
			
			SlabType toType = toState.get(Properties.SLAB_TYPE);
			
			// If the two slabs are both tops or both bottoms, no merging can occur
			if (toType == fromType) {
				return false;
			}
			// If the movement is horizontal, merging will occur (given the slabs are different types)
			// If the movement is vertical, merging will only occur if the two slabs are not already touching
			if (motionDirection.getAxis().isHorizontal() || toType == SlabHelper.getTypeFromDirection(motionDirection)) {
				mergedSlabTypes.put(toPos, fromType);
				return true;
			}
		}
		return false;
	}
}
