package redstonetweaks.mixin.server;

import java.util.ArrayList;
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

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.SlabBlock;
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
	@Shadow @Final private List<BlockPos> movedBlocks;

	private boolean sticky;
	private List<BlockPos> anchoredChains;
	private List<BlockEntity> movedBlockEntities;
	private Map<BlockPos, SlabType> splitSlabTypes;
	
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
			splitSlabTypes = Maps.newHashMap();
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
	private boolean onCalculatePushInjectAtHead(BlockState blockState, World world, BlockPos blockPos, Direction direction, boolean canBreak, Direction pistonDir) {
		return PistonBlock.isMovable(blockState, world, blockPos, direction, canBreak, pistonDir) && (retracted || !SlabHelper.isSlab(blockState) || PistonHelper.canSlabStickTo(blockState, direction));
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private void onTryMoveInjectAfterGetState0(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
		if (Settings.Global.MERGE_SLABS.get()) {
			// Note: The direction is incorrect in the calculatePush method.
			Direction fixedDir = (pos != posTo || retracted) ? dir : dir.getOpposite();
			
			if (!tryAddSplitSlab(pos, fixedDir, blockState, (pos == posTo && retracted))) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsBlockSticky0(Block block) {
		return true;
	}
	
	private boolean onTryMoveIsAdjacentBlockStuck;
	
	@Inject(method = "tryMove", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onTryMoveInjectBeforeIsAdjacentBlockStuck(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, Block block, int i, BlockPos blockPos) {
		BlockPos prevBlockPos = blockPos.offset(motionDirection);
		BlockState pullingState = world.getBlockState(prevBlockPos);
		onTryMoveIsAdjacentBlockStuck = isAdjacentBlockStuck(prevBlockPos, pullingState, blockPos, blockState, motionDirection.getOpposite());
	}

	@Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean onTryMoveRedirectIsAdjacentBlockStuck(Block block1, Block block2) {
		return onTryMoveIsAdjacentBlockStuck;
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private void onTryMoveBeforeListAdd0(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, Block block, int i, int j, int l) {
		if (Settings.Global.MERGE_SLABS.get() && l != 0) {
			Direction direction = motionDirection.getOpposite();
			BlockPos blockPos = pos.offset(direction, l);
			BlockState state = world.getBlockState(blockPos);
			
			if (!tryAddSplitSlab(blockPos, direction, state, false)) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private void onTryMoveInjectBeforeGetPistonBehavior(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState frontState, Block block, int i, int j, int l) {
		if (Settings.Global.MERGE_SLABS.get() && SlabHelper.isSlab(frontState)) {
			SlabType frontType = frontState.get(SlabBlock.TYPE);

			if (frontType != SlabType.DOUBLE) {
				// Check the state right behind the front block.
				BlockPos pushedPos = pos.offset(motionDirection, l - 1);
				BlockState pushedState = world.getBlockState(pushedPos);
			
				if (pushedState.isOf(frontState.getBlock())) {
					// Make sure that we also merge if the pushed slab is split.
					SlabType pushedType = splitSlabTypes.getOrDefault(pushedPos, pushedState.get(SlabBlock.TYPE));
					
					if (pushedType == SlabHelper.getOppositeType(frontType)) {
						// Make sure that we can actually merge the slabs from the pushing direction.
						if (motionDirection.getAxis().isHorizontal() || frontType == SlabHelper.getTypeFromDirection(motionDirection)) {
							cir.setReturnValue(true);
							cir.cancel();
						}
					}
				}
			}
		}
	}
	
	@Inject(method = "tryMove", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 2, shift = Shift.BEFORE, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private void onTryMoveBeforeListAdd2(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, Block block, int i, int j, int l) {
		if (Settings.Global.MERGE_SLABS.get()) {
			BlockPos blockPos = pos.offset(motionDirection, l);
			BlockState state = world.getBlockState(blockPos);
			
			if (!tryAddSplitSlab(blockPos, motionDirection, state, true)) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}
	
	private boolean tryAddSplitSlab(BlockPos pos, Direction dir, BlockState blockState, boolean pushing) {
		if (SlabHelper.isSlab(blockState) && blockState.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
			SlabType type = SlabType.DOUBLE;
			
			if (pushing) {
				if (motionDirection.getAxis().isVertical()) {
					type = SlabType.DOUBLE;
				} else {
					BlockPos pushingPos = pos.offset(dir.getOpposite());
					BlockState pushingState = world.getBlockState(pushingPos);
					
					if (pushingState.isOf(blockState.getBlock())) {
						type = pushingState.get(SlabBlock.TYPE);
						
						if (type == SlabType.DOUBLE) {
							// Note: default case should never be caught..
							type = splitSlabTypes.getOrDefault(pushingPos, SlabType.DOUBLE);
						}
					}
				}
			} else if (dir.getAxis().isVertical()) {
				type = SlabHelper.getTypeFromDirection(dir.getOpposite());
			}

			SlabType currentType = splitSlabTypes.get(pos);
			
			if (currentType != null && currentType != type) {
				// The slab is moved from multiple directions.
				if (currentType != SlabType.DOUBLE) {
					splitSlabTypes.put(pos, SlabType.DOUBLE);
					return ensureSplitSlabsArePushed(pos, currentType);
				}
			} else {
				splitSlabTypes.put(pos, type);
			}
		}
		
		return true;
	}
	
	private boolean ensureSplitSlabsArePushed(BlockPos pos, SlabType oldType) {
		BlockPos frontPos = pos.offset(motionDirection);
		BlockState frontState = world.getBlockState(frontPos);
		
		if (splitSlabTypes.get(frontPos) == oldType) {
			tryAddSplitSlab(frontPos, motionDirection, frontState, true);
		} else if (SlabHelper.isSlab(frontState) && !movedBlocks.contains(frontPos)) {
			return tryMove(frontPos, motionDirection);
		}
		
		return true;
	}
	
	@ModifyConstant(method = "tryMove", constant = @Constant(intValue = 12))
	private int pushLimit(int oldPushLimit) {
		return sticky ? Settings.StickyPiston.PUSH_LIMIT.get() : Settings.NormalPiston.PUSH_LIMIT.get();
	}

	@Inject(method = "canMoveAdjacentBlock", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private void onCanMoveAdjacentBlockInjectBeforeIsAdjacentBlockStuck(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState pullingState, Direction[] directions, int len, int i, Direction dir, BlockPos adjacentPos, BlockState adjacentState) {
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
	
	// dir is the direction from the pulling state towards to adjacent state
	private boolean isAdjacentBlockStuck(BlockPos pos, BlockState pullingState, BlockPos adjacentPos, BlockState adjacentState, Direction dir) {
		if (SlabHelper.isSlab(adjacentState) && !PistonHelper.canSlabStickTo(adjacentState, dir.getOpposite()))
			return false;
		
		// Default vanilla implementation. Slime and honey does not stick.
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
	public Map<BlockPos, SlabType> getSplitSlabTypes() {
		return splitSlabTypes;
	}
}
