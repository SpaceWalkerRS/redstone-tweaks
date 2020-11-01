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
import net.minecraft.block.enums.ChestType;
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
import redstonetweaks.setting.Settings;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements RTIPistonHandler {
	
	@Shadow @Final private World world;
	@Shadow @Final private boolean retracted;
	@Shadow @Final private BlockPos posTo;
	@Shadow @Final private Direction motionDirection;
	@Shadow @Final private Direction pistonDirection;
	@Shadow @Final private List<BlockPos> movedBlocks;
	@Shadow @Final private List<BlockPos> brokenBlocks;

	private boolean sticky;
	private List<BlockPos> anchoredChains;
	private List<BlockEntity> movedBlockEntities;
	// A map of positions where two slabs will merge, mapped to the type of slab that will move in
	private Map<BlockPos, SlabType> mergingSlabTypes;
	// A map of positions where a double slab is split, mapped to type of slab that will move out
	private Map<BlockPos, SlabType> splittingSlabTypes;
	
	@Shadow protected abstract boolean tryMove(BlockPos pos, Direction dir);
	
	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void onInitInjectAtReturn(World world, BlockPos pos, Direction dir, boolean retracted, CallbackInfo ci) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof PistonBlock) {
			sticky = state.isOf(Blocks.STICKY_PISTON);
		} else
		if (state.isOf(Blocks.MOVING_PISTON)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				if (pistonBlockEntity.isSource()) {
					BlockState movedState = pistonBlockEntity.getPushedBlock();
					if (movedState.getBlock() instanceof PistonBlock) {
						sticky = movedState.isOf(Blocks.STICKY_PISTON);
					}
				}
			}
		}
		
		if (Settings.Global.CHAINSTONE.get()) {
			anchoredChains = new ArrayList<>();
		}
		if (Settings.Global.MOVABLE_BLOCK_ENTITIES.get()) {
			movedBlockEntities = new ArrayList<>();
		}
		if (Settings.Global.MERGE_SLABS.get()) {
			mergingSlabTypes = new HashMap<>();
			splittingSlabTypes = new HashMap<>();
		}
	}
	
	@Inject(method = "getMovedBlocks", at = @At(value = "HEAD"))
	private void onGetMovedBlocksInjectAtHead(CallbackInfoReturnable<List<BlockPos>> cir) {
		for (BlockPos pos : movedBlocks) {
			// Notify clients of any pistons that are about to be "double retracted"
			if (Settings.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
				PistonHelper.getDoubleRetractionState(world, pos);
			}
			
			// Create list of block entities that are about to be moved
			if (Settings.Global.MOVABLE_BLOCK_ENTITIES.get()) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				movedBlockEntities.add(blockEntity);
				
				if (blockEntity != null) {
					world.removeBlockEntity(pos);
					
					// Fix for disappearing block entities on the client
					if (world.isClient()) {
						blockEntity.markDirty();
					}
				}
			}
		}
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
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private void onTryMoveInjectAfterGetBlock0(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState movedState) {
		if (Settings.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedState)) {
			// Usually the direction given is the opposite of the side from which the block is pulled along,
			// but when tryMove is called inside calculatePush for a retraction event, it is not
			Direction direction = (pos == posTo && !retracted) ? dir : dir.getOpposite();
			
			if (shouldTrySplitting(pos, direction)) {
				// tryMove is usually only called in a situation where a block is pulled along, with the one
				// exception being when it is called inside calculatePush for an extension event. In this case
				// a double slab should not split
				if (pos != posTo || !retracted) {
					System.out.println("trying to split " + movedState);
					trySplitDoubleSlab(pos, movedState, SlabHelper.getTypeFromDirection(direction));
				}
			} else {
				// The block is being pulled along regardless, and if it was pulled from a horizontal face
				// it is already splitting from another direction that means the entire block will move
				splittingSlabTypes.remove(pos);
			}
			
			if (!splittingSlabTypes.containsKey(pos)) {
				// If the block moves as a whole it cannot be merged into
				mergingSlabTypes.remove(pos);
			}
		}
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsBlockSticky0(Block block) {
		return true;
	}
	
	private boolean onTryMoveIsAdjacentBlockStuck;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onTryMoveInjectBeforeIsAdjacentBlockStuck(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState behindState, Block block, int i, BlockPos behindPos) {
		BlockPos blockPos = behindPos.offset(motionDirection);
		BlockState blockState = world.getBlockState(blockPos);
		onTryMoveIsAdjacentBlockStuck = isAdjacentBlockStuck(blockPos, blockState, behindPos, behindState, motionDirection.getOpposite());
		
		if (onTryMoveIsAdjacentBlockStuck && Settings.Global.MERGE_SLABS.get() && SlabHelper.isSlab(behindState)) {
			if (shouldTrySplitting(behindPos, motionDirection)) {
				trySplitDoubleSlab(behindPos, behindState, SlabHelper.getTypeFromDirection(motionDirection));
			}
			
			// If the slab is pulled in the direction of the movement, it can't be merged into!
			mergingSlabTypes.remove(behindPos);
		}
		
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsAdjacentBlockStuck(Block block1, Block block2) {
		return onTryMoveIsAdjacentBlockStuck;
	}
	
	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I"))
	private int onTryMoveRedirectIndexOf(List<Object> movedBlocks, Object pos) {
		if (Settings.Global.MERGE_SLABS.get()) {
			// If a double slab is split the half that is left behind can still be pushed
			if (splittingSlabTypes.containsKey(pos)) {
				return -1;
			}
		}
		return movedBlocks.indexOf(pos);
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private void onTryMoveInjectBeforeGetPistonBehavior(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState frontState, Block block, int i, int j, int distance, BlockPos frontPos) {
		if (Settings.Global.MERGE_SLABS.get()) {
			System.out.println(frontState + " being pushed!");
			
			// We break out of the loop to prevent the front block from being added to the movedBlocks list multiple times
			boolean breakLoop = movedBlocks.contains(frontPos);
			
			BlockPos blockPos = pos.offset(motionDirection, distance - 1);
			BlockState movedState = world.getBlockState(blockPos);
			
			// If the moved state is a split double slab only one half of it will be moving
			if (splittingSlabTypes.containsKey(blockPos)) {
				movedState = movedState.with(Properties.SLAB_TYPE, splittingSlabTypes.get(blockPos));
			}
			
			if (tryMergeSlabs(movedState, frontPos, frontState)) {
				// tryMergeSlabs will also return true if the moved state merges into a double slab,
				// in which case one half will be pushed over. In this case the front position should
				// be added to the movedBlocksList, otherwise we break out of the loop
				if (!trySplitDoubleSlab(frontPos, frontState, mergingSlabTypes.get(frontPos))) {
					// If the position is merged into we also don't add it to the movedBlocks list
					// since it might not be moving at all!
					breakLoop = true;
				}
			} else {
				// If no merging is occurring, the front position will be moved as a whole,
				// so we can remove it from the splittingSlabTypes list
				splittingSlabTypes.remove(frontPos);
			}
			
			if (isPotentiallySticky(frontState.getBlock())) {
				if (isAdjacentBlockStuck(frontPos, frontState, blockPos, movedState, motionDirection.getOpposite())) {
					splittingSlabTypes.remove(blockPos);
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
		return sticky ? Settings.StickyPiston.PUSH_LIMIT.get() : Settings.NormalPiston.PUSH_LIMIT.get();
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
	
	// dir is the direction from pos towards adjacentPos
	private boolean isAdjacentBlockStuck(BlockPos pos, BlockState pullingState, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		if (SlabHelper.isSlab(adjacentState) && !PistonHelper.canSlabStickTo(adjacentState, dir.getOpposite()))
			return false;
		
		// Default vanilla implementation: slime and honey do not stick to each other
		if (pullingState.isOf(Blocks.SLIME_BLOCK) && !adjacentState.isOf(Blocks.HONEY_BLOCK)) {
			return true;
		}
		if (pullingState.isOf(Blocks.HONEY_BLOCK) && !adjacentState.isOf(Blocks.SLIME_BLOCK)) {
			return true;
		}
		
		if (Settings.StickyPiston.SUPER_STICKY.get() && pullingState.isOf(Blocks.STICKY_PISTON)) {
			return dir == pullingState.get(Properties.FACING);
		}
		if (Settings.Global.CHAINSTONE.get() && pullingState.isOf(Blocks.CHAIN)) {
			Direction.Axis axis = pullingState.get(Properties.AXIS);
			
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
		if (Settings.Global.MOVABLE_BLOCK_ENTITIES.get() && (pullingState.isOf(Blocks.CHEST) || pullingState.isOf(Blocks.TRAPPED_CHEST))) {
			ChestType chestType = pullingState.get(Properties.CHEST_TYPE);
			
			if (chestType == ChestType.SINGLE) {
				return false;
			}
			
			if (adjacentState.isOf(pullingState.getBlock()) && adjacentState.get(Properties.CHEST_TYPE) == chestType.getOpposite()) {
				Direction facing = pullingState.get(Properties.HORIZONTAL_FACING);

				if (chestType == ChestType.LEFT) {
					return (dir == facing.rotateYClockwise());
				}
				return (dir == facing.rotateYCounterclockwise());
			}
		}
	
		return false;
	}
	
	private static boolean isPotentiallySticky(Block block) {
		if (block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK) {
			return true;
		}
		if (Settings.StickyPiston.SUPER_STICKY.get() && block == Blocks.STICKY_PISTON) {
			return true;
		}
		if (Settings.Global.CHAINSTONE.get() && block == Blocks.CHAIN) {
			return true;
		}
		if (Settings.Global.MOVABLE_BLOCK_ENTITIES.get() && (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)) {
			return true;
		}
		return false;
	}
	
	@Override
	public Map<BlockPos, SlabType> getMergingSlabTypes() {
		return mergingSlabTypes;
	}
	
	@Override
	public Map<BlockPos, SlabType> getSplittingSlabTypes() {
		return splittingSlabTypes;
	}
	
	private boolean shouldTrySplitting(BlockPos pos, Direction dir) {
		return dir.getAxis().isVertical() && !movedBlocks.contains(pos);
	}
	
	// Check if a block state is a double slab block that should split
	// if a pulling force is applied to the given half
	private boolean trySplitDoubleSlab(BlockPos pos, BlockState movedState, SlabType half) {
		if (movedState.get(Properties.SLAB_TYPE) == SlabType.DOUBLE) {
			SlabType oldHalf = splittingSlabTypes.get(pos);
			
			if (oldHalf == null) {
				splittingSlabTypes.put(pos, half);
				
				return true;
			} else if (oldHalf != half) {
				splittingSlabTypes.remove(pos);
				mergingSlabTypes.remove(pos.offset(motionDirection));
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
				mergingSlabTypes.put(toPos, fromType);
				return true;
			}
		}
		return false;
	}
}
