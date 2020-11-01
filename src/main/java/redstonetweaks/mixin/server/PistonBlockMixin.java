package redstonetweaks.mixin.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.interfaces.RTIPistonBlockEntity;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.interfaces.RTIBlock;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.interfaces.RTIServerWorld;
import redstonetweaks.world.common.UnfinishedEvent.Source;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin extends Block implements RTIBlock {
	
	@Shadow @Final private boolean sticky;
	
	protected PistonBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow public static native boolean isMovable(BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir);
	@Shadow protected abstract boolean move(World world, BlockPos pos, Direction dir, boolean retract);
	
	@Redirect(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onPlacedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			newTryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void neighborUpdateRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			newTryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onBlockAddedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			newTryMove(world, pos, state, false);
		}
	}
	
	@ModifyVariable(method = "onSyncedBlockEvent", argsOnly = true, ordinal = 0, at = @At(value = "HEAD"))
	private int modifyTypeValue(int oldType) {
		return (oldType == 2 ? 1 : oldType);
	}
	
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "HEAD"), cancellable = true)
	private void onOnSyncedBlockEventInjectAtHead(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!((RTIWorld)world).updateNeighborsNormally()) {
			BlockEventHandler blockEventHandler = new BlockEventHandler(world, pos, state, type, data, sticky);
			boolean startedBlockEvent = false;
			
			if (((RTIWorld)world).addBlockEventHandler(blockEventHandler)) {
				startedBlockEvent = blockEventHandler.startBlockEvent();
				if (startedBlockEvent) {
					if (!world.isClient()) {
						BlockState blockState = world.getBlockState(pos);
						((RTIServerWorld)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, blockState, pos, 0, 64.0D);
					}
				}
			}
			
			cir.setReturnValue(startedBlockEvent);
			cir.cancel();
		}
	}
	
	// If the lazy setting is enabled,
	// the value of bl is is inferred from the current value
	// of the EXTENDED property.
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;shouldExtend(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onOnSyncedBlockEventRedirectShouldExtend(PistonBlock piston, World world1, BlockPos pos1, Direction direction, BlockState state, World world, BlockPos pos, int type, int data) {
		boolean extended = type != 0;
		boolean lazy = extended ? PistonHelper.lazyFallingEdge(sticky) : PistonHelper.lazyRisingEdge(sticky);
		return lazy ? !extended : PistonHelper.isReceivingPower(world, pos, state, direction, true);
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 2, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	public int onOnSyncedBlockEventOnSetBlockState0ModifyFlags(int oldFlags) {
		return redstonetweaks.setting.Settings.Global.DOUBLE_RETRACTION.get() ? oldFlags | 16 : oldFlags;
	}
	
	// If the piston is powered but unable to extend and
	// the forceUpdatePoweredPistons setting is enabled,
	// a block tick should be scheduled in the next tick.
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "RETURN", ordinal = 1))
	private void onOnSyncedBlockEventInjectAtReturn1(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Float> cir) {
		if (PistonHelper.updateSelfWhilePowered(sticky)) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonHelper.tickPriorityRisingEdge(sticky));
		}
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventExtensionOnPlaySoundModifyPitch(float oldPitch) {
		int speed = PistonHelper.speedRisingEdge(sticky);
		return speed > 0 ? oldPitch * (2.0f / speed) : Float.POSITIVE_INFINITY;
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventRetractionOnPlaySoundModifyPitch(float oldPitch) {
		int speed = PistonHelper.speedFallingEdge(sticky);
		return speed > 0 ? oldPitch * (2.0f / speed) : Float.POSITIVE_INFINITY;
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnSyncedBlockEventInjectAfterFinish0(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!(world.getBlockState(pos).getBlock() instanceof PistonBlock)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onOnSyncedBlockEventGetBlockState(World world, BlockPos pos) {
		if (redstonetweaks.setting.Settings.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			BlockState state = world.getBlockState(pos);
			
			if (state.getBlock() instanceof PistonBlock && state.get(Properties.EXTENDED)) {
				world.updateNeighbor(pos, state.getBlock(), pos);
			}
		}
		return world.getBlockState(pos);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;isExtending()Z"))
	private boolean onOnSyncedBlockEventRedirectIsExtending(PistonBlockEntity pistonBlockEntity) {
		if (pistonBlockEntity.isExtending()) {
			if (redstonetweaks.setting.Settings.StickyPiston.DO_BLOCK_DROPPING.get() && !redstonetweaks.setting.Settings.StickyPiston.FAST_BLOCK_DROPPING.get()) {
				return false;
			}
			pistonBlockEntity.finish();
		}
		return false;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private PistonBehavior onOnSyncedBlockEventRedirectGetPistonBehavior(BlockState state) {
		return redstonetweaks.setting.Settings.Barrier.IS_MOVABLE.get() && state.isOf(Blocks.BARRIER) ? PistonBehavior.NORMAL : state.getPistonBehavior();
	}
	
	@Inject(method = "onSyncedBlockEvent", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private void onOnSyncedBlockEventInjectAfterRemoveBlock(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction facing, BlockEntity blockEntity, BlockState blockState, BlockPos blockPos, BlockState blockState2, boolean droppedBlock) {
		if (!redstonetweaks.setting.Settings.StickyPiston.DO_BLOCK_DROPPING.get()) {
			move(world, pos, facing, false);
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onOnSyncedBlockEventRedirectCreateBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlock, dir, extending, source);
		((RTIPistonBlockEntity)pistonBlockEntity).setIsMovedByStickyPiston(sticky);
		return pistonBlockEntity;
	}
	
	@Redirect(method = "isMovable", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z"))
	private static boolean onIsMovableRedirectIsAir(BlockState state) {
		return state.isAir() || (state.isOf(Blocks.BARRIER) && redstonetweaks.setting.Settings.Barrier.IS_MOVABLE.get());
	}
	
	@Redirect(method = "isMovable", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasBlockEntity()Z"))
	private static boolean onIsMovableRedirectHasBlockEntity(Block block) {
		if (block.hasBlockEntity()) {
			if (redstonetweaks.setting.Settings.Global.MOVABLE_BLOCK_ENTITIES.get()) {
				return !PistonHelper.canMoveBlockEntityOf(block);
			}
			if (block == Blocks.TARGET) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 2, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockEntity0(World world, BlockPos pos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedBlockStatesMap,
			List<BlockPos> movedBlocksPos, List<BlockState> movedBlockStates, List<BlockPos> brokenBlocksPos,
			BlockState[] affectedBlockStates, Direction motionDirection, int j, int index, BlockPos toPos) 
	{
		BlockState movedState = movedBlockStates.get(index);
		BlockEntity movedBlockEntity = null;
		boolean isMergingSlabs = false;
		
		if (redstonetweaks.setting.Settings.Global.MOVABLE_BLOCK_ENTITIES.get()) {
			movedBlockEntity = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities().get(index);
		}
		if (redstonetweaks.setting.Settings.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedState)) {
			Map<BlockPos, SlabType> splittingSlabTypes = ((RTIPistonHandler)pistonHandler).getSplittingSlabTypes();
			Map<BlockPos, SlabType> mergingSlabTypes = ((RTIPistonHandler)pistonHandler).getMergingSlabTypes();
			
			BlockPos fromPos = toPos.offset(motionDirection.getOpposite());
			
			if (splittingSlabTypes.containsKey(fromPos)) {
				SlabType movingType = splittingSlabTypes.get(fromPos);
				SlabType remainingType = SlabHelper.getOppositeType(movingType);
				world.setBlockState(fromPos, movedState.with(Properties.SLAB_TYPE, remainingType), 4);
				
				movedState = movedState.with(Properties.SLAB_TYPE, movingType);
			}
			if (mergingSlabTypes.containsKey(fromPos)) {
				isMergingSlabs = true;
			}
		}
		
		world.setBlockState(toPos, Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, pistonDir), 68);
		world.setBlockEntity(toPos, PistonHelper.createPistonBlockEntity(movedState, movedBlockEntity, pistonDir, extend, false, sticky, isMergingSlabs));
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onMoveRedirectSetBlockState2(World world, BlockPos pos, BlockState state, int flags) {
		return false;
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity0(World world, BlockPos pos, BlockEntity blockEntity) {
		
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveInjectBeforeSetBlockEntity1(World world, BlockPos pos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedBlockStatesMap,
			List<BlockPos> movedBlocksPos, List<BlockState> movedBlockStates, List<BlockPos> brokenBlocksPos,
			BlockState[] affectedBlockStates, PistonType type, BlockState blockState4) 
	{
		world.setBlockEntity(headPos, PistonHelper.createPistonBlockEntity(blockState4, pistonDir, true, true, sticky));
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity1(World world1, BlockPos toPos, BlockEntity pistonBlockEntity, World world, BlockPos pos, Direction pistonDir, boolean extend) {
		
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectBeforeUpdateNeighborsAlways(World world, BlockPos pos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedBlockStatesMap,
			List<BlockPos> movedBlocksPos, List<BlockState> movedBlockStates, List<BlockPos> brokenBlocksPos,
			BlockState[] affectedBlockStates, int j, int n)
	{
		if (redstonetweaks.setting.Settings.BugFixes.MC120986.get()) {
			BlockState movedBlockState = affectedBlockStates[j - 1];
			if (movedBlockState.hasComparatorOutput()) {
				world.updateComparators(movedBlocksPos.get(n), movedBlockState.getBlock());
			}
		}
	}
	
	@Inject(method = "move", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectBeforeUpdateNeighborsAlways2(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir) {
		if (PistonHelper.suppressHeadUpdatesOnExtension(sticky)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 4, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockState4(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> remainingStates, List<BlockPos> list, List<BlockState> list2, List<BlockPos> list3, BlockState[] affectedStates, BlockState airState, Iterator<BlockPos> var25, BlockPos fromPos) {
		Map<BlockPos, SlabType> splittingSlabTypes = ((RTIPistonHandler)pistonHandler).getSplittingSlabTypes();
		if (!redstonetweaks.setting.Settings.Global.MERGE_SLABS.get() || !splittingSlabTypes.containsKey(fromPos)) {
			world.setBlockState(fromPos, airState, 82);
		}
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onMoveRedirectSetBlockState4(World world, BlockPos pos, BlockState newState, int flags) {
		// Replaced by above inject
		return false;
	}

	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveInjectBeforeUpdateNeighbors(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> remainingStates, List<BlockPos> list, List<BlockState> list2, List<BlockPos> list3, BlockState[] affectedStates, BlockState blockState6, Iterator<BlockPos> var25, Map.Entry<BlockPos, BlockState> entry) {
		if (redstonetweaks.setting.Settings.Global.MERGE_SLABS.get()) {
			Map<BlockPos, SlabType> splittingSlabTypes = ((RTIPistonHandler)pistonHandler).getSplittingSlabTypes();
			BlockState adjustedState = PistonHelper.getAdjustedSlabState(blockState6, world, entry.getKey(), splittingSlabTypes);
			
			adjustedState.updateNeighbors(world, entry.getKey(), 2);
			adjustedState.prepare(world, entry.getKey(), 2);
		} else {
			blockState6.updateNeighbors(world, entry.getKey(), 2);
			blockState6.prepare(world, entry.getKey(), 2);
		}
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveRedirectUpdateNeighbors(BlockState state, WorldAccess world, BlockPos pos, int flags) {
		// Replaced by above inject
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/BlockState;prepare(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveRedirectPrepare1(BlockState state, WorldAccess world, BlockPos pos, int flags) {
		// Replaced by above inject
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		newTryMove(world, pos, state, true);
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		BlockEventHandler blockEventHandler = ((RTIWorld)world).getBlockEventHandler(pos);
		
		if (blockEventHandler != null) {
			if (blockEventHandler.tryContinueBlockEvent()) {
				if (!world.isClient()) {
					BlockState blockState = world.getBlockState(pos);
					((RTIServerWorld)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, blockState, pos, 0, 64.0D);
				}
			} else {
				((RTIWorld)world).removeBlockEventHandler(pos);
			}
		}
		
		return true;
	}
	
	// The onScheduledTick argument tells us if this method is called
	// from inside the scheduledTick method.
	private void newTryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		Direction facing = state.get(Properties.FACING);
		boolean isExtended = state.get(Properties.EXTENDED);
		int activationDelay;
		boolean lazy;
		if (isExtended) {
			activationDelay = PistonHelper.delayFallingEdge(sticky);
			lazy = PistonHelper.lazyFallingEdge(sticky);
		} else {
			activationDelay = PistonHelper.delayRisingEdge(sticky);
			lazy = PistonHelper.lazyRisingEdge(sticky);
		}
		boolean powered = PistonHelper.isReceivingPower(world, pos, state, facing);
		
		// Usually the tryMove method is only called from inside the
		// onPlaced, neighborUpdate and onBlockAdded methods.
		// However, Redstone Tweaks allows players to add activation delay to pistons.
		// This is done using scheduled ticks.
		// Redstone Tweaks also adds the lazy setting.
		// If this setting is enabled, pistons should not check for power
		// if the newTryMove method is called from scheduledTick method.
		// Instead, the value of shouldExtend is inferred from the current
		// value of the EXTENDED property.
		boolean shouldExtend = (onScheduledTick && lazy) ? !isExtended : powered;
		
		if (shouldExtend && !isExtended) {
			if ((new PistonHandler(world, pos, facing, true)).calculatePush()) {
				if (activationDelay == 0 || onScheduledTick) {
					world.addSyncedBlockEvent(pos, state.getBlock(), 0, facing.getId());
				} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), activationDelay, PistonHelper.tickPriorityRisingEdge(sticky));
				}
			} else {
				// We must check that the piston is currently not extending.
				// Otherwise the piston will continually pulse if the
				// forceUpdateWhenPowered and lazy settings are both enabled
				if (powered && PistonHelper.updateSelfWhilePowered(sticky) && !isExtending(world, pos, state, facing)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonHelper.tickPriorityRisingEdge(sticky));
				}
			}
			if (redstonetweaks.setting.Settings.RedstoneTorch.SOFT_INVERSION.get() && !onScheduledTick) {
				updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
			}
		} else if (!shouldExtend) {
			if (isExtended && !(PistonHelper.ignoreUpdatesWhileExtending(sticky) && isExtending(world, pos, state, facing))) {
				if (activationDelay == 0 || onScheduledTick) {
					if (redstonetweaks.setting.Settings.Global.DOUBLE_RETRACTION.get()) {
						world.setBlockState(pos, state.with(Properties.EXTENDED, false), 16);
					}
					world.addSyncedBlockEvent(pos, state.getBlock(), 1, facing.getId());
				} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), activationDelay, PistonHelper.tickPriorityFallingEdge(sticky));
				}
			}
			if (redstonetweaks.setting.Settings.RedstoneTorch.SOFT_INVERSION.get()) {
				updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
			}
		}
	}
	
	// The base of an extending piston is a piston block with the
	// EXTENDED property set to true, the same as an extended piston
	// So to determine whether the piston is extending, we need to
	// look at the block in front of the piston. If that block is
	// a moving block that is extending and facing the same direction
	// as the piston, then we can conclude that the piston is extending.
	private boolean isExtending(World world, BlockPos pos, BlockState state, Direction facing) {
		if (!(state.get(Properties.EXTENDED) || redstonetweaks.setting.Settings.Global.DOUBLE_RETRACTION.get())) {
			return false;
		}
		BlockPos frontPos = pos.offset(facing);
		BlockState frontState = world.getBlockState(frontPos);
		if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
			return world.getBlockEntity(frontPos) instanceof PistonBlockEntity;
		}
		
		return false;
	}
	
	private void updateAdjacentRedstoneTorches(World world, BlockPos pos, Block block) {
		if (!world.isDebugWorld()) {
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.offset(direction);
				if (world.getBlockState(neighborPos).getBlock() instanceof RedstoneTorchBlock) {
					world.updateNeighbor(neighborPos, block, pos);
				}
			}
		}
	}
}
