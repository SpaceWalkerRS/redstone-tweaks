package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

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
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.PistonBlockEntityHelper;
import redstonetweaks.helper.PistonBlockHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.piston.BlockEventHandler;
import redstonetweaks.setting.SettingsPack;
import redstonetweaks.world.server.UnfinishedEvent.Source;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin extends Block implements BlockHelper {
	
	@Shadow @Final private boolean sticky;
	
	private BlockState movedBlockState = null;
	
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
		if (!((WorldHelper)world).updateNeighborsNormally()) {
			BlockEventHandler blockEventHandler = ((WorldHelper)world).getPistonBlockEventHandler();
			blockEventHandler.newBlockEvent(state, pos, type, data, sticky);
			
			boolean startedBlockEvent = blockEventHandler.startBlockEvent();
			if (startedBlockEvent) {
				if (!world.isClient()) {
					BlockState blockState = world.getBlockState(pos);
					((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, blockState, pos, 0, 64.0D);
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
	private boolean onOnBlockActionRedirectShouldExtend(PistonBlock piston, World world1, BlockPos pos1, Direction direction, BlockState state, World world, BlockPos pos, int type, int data) {
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(piston);
		boolean extended = type != 0;
		boolean lazy = extended ? settings.get(FALLING_LAZY) : settings.get(RISING_LAZY);
		return lazy ? !extended : PistonBlockHelper.isReceivingPower(world, pos, state, direction, true);
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 2, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	public int onOnSyncedBlockEventOnSetBlockState0ModifyFlags(int oldFlags) {
		return GLOBAL.get(DOUBLE_RETRACTION) ? (oldFlags & ~2 | 16 ) : oldFlags;
	}
	
	// If the piston is powered but unable to extend and
	// the forceUpdatePoweredPistons setting is enabled,
	// a block tick should be scheduled in the next tick.
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "RETURN", ordinal = 1))
	private void onOnSyncedBlockEventIfCannotExtend(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Float> cir) {
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(state.getBlock());
		if (settings.get(FORCE_UPDATE_WHEN_POWERED)) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, settings.get(RISING_TICK_PRIORITY));
		}
	}
	
	@Inject(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", ordinal = 0, shift = Shift.BEFORE, target = "Lnet/minecraft/block/PistonBlock;move(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Z"))
	private void onOnSyncedBlockEventInjectBeforeMove0(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir) {
		if (GLOBAL.get(DOUBLE_RETRACTION) && !world.isClient()) {
			PistonBlockHelper.getDoubleRetractionState(world, pos.offset(state.get(Properties.FACING)));
		}
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventExtensionOnPlaySoundModifyPitch(float oldPitch) {
		SettingsPack settings = sticky ? STICKY_PISTON : NORMAL_PISTON;
		return settings.get(RISING_SPEED) > 0 ? oldPitch * (2.0f / settings.get(RISING_SPEED)) : Float.POSITIVE_INFINITY;
	}
	
	@ModifyArg(method = "onSyncedBlockEvent", index = 5, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
	private float onOnSyncedBlockEventRetractionOnPlaySoundModifyPitch(float oldPitch) {
		SettingsPack settings = sticky ? STICKY_PISTON : NORMAL_PISTON;
		return settings.get(FALLING_SPEED) > 0 ? oldPitch * (2.0f / settings.get(FALLING_SPEED)) : Float.POSITIVE_INFINITY;
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	private BlockState onOnSyncedBlockEventGetBlockState(World world, BlockPos pos) {
		if (GLOBAL.get(DOUBLE_RETRACTION) && !world.isClient()) {
			return PistonBlockHelper.getDoubleRetractionState(world, pos);
		}
		return world.getBlockState(pos);
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/PistonBlockEntity;isExtending()Z"))
	private boolean onOnSyncedBlockEventRedirectIsExtending(PistonBlockEntity pistonBlockEntity) {
		if (pistonBlockEntity.isExtending()) {
			if (STICKY_PISTON.get(DO_BLOCK_DROPPING) && !STICKY_PISTON.get(FAST_BLOCK_DROPPING)) {
				return false;
			}
			pistonBlockEntity.finish();
		}
		return false;
	}
	
	@Inject(method = "onSyncedBlockEvent", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private void onOnSyncedBlockEventInjectAfterRemoveBlock(BlockState state, World world, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir, Direction facing, BlockEntity blockEntity, BlockState blockState, BlockPos blockPos, BlockState blockState2, boolean droppedBlock) {
		if (!STICKY_PISTON.get(DO_BLOCK_DROPPING)) {
			move(world, pos, facing, false);
		}
	}
	
	@Redirect(method = "onSyncedBlockEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onOnSyncedBlockEventRedirectCreateBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlock, dir, extending, source);
		((PistonBlockEntityHelper)pistonBlockEntity).setIsMovedByStickyPiston(sticky);
		return pistonBlockEntity;
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;"))
	private BlockEntity onMoveRedirectCreateBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		PistonBlockEntity pistonBlockEntity = new PistonBlockEntity(pushedBlock, dir, extending, source);
		((PistonBlockEntityHelper)pistonBlockEntity).setIsMovedByStickyPiston(sticky);
		return pistonBlockEntity;
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"))
	private Block onMoveRedirectGetBlock(BlockState state) {
		if (BUG_FIXES.get(MC120986)) {
			movedBlockState = state;
		}
		return state.getBlock();
	}
	
	@Redirect(method = "move", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveRedirectUpdateNeighborsAlways1(World world, BlockPos pos, Block block) {
		world.updateNeighborsAlways(pos, block);
		
		if (BUG_FIXES.get(MC120986)) {
			if (movedBlockState.hasComparatorOutput()) {
				world.updateComparators(pos, block);
			}
			
			movedBlockState = null;
		}
	}
	
	@Inject(method = "move", cancellable = true, at = @At(value = "INVOKE", ordinal = 2, shift = Shift.BEFORE, target = "Lnet/minecraft/world/World;updateNeighborsAlways(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
	private void onMoveInjectBeforeUpdateNeighborsAlways2(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir) {
		SettingsPack settings = sticky ? STICKY_PISTON : NORMAL_PISTON;
		if (!settings.get(HEAD_UPDATES_ON_EXTENSION)) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		newTryMove(world, pos, state, true);
	}
	
	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		BlockEventHandler blockEventHandler = ((WorldHelper)world).getPistonBlockEventHandler();
		if (blockEventHandler.tryContinueBlockEvent()) {
			if (!world.isClient()) {
				BlockState blockState = world.getBlockState(pos);
				((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, blockState, pos, 0, 64.0D);
			}
		}
		
		return true;
	}
	
	// The onScheduledTick argument tells us if this method is called
	// from inside the scheduledTick method.
	private void newTryMove(World world, BlockPos pos, BlockState state, boolean onScheduledTick) {
		Direction facing = state.get(Properties.FACING);
		boolean isExtended = state.get(Properties.EXTENDED);
		SettingsPack settings = BLOCK_TO_SETTINGS_PACK.get(state.getBlock());
		int activationDelay;
		boolean lazy;
		if (isExtended) {
			activationDelay = settings.get(FALLING_DELAY);
			lazy = settings.get(FALLING_LAZY);
		} else {
			activationDelay = settings.get(RISING_DELAY);
			lazy = settings.get(RISING_LAZY);
		}
		boolean powered = PistonBlockHelper.isReceivingPower(world, pos, state, facing);
		
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
				} else if (!((ServerWorldHelper)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), activationDelay, settings.get(RISING_TICK_PRIORITY));
				}
			} else {
				// We must check that the piston is currently not extending.
				// Otherwise the piston will continually pulse if the
				// forceUpdatePoweredPistons and lazy settings are both enabled
				if (powered && settings.get(FORCE_UPDATE_WHEN_POWERED) && !isExtending(world, pos, state, facing)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), 1, settings.get(RISING_TICK_PRIORITY));
				}
			}
			if (REDSTONE_TORCH.get(SOFT_INVERSION) && !onScheduledTick) {
				updateAdjacentRedstoneTorches(world, pos, state.getBlock());	
			}
			if (PistonBlockHelper.isExtended(world, pos, state, facing) || isExtending(world, pos, state, facing)) {
				world.setBlockState(pos, state.with(Properties.EXTENDED, true), 18);
			}
		} else if (!shouldExtend) {
			if (isExtended && !(settings.get(IGNORE_UPDATES_WHEN_EXTENDING) && isExtending(world, pos, state, facing))) {
				if (activationDelay == 0 || onScheduledTick) {
					if (GLOBAL.get(DOUBLE_RETRACTION)) {
						world.setBlockState(pos, state.with(Properties.EXTENDED, false), 18);
					}
					world.addSyncedBlockEvent(pos, state.getBlock(), 1, facing.getId());
				} else if (!((ServerWorldHelper)world).hasBlockEvent(pos)) {
					world.getBlockTickScheduler().schedule(pos, state.getBlock(), activationDelay, settings.get(FALLING_TICK_PRIORITY));
				}
			}
			if (REDSTONE_TORCH.get(SOFT_INVERSION)) {
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
		if (!(state.get(Properties.EXTENDED) || GLOBAL.get(DOUBLE_RETRACTION))) {
			return false;
		}
		BlockPos frontPos = pos.offset(facing);
		BlockState frontState = world.getBlockState(frontPos);
		if (frontState.isOf(Blocks.MOVING_PISTON) && frontState.get(Properties.FACING) == facing) {
			BlockEntity blockEntity = world.getBlockEntity(frontPos);
			if (blockEntity instanceof PistonBlockEntity) {
				PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
				
				return pistonBlockEntity.isSource() && pistonBlockEntity.isExtending() && pistonBlockEntity.getFacing() == facing;
			}
		}
		return false;
	}
	
	private void updateAdjacentRedstoneTorches(World world, BlockPos pos, Block block) {
		if (!world.isDebugWorld()) {
			for (Direction direction : Direction.values()) {
				BlockPos blockPos = pos.offset(direction);
				if (world.getBlockState(blockPos).getBlock() instanceof RedstoneTorchBlock) {
					world.updateNeighbor(blockPos, block, pos);
				}
			}
		}
	}
}
