package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIAbstractBlockState;
import redstonetweaks.interfaces.mixin.RTIBlock;
import redstonetweaks.interfaces.mixin.RTIServerWorld;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.settings.Tweaks;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin extends AbstractBlock implements RTIBlock {
	
	public ObserverBlockMixin(Settings settings) {
		super(settings);
	}
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract void updateNeighbors(World world, BlockPos pos, BlockState state);
	
	@ModifyVariable(
			method = "<init>",
			argsOnly = true,
			at = @At(
					value = "HEAD"
			)
	)
	private static Settings onInitModifySettings(Settings settings) {
		AbstractBlock.ContextPredicate solidPredicate = (state, world, pos) -> {
			return Tweaks.Observer.IS_SOLID.get();
		};
		
		return settings.solidBlock(solidPredicate);
	}
	
	@Inject(
			method = "scheduledTick", 
			cancellable = true, 
			at = @At(
					value = "INVOKE", 
					ordinal = 1, 
					shift = Shift.BEFORE, 
					target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
			)
	)
	private void onScheduledTickInjectBeforeSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			tryPowerOff(world, pos, state);
		}
	}
	
	@Inject(
			method = "scheduledTick", 
			cancellable = true, 
			at = @At(
					value = "INVOKE", 
					ordinal = 1, 
					shift = Shift.AFTER, 
					target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
			)
	)
	private void onScheduledTickInjectAfterSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!((RTIWorld)world).immediateNeighborUpdates() && !Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 1, (ObserverBlock)(Object)this);
			
			ci.cancel();
		}
	}
	
	@Redirect(
			method = "scheduledTick", 
			at = @At(
					value = "INVOKE", 
					target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"
			)
	)
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos blockPos, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			tryPowerOff(world, pos, world.getBlockState(pos));
		}
	}
	
	@Inject(
			method = "getStateForNeighborUpdate",
			cancellable = true , 
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStateForNeighborUpdateInjectAtHead(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom, CallbackInfoReturnable<BlockState> cir) {
		if (Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			cir.setReturnValue(state);
			cir.cancel();
		}
	}
	
	@Inject(
			method = "getStateForNeighborUpdate", 
			cancellable = true, 
			at = @At(
					value = "INVOKE", 
					shift = Shift.BEFORE, 
					target = "Lnet/minecraft/block/ObserverBlock;scheduleTick(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"
			)
	)
	private void onGetStateForNeighborUpdateInjectBeforeScheduleTick(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos notifierPos, CallbackInfoReturnable<BlockState> cir) {
		cir.setReturnValue(tryPowerOn(world, pos, state));
		cir.cancel();
	}
	
	@Inject(
			method = "updateNeighbors", 
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, state.get(Properties.FACING).getOpposite(), state.getBlock(), Tweaks.Observer.BLOCK_UPDATE_ORDER.get());
		
		ci.cancel();
	}
	
	@Inject(
			method = "getStrongRedstonePower", 
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetStrongRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, true));
		cir.cancel();
	}
	
	@Inject(
			method = "getWeakRedstonePower", 
			cancellable = true, 
			at = @At(
					value = "HEAD"
			)
	)
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(getPowerOutput(world, pos, state, dir, false));
		cir.cancel();
	}
	
	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566) and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call. Enabling the 1 flag makes sure neighboring blocks are
	// updated, fixing MC-136566. Disabling the 16 flag makes sure neighboring observers are updated, fixing MC-137127.
	@ModifyConstant(
			method = "onBlockAdded", 
			constant = @Constant(
					intValue = 18
			)
	)
	private int onBlockAddedModifySetBlockStateFlags(int flags) {
		if (Tweaks.BugFixes.MC136566.get()) {
			flags |= 1;
		}
		if (Tweaks.BugFixes.MC137127.get()) {
			flags &= ~16;
		}
		
		return flags;
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos notifierPos, boolean notify) {
		if (Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get() && !state.get(Properties.POWERED)) {
			if (notifierPos.equals(pos) || notifierPos.equals(pos.offset(state.get(Properties.FACING)))) {
				tryPowerOn(world, pos, state);
			}
		}
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return BlockHelper.microTickModeBlockEvent(state, world, pos, type, data);
	}
	
	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		BlockState state = world.getBlockState(pos);
		
		if (!world.isClient()) {
			tryPowerOff((ServerWorld)world, pos, state);
		}
		updateNeighbors(world, pos, state);
		
		return false;
	}
	
	private BlockState tryPowerOn(WorldAccess world, BlockPos pos, BlockState state) {
		if (!Tweaks.Observer.DISABLE.get() && !world.isClient()) {
			int delay = Tweaks.Observer.DELAY_RISING_EDGE.get();
			
			BlockPos frontPos = pos.offset(state.get(Properties.FACING).getOpposite());
			RTIAbstractBlockState frontState = (RTIAbstractBlockState)world.getBlockState(frontPos);
			
			delay = frontState.delayOverride(delay);
			
			if (Tweaks.Observer.MICRO_TICK_MODE.get() || frontState.forceMicroTickMode()) {
				if (world instanceof World && !((RTIServerWorld)world).hasBlockEvent(pos, state.getBlock())) {
					((ServerWorld)world).addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
				}
			} else {
				if (!world.getBlockTickScheduler().isScheduled(pos, state.getBlock())) {
					TickPriority tickPriority = Tweaks.Observer.TICK_PRIORITY_RISING_EDGE.get();
					
					if (TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, frontState.tickPriorityOverride(tickPriority))) {
						return world.getBlockState(pos);
					}
				}
			}
		}
		
		return state;
	}
	
	private void tryPowerOff(ServerWorld world, BlockPos pos, BlockState state) {
		int delay = Tweaks.Observer.DELAY_FALLING_EDGE.get();
		
		BlockPos frontPos = pos.offset(state.get(Properties.FACING).getOpposite());
		RTIAbstractBlockState frontState = (RTIAbstractBlockState)world.getBlockState(frontPos);
		
		delay = frontState.delayOverride(delay);
		
		if (Tweaks.Observer.MICRO_TICK_MODE.get()) {
			world.addSyncedBlockEvent(pos, state.getBlock(), delay, 0);
		} else {
			TickPriority tickPriority = Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get();
			
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, frontState.tickPriorityOverride(tickPriority));
		}
	}
	
	private int getPowerOutput(BlockView world, BlockPos pos, BlockState state, Direction dir, boolean strong) {
		if (!state.get(Properties.POWERED)) {
			return 0;
		}
		
		Direction behind = state.get(Properties.FACING);
		
		if (dir != behind) {
			return 0;
		}
		
		int power = strong ? Tweaks.Observer.POWER_STRONG.get() : Tweaks.Observer.POWER_WEAK.get();
		
		BlockPos frontPos = pos.offset(behind.getOpposite());
		RTIAbstractBlockState frontState = (RTIAbstractBlockState)world.getBlockState(frontPos);
		
		return strong ? frontState.strongPowerOverride(power) : frontState.weakPowerOverride(power);
	}
}
