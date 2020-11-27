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
import redstonetweaks.interfaces.RTIBlock;
import redstonetweaks.interfaces.RTIPistonHandler;
import redstonetweaks.interfaces.RTIServerWorld;
import redstonetweaks.interfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;
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
			tryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void neighborUpdateRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			tryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onBlockAddedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, state.getBlock())) {
			tryMove(world, pos, state, false);
		}
	}
	
	@ModifyVariable(method = "onSyncedBlockEvent", argsOnly = true, ordinal = 0, at = @At(value = "HEAD"))
	private int modifyTypeValue(int oldType) {
		return (oldType == 2 ? 1 : oldType);
	}
	
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "HEAD"), cancellable = true)
	private void onOnSyncedBlockEventInjectAtHead(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!((RTIWorld)world).updateNeighborsImmediately()) {
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
		return Tweaks.Global.DOUBLE_RETRACTION.get() ? oldFlags | 16 : oldFlags;
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
	private float onOnSyncedBlockEventExtensionOnPlaySoundModifyPitch(float basePitch) {
		return PistonHelper.adjustSoundPitch(basePitch, true, sticky);
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventRetractionOnPlaySoundModifyPitch(float basePitch) {
		return PistonHelper.adjustSoundPitch(basePitch, false, sticky);
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnSyncedBlockEventInjectAfterFinish0(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (!(world.getBlockState(pos).getBlock() instanceof PistonBlock)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onOnSyncedBlockEventRedirectGetBlockState(World world, BlockPos pos) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
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
			if (Tweaks.StickyPiston.DO_BLOCK_DROPPING.get()) {
				return true;
			}
			pistonBlockEntity.finish();
		}
		return false;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnSyncedBlockEventRedirectFinish1(PistonBlockEntity pistonBlockEntity) {
		if (Tweaks.StickyPiston.FAST_BLOCK_DROPPING.get()) {
			pistonBlockEntity.finish();
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private PistonBehavior onOnSyncedBlockEventRedirectGetPistonBehavior(BlockState state) {
		return PistonHelper.getPistonBehavior(state);
	}
	
	@Inject(method = "onSyncedBlockEvent", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private void onOnSyncedBlockEventInjectAfterRemoveBlock(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction facing, BlockEntity blockEntity, BlockState blockState, BlockPos blockPos, BlockState blockState2, boolean droppedBlock) {
		if (!Tweaks.StickyPiston.DO_BLOCK_DROPPING.get()) {
			move(world, pos, facing, false);
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onOnSyncedBlockEventRedirectCreateBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		return PistonHelper.createPistonBlockEntity(pushedBlock, dir, extending, source, sticky);
	}
	
	@Inject(method = "isMovable", cancellable = true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/block/Blocks;PISTON:Lnet/minecraft/block/Block;"))
	private static void onIsMovedInjectBeforeBlockPiston(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
		if (PistonHelper.movableWhenExtended(false) && ((state.isOf(Blocks.PISTON) && state.get(Properties.EXTENDED)) || (state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.DEFAULT))) {
			cir.setReturnValue(true);
			cir.cancel();
		} else
		if (PistonHelper.movableWhenExtended(true) && ((state.isOf(Blocks.STICKY_PISTON) && state.get(Properties.EXTENDED)) || (state.isOf(Blocks.PISTON_HEAD) && state.get(Properties.PISTON_TYPE) == PistonType.STICKY))) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Inject(method = "isMovable", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"))
	private static void onIsMovedInjectBeforeGetHardness(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
		if (Tweaks.Barrier.IS_MOVABLE.get() && state.isOf(Blocks.BARRIER)) {
			cir.setReturnValue(true);
			cir.cancel();
		} else
		if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get() && state.isOf(Blocks.MOVING_PISTON)) {
			boolean movable = true;
			
			// Prevent a piston from pushing its own extending piston head
			if (direction == pistonDir && direction == state.get(Properties.FACING)) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				
				if (blockEntity instanceof PistonBlockEntity) {
					PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
					
					movable = !(pistonBlockEntity.isSource() && pistonBlockEntity.isExtending());
				}
			}
			
			cir.setReturnValue(movable);
			cir.cancel();
		}
	}
	
	@Redirect(method = "isMovable", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;hasBlockEntity()Z"))
	private static boolean onIsMovableRedirectHasBlockEntity(Block block) {
		if (block.hasBlockEntity()) {
			if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get()) {
				return !PistonHelper.canMoveBlockEntityOf(block);
			}
			if (block == Blocks.TARGET) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private void onMoveInjectBeforeListAdd(World world, BlockPos pistonPos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedBlocksMap, List<BlockPos> movedBlocksPos, List<BlockState> movedBlocks, int index, BlockPos movedPos, BlockState movedState) {
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get()) {
			BlockEntity movedBlockEntity = world.getBlockEntity(movedPos);
			
			((RTIPistonHandler)pistonHandler).addMovedBlockEntity(movedBlockEntity);
			
			if (movedBlockEntity != null) {
				world.removeBlockEntity(movedPos);
				
				// Fix for disappearing block entities on the client
				if (world.isClient()) {
					movedBlockEntity.markDirty();
				}
			}
		}
		
		// Notify clients of any pistons that are about to be "double retracted"
		PistonHelper.prepareDoubleRetraction(world, movedPos, movedState);
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockState2(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenBlocksPos,
			BlockState[] affectedStates, Direction motionDirection, int affectedIndex, int movedIndex, BlockPos toPos) 
	{
		BlockState movedState = movedStates.get(movedIndex);
		BlockEntity movedBlockEntity = null;
		boolean isMergingSlabs = false;
		
		if (Tweaks.Global.MOVABLE_BLOCK_ENTITIES.get()) {
			movedBlockEntity = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities().get(movedIndex);
		}
		if (Tweaks.Global.MERGE_SLABS.get() && SlabHelper.isSlab(movedState)) {
			Map<BlockPos, SlabType> splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
			Map<BlockPos, SlabType> mergedSlabTypes = ((RTIPistonHandler)pistonHandler).getMergedSlabTypes();
			
			BlockPos fromPos = toPos.offset(motionDirection.getOpposite());
			
			SlabType movedType = splitSlabTypes.get(fromPos);
			if (movedType != null) {
				SlabType remainingType = SlabHelper.getOppositeType(movedType);
				BlockState remainingState = movedState.with(Properties.SLAB_TYPE, remainingType);
				
				movedStatesMap.put(fromPos, remainingState);
				world.setBlockState(fromPos, remainingState, 4);
				
				movedState = movedState.with(Properties.SLAB_TYPE, movedType);
			}
			if (mergedSlabTypes.containsKey(toPos)) {
				isMergingSlabs = true;
			}
		}
		
		world.setBlockEntity(toPos, PistonHelper.createPistonBlockEntity(movedState, movedBlockEntity, pistonDir, extend, false, sticky, isMergingSlabs));
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity0(World world, BlockPos pos, BlockEntity blockEntity) {
		
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveInjectBeforeSetBlockEntity1(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions,
			BlockState[] affectedStates, PistonType headType, BlockState headState) 
	{
		world.setBlockEntity(headPos, PistonHelper.createPistonBlockEntity(headState, pistonDir, true, true, sticky));
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity1(World world1, BlockPos toPos, BlockEntity pistonBlockEntity, World world, BlockPos pos, Direction pistonDir, boolean extend) {
		
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectAfterUpdateNeighborsAlways1(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions,
			BlockState[] affectedStates, int affectedIndex, int movedIndex)
	{
		if (Tweaks.BugFixes.MC120986.get()) {
			BlockState state = affectedStates[affectedIndex - 1];
			
			if (state.hasComparatorOutput()) {
				world.updateComparators(movedPositions.get(movedIndex), state.getBlock());
			}
		}
	}
	
	@Inject(method = "move", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectBeforeUpdateNeighborsAlways2(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir) {
		if (!PistonHelper.headUpdatesOnExtension(sticky)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 4, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockState4(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> remainingStates, List<BlockPos> list, List<BlockState> list2, List<BlockPos> list3, BlockState[] affectedStates, BlockState airState, Iterator<BlockPos> var25, BlockPos leftOverPos) {
		Map<BlockPos, SlabType> splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
		if (!Tweaks.Global.MERGE_SLABS.get() || !splitSlabTypes.containsKey(leftOverPos)) {
			world.setBlockState(leftOverPos, airState, 82);
		}
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private boolean onMoveRedirectSetBlockState4(World world, BlockPos pos, BlockState newState, int flags) {
		// Replaced by above inject
		return false;
	}

	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/BlockState;updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveInjectBeforeUpdateNeighbors(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions,
			BlockState[] affectedStates, BlockState airState, Iterator<BlockPos> leftOverPositions, Map.Entry<BlockPos, BlockState> entry) {
		if (Tweaks.Global.MERGE_SLABS.get() && ((RTIPistonHandler)pistonHandler).getSplitSlabTypes().containsKey(entry.getKey())) {
			BlockState newState = movedStatesMap.get(entry.getKey());
			
			newState.updateNeighbors(world, entry.getKey(), 2);
			newState.prepare(world, entry.getKey(), 2);
			
		} else {
			airState.updateNeighbors(world, entry.getKey(), 2);
			airState.prepare(world, entry.getKey(), 2);
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
		tryMove(world, pos, state, true);
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
	private void tryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
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
		
		// Usually the tryMove method is only called from inside the onPlaced, neighborUpdate and
		// onBlockAdded methods. However, Redstone Tweaks allows players to add activation delay to
		// pistons. This is done using scheduled ticks.
		// Redstone Tweaks also adds the lazy setting. If this setting is enabled, pistons should not
		// check for power if the newTryMove method is called from scheduledTick method. Instead,
		// the value of shouldExtend is inferred from the current value of the EXTENDED property.
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
				// updateSelfWhilePowered and lazy settings are both enabled
				if (powered && PistonHelper.updateSelfWhilePowered(sticky) && !PistonHelper.isExtending(world, pos, state, facing)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonHelper.tickPriorityRisingEdge(sticky));
				}
			}
			if (Tweaks.RedstoneTorch.SOFT_INVERSION.get() && !onScheduledTick) {
				updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
			}
		} else if (!shouldExtend) {
			if (isExtended && !(PistonHelper.ignoreUpdatesWhileExtending(sticky) && PistonHelper.isExtending(world, pos, state, facing))) {
				if (activationDelay == 0 || onScheduledTick) {
					if (Tweaks.Global.DOUBLE_RETRACTION.get()) {
						world.setBlockState(pos, state.with(Properties.EXTENDED, false), 16);
					}
					world.addSyncedBlockEvent(pos, state.getBlock(), 1, facing.getId());
				} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), activationDelay, PistonHelper.tickPriorityFallingEdge(sticky));
				}
			}
			if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
				updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
			}
		}
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
