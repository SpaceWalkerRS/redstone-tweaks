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
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
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
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.block.piston.BlockEventHandler;
import redstonetweaks.block.piston.MotionType;
import redstonetweaks.block.piston.PistonSettings;
import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.SlabHelper;
import redstonetweaks.mixinterfaces.RTIBlock;
import redstonetweaks.mixinterfaces.RTIPistonBlockEntity;
import redstonetweaks.mixinterfaces.RTIPistonHandler;
import redstonetweaks.mixinterfaces.RTIServerWorld;
import redstonetweaks.mixinterfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin extends Block implements RTIBlock {
	
	@Shadow @Final private boolean sticky;
	
	protected PistonBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow public abstract boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data);
	@Shadow public static native boolean isMovable(BlockState state, World world, BlockPos pos, Direction motionDir, boolean canBreak, Direction pistonDir);
	@Shadow protected abstract boolean move(World world, BlockPos pos, Direction dir, boolean retract);
	
	@Redirect(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onPlacedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, piston)) {
			tryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void neighborUpdateRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, piston)) {
			tryMove(world, pos, state, false);
		}
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;tryMove(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onBlockAddedRedirectTryMove(PistonBlock piston, World world, BlockPos pos, BlockState state) {
		if (!world.getBlockTickScheduler().isTicking(pos, piston)) {
			tryMove(world, pos, state, false);
		}
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, at = @At(value = "HEAD"))
	private void onOnSyncedBlockEventInjectAtHead(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		BlockEventHandler blockEventHandler = new BlockEventHandler(world, pos, state, type, data, sticky);
		
		((RTIWorld)world).addBlockEventHandler(blockEventHandler);
		
		if (!((RTIWorld)world).immediateNeighborUpdates()) {
			boolean startedBlockEvent = blockEventHandler.startBlockEvent();
			
			if (startedBlockEvent) {
				if (!world.isClient()) {
					((RTIServerWorld)world).getUnfinishedEventScheduler().scheduleBlockAction(pos, 0, 64.0D, (Block)this);
				}
			} else {
				((RTIWorld)world).removeBlockEventHandler(pos);
			}
			
			cir.setReturnValue(startedBlockEvent);
			cir.cancel();
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;shouldExtend(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
	private boolean onOnSyncedBlockEventRedirectShouldExtend(PistonBlock piston, World world1, BlockPos pos1, Direction direction, BlockState state, World world, BlockPos pos, int type, int data) {
		boolean extending = type == MotionType.EXTEND || type == MotionType.EXTEND_BACKWARDS;
		boolean lazy = extending ? PistonSettings.lazyRisingEdge(sticky) : PistonSettings.lazyFallingEdge(sticky);
		return lazy ? extending : PistonHelper.isReceivingPower(world, pos, state, direction, true);
	}
	
	@ModifyConstant(method = "onSyncedBlockEvent", constant = @Constant(intValue = MotionType.RETRACT_A, ordinal = 0))
	private int onOnSyncedBlockEventModifyEqualsOne0(int oldValue, BlockState state, World world, BlockPos pos, int type, int data) {
		return type == MotionType.RETRACT_FORWARDS ? MotionType.RETRACT_FORWARDS : MotionType.RETRACT_A;
	}
	
	@ModifyConstant(method = "onSyncedBlockEvent", constant = @Constant(intValue = MotionType.EXTEND, ordinal = 1))
	private int onOnSyncedBlockEventModifyEqualsZero1(int oldValue, BlockState state, World world, BlockPos pos, int type, int data) {
		return type == MotionType.EXTEND_BACKWARDS ? MotionType.EXTEND_BACKWARDS : MotionType.EXTEND;
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 2, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private int onOnSyncedBlockEventOnSetBlockState0ModifyFlags(int oldFlags) {
		return Tweaks.Global.DOUBLE_RETRACTION.get() ? oldFlags | 16 : oldFlags;
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN", ordinal = 2, shift = Shift.BEFORE))
	private void onOnSyncedBlockEventInjectBeforeReturn2(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction dir) {
		if (PistonSettings.updateSelf(sticky)) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityRisingEdge(sticky));
		}
	}
	
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 1, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onOnSyncedBlockEventInjectBeforeSetBlockState1(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		// We remove the block event handler a little earlier to preserve vanilla behavior
		// like pistons 0-ticking from a button placed on them
		((RTIWorld)world).removeBlockEventHandler(pos);
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventExtensionOnPlaySoundModifyPitch(float basePitch) {
		return PistonHelper.adjustSoundPitch(basePitch, true, sticky);
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventRetractionOnPlaySoundModifyPitch(float basePitch) {
		return PistonHelper.adjustSoundPitch(basePitch, false, sticky);
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private void onOnSyncedBlockEventInjectBeforeGetBlockEntity0(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction pistonDir) {
		if (PistonSettings.looseHead(sticky) && !PistonHelper.hasPistonHead(world, pos, sticky, pistonDir)) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
	
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private void onOnSyncedBlockEventInjectBeforeGetBlockEntity0(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		PistonHelper.cancelDoubleRetraction(world, pos, state);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onOnSyncedBlockEventRedirectGetBlockEntity0(World world, BlockPos pos) {
		// The head does not need to be removed before calculatePush since the piston handler will ignore it
		return null;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onOnSyncedBlockEventRedirectCreateBlockEntityPiston(BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
		return PistonHelper.createPistonBlockEntity(pushedBlock, facing, extending, source, sticky);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onOnSyncedBlockEventRedirectGetBlockState(World world, BlockPos pos) {
		if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient()) {
			BlockState state = world.getBlockState(pos);
			
			if (PistonHelper.isPiston(state) && state.get(Properties.EXTENDED)) {
				world.updateNeighbor(pos, state.getBlock(), pos);
			}
		}
		return world.getBlockState(pos);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;isExtending()Z"))
	private boolean onOnSyncedBlockEventRedirectIsExtending(PistonBlockEntity pistonBlockEntity) {
		if (pistonBlockEntity.isExtending()) {
			if (PistonSettings.doBlockDropping()) {
				return true;
			}
			pistonBlockEntity.finish();
		}
		return false;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnSyncedBlockEventRedirectFinish1(PistonBlockEntity pistonBlockEntity, BlockState state, World world, BlockPos pos, int type, int data) {
		if (PistonSettings.fastBlockDropping()) {
			pistonBlockEntity.finish();
		}
	}
	
	@Inject(method = "onSyncedBlockEvent", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lnet/minecraft/block/entity/PistonBlockEntity;finish()V"))
	private void onOnSyncedBlockEventInjectAfterFinish1(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction pistonDir) {
		PistonHelper.tryBreakPistonHead(world, pos.offset(pistonDir), sticky, pistonDir);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z"))
	private boolean onOnSyncedBlockEventRedirectIsMovable(BlockState state, World world, BlockPos frontPos, Direction dir, boolean canBreak, Direction pistonDir) {
		BlockState frontState = world.getBlockState(frontPos);
		
		return PistonBlock.isMovable(frontState, world, frontPos, dir, canBreak, pistonDir) && PistonHelper.getPistonBehavior(frontState) == PistonBehavior.NORMAL;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"))
	private PistonBehavior onOnSyncedBlockEventRedirectGetPistonBehavior(BlockState frontState, BlockState state, World world, BlockPos pos, int type, int data) {
		// Replaced by the redirect above
		return PistonBehavior.NORMAL;
	}
	
	@Inject(method = "onSyncedBlockEvent", cancellable = true, at = @At(value = "RETURN", shift = Shift.BEFORE, ordinal = 3))
	private void onOnSyncedBlockEventInjectAtReturn3(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		boolean didBlockEvent = true;
		
		if (type == MotionType.EXTEND_BACKWARDS) {
			
			Direction facing = state.get(Properties.FACING);
			Direction motionDir = facing.getOpposite();
			
			if (move(world, pos, motionDir, true)) {
				PistonType pistonType = sticky ? PistonType.STICKY : PistonType.DEFAULT;
				
				// A short arm is placed so the rod does not poke out of the back of the piston base
				BlockState pistonHead = Blocks.PISTON_HEAD.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType).with(Properties.SHORT, true);
				world.setBlockState(pos, pistonHead, 67);
				
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, pistonType);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(state.with(Properties.EXTENDED, true), motionDir, true, true, sticky, true);
				
				BlockPos toPos = pos.offset(motionDir);
				
				((RTIWorld)world).queueBlockEntityPlacement(pistonBlockEntity);
				world.setBlockState(toPos, pistonExtension, 20);
				
				world.updateNeighbors(toPos, pistonExtension.getBlock());
				pistonExtension.updateNeighbors(world, toPos, 2);
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, true, sticky));
				
				didBlockEvent = true;
			} else {
				if (PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityRisingEdge(sticky));
				}
				
				didBlockEvent = false;
			}
		} else
		if (type == MotionType.RETRACT_FORWARDS) {
			Direction facing = state.get(Properties.FACING);
			
			if (PistonSettings.looseHead(sticky) && !PistonHelper.hasPistonHead(world, pos, sticky, facing)) {
				cir.setReturnValue(false);
				cir.cancel();
				
				return;
			}
			
			boolean canRetractForwards = false;
			
			if (sticky) {
				BlockPos frontPos = pos.offset(facing, 2);
				BlockState frontState = world.getBlockState(frontPos);
				
				if (!frontState.isAir() && PistonHelper.canPull(frontState) && !(isMovable(frontState, world, frontPos, facing.getOpposite(), false, facing) && new PistonHandler(world, pos, facing, false).calculatePush())) {
					canRetractForwards = true;
				}
			}
			
			if (canRetractForwards) {
				PistonHelper.cancelDoubleRetraction(world, pos, state);
				
				BlockPos headPos = pos.offset(facing);
				
				BlockState air = Blocks.AIR.getDefaultState();
				
				world.setBlockState(pos, air, 18);
				world.setBlockState(headPos, air, 18);
				
				world.updateNeighbors(pos, air.getBlock());
				air.updateNeighbors(world, pos, 2);
				
				BlockState pistonExtension = Blocks.MOVING_PISTON.getDefaultState().with(Properties.FACING, facing).with(Properties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT);
				PistonBlockEntity pistonBlockEntity = PistonHelper.createPistonBlockEntity(state.with(Properties.EXTENDED, false), facing, false, true, sticky, true);
				
				((RTIWorld)world).queueBlockEntityPlacement(pistonBlockEntity);
				world.setBlockState(headPos, pistonExtension, 67);
				
				world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, PistonHelper.getSoundPitch(world, false, sticky));
				
				didBlockEvent = true;
			} else {
				if (!world.isClient()) {
					world.addSyncedBlockEvent(pos, state.getBlock(), MotionType.RETRACT_A, data);
				}
				
				didBlockEvent = false;
			}
		}
		
		cir.setReturnValue(didBlockEvent);
		cir.cancel();
	}
	
	@Inject(method = "isMovable", cancellable = true, at = @At(value = "FIELD", shift = Shift.BEFORE, target = "Lnet/minecraft/block/Blocks;PISTON:Lnet/minecraft/block/Block;"))
	private static void onIsMovedInjectBeforeBlockPiston(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
		if (PistonHelper.isMovablePiston(world, pos, state, true) || PistonHelper.isMovablePiston(world, pos, state, false)) {
			cir.setReturnValue(true);
			cir.cancel();
		} else
		if (Tweaks.HayBale.DIRECTIONALLY_MOVABLE.get() && state.isOf(Blocks.HAY_BLOCK)) {
			cir.setReturnValue(state.get(Properties.AXIS) == direction.getAxis());
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
			
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				// Check if the block that is moved by the moving block is movable itself
				// By default piston heads are not movable but they do appear in the moving blocks of extending pistons
				BlockState movedState = ((RTIPistonBlockEntity)pistonBlockEntity).getMovedState();
				
				if (!movedState.isOf(Blocks.MOVING_PISTON) && PistonBlock.isMovable(movedState, world, pos, direction, canBreak, pistonDir)) {
					// Prevent a piston from pushing its own extending piston head
					if (direction == pistonDir && direction == state.get(Properties.FACING)) {
						movable = !(pistonBlockEntity.isSource() && pistonBlockEntity.isExtending());
					}
				} else {
					movable = false;
				}
			} else {
				movable = false;
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
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onMoveRedirectGetBlockState0(World world, BlockPos pos) {
		// The head does not need to be removed before calculatePush since the piston handler will ignore it
		return Blocks.AIR.getDefaultState();
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN", ordinal = 0))
	private void onMoveInjectAtReturn0(World world, BlockPos pos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos) {
		if (!extend) {
			PistonHelper.tryRemovePistonHead(world, headPos, sticky, pistonDir, PistonSettings.headUpdatesWhenPulling());
		}
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private void onMoveInjectBeforeListAdd(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap, List<BlockPos> movedBlocksPos,
			List<BlockState> movedStates, int index, BlockPos movedPos, BlockState movedState) {
		List<BlockEntity> movedBlockEntities = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities();
		
		Map<BlockPos, Boolean> detachedPistonHeads = ((RTIPistonHandler)pistonHandler).getDetachedPistonHeads();
		Map<BlockPos, SlabType> splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
		
		if (PistonHelper.isPiston(movedState) && !movedState.get(Properties.EXTENDED)) {
			if (Tweaks.Global.DOUBLE_RETRACTION.get() && !world.isClient() && ((RTIServerWorld)world).hasBlockEvent(movedPos, MotionType.RETRACT_A, MotionType.RETRACT_B, MotionType.RETRACT_FORWARDS)) {
				// Notify clients of any pistons that are about to be double retracted
				BlockUpdateS2CPacket packet = new BlockUpdateS2CPacket(world, movedPos);
				((ServerWorld)world).getServer().getPlayerManager().sendToAround(null, movedPos.getX(), movedPos.getY(), movedPos.getZ(), 64.0D, world.getRegistryKey(), packet);
			}
			if (detachedPistonHeads.containsKey(movedPos)) {
				if (detachedPistonHeads.get(movedPos)) {
					movedState = Blocks.PISTON_HEAD.getDefaultState().with(Properties.PISTON_TYPE, PistonHelper.isSticky(movedState) ? PistonType.STICKY : PistonType.DEFAULT).with(Properties.FACING, movedState.get(Properties.FACING));
				} else {
					movedState = movedState.with(Properties.EXTENDED, true);
				}
			}
		} else if (splitSlabTypes.containsKey(movedPos) && SlabHelper.isSlab(movedState)) {
			movedState = movedState.with(Properties.SLAB_TYPE, splitSlabTypes.get(movedPos));
		}
		
		movedStates.add(movedState);
		movedStatesMap.put(movedPos, movedState);
		
		BlockEntity movedBlockEntity = world.getBlockEntity(movedPos);
		
		if (movedBlockEntity != null) {
			world.removeBlockEntity(movedPos);
			
			// Fix for disappearing block entities on the client
			if (world.isClient()) {
				movedBlockEntity.markDirty();
			}
		}
		
		movedBlockEntities.add(movedBlockEntity);
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private boolean onMoveRedirectListAdd(List<Object> movedStates, Object state) {
		// Replaced by the inject above
		return false;
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object onMoveRedirectMapPut(Map<Object, Object> movedStatesMap, Object pos, Object state) {
		// Replaced by the inject above
		return state;
	}

	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonBlock;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V")), at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Ljava/util/List;size()I"))
	private void onMoveInjectBeforeListSize(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir, BlockPos headPos) {
		if (!extend) {
			PistonHelper.tryRemovePistonHead(world, headPos, sticky, pistonDir, PistonSettings.headUpdatesWhenPulling());
		}
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 2, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockState2(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenBlocksPos,
			BlockState[] removedStates, Direction moveDirection, int affectedIndex, int movedIndex, BlockPos toPos) 
	{
		List<BlockEntity> movedBlockEntities = ((RTIPistonHandler)pistonHandler).getMovedBlockEntities();
		Map<BlockPos, SlabType> mergedSlabTypes = ((RTIPistonHandler)pistonHandler).getMergedSlabTypes();
		Map<BlockPos, Boolean> detachedPistonHeads = ((RTIPistonHandler)pistonHandler).getDetachedPistonHeads();
		
		BlockPos fromPos = toPos.offset(moveDirection.getOpposite());
		
		BlockState movedState = movedStates.get(movedIndex);
		BlockEntity movedBlockEntity = movedBlockEntities.get(movedIndex);
		boolean isMergingSlabs = mergedSlabTypes.containsKey(toPos);
		boolean detachedPistonHead = detachedPistonHeads.containsKey(fromPos);
		
		if (detachedPistonHead) {
			((RTIWorld)world).queueBlockEntityPlacement(PistonHelper.createPistonBlockEntity(movedState, null, moveDirection, true, true, sticky, false, !detachedPistonHeads.get(fromPos)));
		} else {
			if (Tweaks.Global.MOVABLE_MOVING_BLOCKS.get()) {
				// This ensures the block entity gets placed
				world.setBlockState(toPos, Blocks.AIR.getDefaultState(), 80);
			}
			
			((RTIWorld)world).queueBlockEntityPlacement(PistonHelper.createPistonBlockEntity(movedState, movedBlockEntity, pistonDir, extend, false, sticky, isMergingSlabs, false));
		}
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity0(World world, BlockPos pos, BlockEntity blockEntity) {
		// Replaced by the inject above
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.BEFORE, ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveInjectBeforeSetBlockEntity1(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions,
			BlockState[] affectedStates, PistonType headType, BlockState pistonHead) 
	{
		world.setBlockEntity(headPos, PistonHelper.createPistonBlockEntity(pistonHead, pistonDir, true, true, sticky));
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;setBlockEntity(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;)V"))
	private void onMoveRedirectSetBlockEntity1(World world1, BlockPos toPos, BlockEntity pistonBlockEntity, World world, BlockPos pos, Direction pistonDir, boolean extend) {
		// Replaced by the inject above
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 4, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onMoveInjectBeforeSetBlockState4(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStates, List<BlockPos> list,
			List<BlockState> list2, List<BlockPos> list3, BlockState[] affectedStates, BlockState remainingState,
			Iterator<BlockPos> var25, BlockPos remainingPos) {
		Map<BlockPos, SlabType> splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
		Map<BlockPos, Boolean> detachedPistonHeads = ((RTIPistonHandler)pistonHandler).getDetachedPistonHeads();
		
		if (splitSlabTypes.containsKey(remainingPos)) {
			BlockState movedState = movedStates.get(remainingPos);
			
			if (SlabHelper.isSlab(movedState)) {
				remainingState = movedState.with(Properties.SLAB_TYPE, SlabHelper.getOppositeType(splitSlabTypes.get(remainingPos)));
			}
		} else if (detachedPistonHeads.containsKey(remainingPos)) {
			BlockState movedState = movedStates.get(remainingPos);
			
			if (detachedPistonHeads.get(remainingPos)) {
				if (PistonHelper.isPistonHead(movedState)) {
					remainingState = (PistonHelper.isStickyHead(movedState) ? Blocks.STICKY_PISTON : Blocks.PISTON).getDefaultState().with(Properties.EXTENDED, true).with(Properties.FACING, movedState.get(Properties.FACING));
				}
			} else {
				if (PistonHelper.isPiston(movedState)) {
					remainingState = Blocks.PISTON_HEAD.getDefaultState().with(Properties.PISTON_TYPE, PistonHelper.isSticky(movedState) ? PistonType.STICKY : PistonType.DEFAULT).with(Properties.FACING, movedState.get(Properties.FACING));
				}
			}
		}
		
		world.setBlockState(remainingPos, remainingState, 82);
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
			BlockState[] affectedStates, BlockState air, Iterator<BlockPos> leftOverPositions, Map.Entry<BlockPos, BlockState> entry)
	{
		Map<BlockPos, SlabType> splitSlabTypes = ((RTIPistonHandler)pistonHandler).getSplitSlabTypes();
		Map<BlockPos, Boolean> detachedPistonHeads = ((RTIPistonHandler)pistonHandler).getDetachedPistonHeads();
		
		BlockPos remainingPos = entry.getKey();
		BlockState remainingState = (splitSlabTypes.containsKey(remainingPos) || detachedPistonHeads.containsKey(remainingPos)) ? movedStatesMap.getOrDefault(remainingPos, air) : air;
		
		remainingState.updateNeighbors(world, remainingPos, 2);
		remainingState.prepare(world, remainingPos, 2);
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;updateNeighbors(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveRedirectUpdateNeighbors(BlockState state, WorldAccess world, BlockPos pos, int flags) {
		// Replaced by above inject
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/block/BlockState;prepare(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;I)V"))
	private void onMoveRedirectPrepare1(BlockState state, WorldAccess world, BlockPos pos, int flags) {
		// Replaced by above inject
	}
	
	@Inject(method = "move", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 1, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectAfterUpdateNeighborsAlways1(World world, BlockPos pistonPos, Direction pistonDir, boolean extend, CallbackInfoReturnable<Boolean> cir,
			BlockPos headPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> movedStatesMap,
			List<BlockPos> movedPositions, List<BlockState> movedStates, List<BlockPos> brokenPositions,
			BlockState[] affectedStates, int affectedIndex, int movedIndex)
	{
		if (Tweaks.BugFixes.MC120986.get()) {
			// We inject after the index has already incremented, so we have to subtract 1
			BlockState state = affectedStates[affectedIndex - 1];
			
			if (state.hasComparatorOutput()) {
				world.updateComparators(movedPositions.get(movedIndex), state.getBlock());
			}
		}
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveRedirectUpdateNeighborsAlways2(World world, BlockPos pos, Block block) {
		if (PistonSettings.headUpdatesOnExtension(sticky)) {
			world.updateNeighborsAlways(pos, block);
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		tryMove(world, pos, state, true);
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		BlockEventHandler blockEventHandler = ((RTIWorld)world).getBlockEventHandler(pos);
		
		if (blockEventHandler != null) {
			if (blockEventHandler.tryContinueBlockEvent()) {
				if (!world.isClient()) {
					((RTIServerWorld)world).getUnfinishedEventScheduler().scheduleBlockAction(pos, 0, 64.0D, (Block)this);
				}
			} else {
				((RTIWorld)world).removeBlockEventHandler(pos);
			}
		}
		
		return true;
	}
	
	private void tryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		if (((RTIWorld)world).hasBlockEventHandler(pos)) {
			return;
		}
		
		Direction facing = state.get(Properties.FACING);
		boolean isExtended = PistonHelper.isExtended(world, pos, state, false) || PistonHelper.isExtending(world, pos, state);
		int delay;
		boolean lazy;
		if (isExtended) {
			delay = PistonSettings.delayFallingEdge(sticky);
			lazy = PistonSettings.lazyFallingEdge(sticky);
		} else {
			delay = PistonSettings.delayRisingEdge(sticky);
			lazy = PistonSettings.lazyRisingEdge(sticky);
		}
		boolean powered = PistonHelper.isReceivingPower(world, pos, state, facing);
		
		boolean shouldExtend = (onScheduledTick && lazy) ? !isExtended : powered;
		
		if (shouldExtend && !isExtended) {
			int type = new PistonHandler(world, pos, facing, true).calculatePush() ? MotionType.EXTEND : ((PistonSettings.canMoveSelf(sticky) && new PistonHandler(world, pos, facing.getOpposite(), true).calculatePush()) ? MotionType.EXTEND_BACKWARDS : MotionType.NONE);
			
			if (type == MotionType.NONE) {
				if (powered && PistonSettings.updateSelf(sticky)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, PistonSettings.tickPriorityRisingEdge(sticky));
				}
			} else {
				if (delay == 0 || onScheduledTick) {
					world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
				} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, PistonSettings.tickPriorityRisingEdge(sticky));
				}
			}
		} else if (!shouldExtend && isExtended && (!PistonSettings.looseHead(sticky) || PistonHelper.hasPistonHead(world, pos, sticky, facing))) {
			int type = MotionType.RETRACT_A;
			
			if (sticky && PistonSettings.canMoveSelf(sticky)) {
				BlockPos frontPos = pos.offset(facing, 2);
				BlockState frontState = world.getBlockState(frontPos);
				
				if (!PistonHelper.isPushingBlock(world, frontPos, frontState, facing) && PistonHelper.canPull(frontState) && !(isMovable(frontState, world, frontPos, facing.getOpposite(), false, facing) && new PistonHandler(world, pos, facing, false).calculatePush())) {
					type = MotionType.RETRACT_FORWARDS;
				}
			}
			
			if (delay == 0 || onScheduledTick) {
				if (Tweaks.Global.DOUBLE_RETRACTION.get()) {
					world.setBlockState(pos, state.with(Properties.EXTENDED, false), 16);
				}
				
				world.addSyncedBlockEvent(pos, state.getBlock(), type, facing.getId());
			} else if (!((RTIServerWorld)world).hasBlockEvent(pos)) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay, PistonSettings.tickPriorityFallingEdge(sticky));
			}
		}
		
		if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
			updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
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
