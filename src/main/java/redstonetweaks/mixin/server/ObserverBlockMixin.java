package redstonetweaks.mixin.server;

import java.util.Random;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.mixinterfaces.RTIBlock;
import redstonetweaks.mixinterfaces.RTIServerWorld;
import redstonetweaks.mixinterfaces.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin implements RTIBlock {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract void scheduleTick(WorldAccess world, BlockPos pos);
	@Shadow protected abstract void updateNeighbors(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos blockPos, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		TickSchedulerHelper.schedule(world, world.getBlockState(pos), tickScheduler, pos, block, Tweaks.Observer.DELAY_FALLING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get());
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState0(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!((RTIWorld)world).immediateNeighborUpdates()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 0, (Block)(Object)this);
			
			ci.cancel();
		}
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!((RTIWorld)world).immediateNeighborUpdates()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 1, (Block)(Object)this);
			
			ci.cancel();
		}
	}
	
	@Inject(method = "getStateForNeighborUpdate", cancellable = true , at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/ObserverBlock;scheduleTick(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onGetStateForNeighborUpdateInjectBeforeScheduleTick(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom, CallbackInfoReturnable<BlockState> cir) {
		if (!Tweaks.Observer.DISABLE.get() && !world.isClient()) {
			if (Tweaks.Observer.DELAY_RISING_EDGE.get() == 0) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
				state = world.getBlockState(pos);
			} else {
				scheduleTick(world, pos);
			}
		}
		cir.setReturnValue(state);
		cir.cancel();
	}
	
	@Redirect(method = "scheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduleTickRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Tweaks.Observer.DELAY_RISING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_RISING_EDGE.get());
	}
	
	@Inject(method = "updateNeighbors", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, state.get(Properties.FACING).getOpposite(), state.getBlock(), Tweaks.Observer.BLOCK_UPDATE_ORDER.get());
		ci.cancel();
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(state.get(Properties.POWERED) && state.get(Properties.FACING) == direction ? Tweaks.Observer.POWER_STRONG.get() : 0);
		cir.cancel();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Tweaks.Observer.POWER_WEAK.get();
	}
	
	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566) and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call. Enabling the 1 flag makes sure neighboring blocks are
	// updated, fixing MC-136566. Disabling the 16 flag makes sure neighboring observers are updated, fixing MC-137127.
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 18))
	private int onBlockAddedFlags(int flags) {
		if (Tweaks.BugFixes.MC136566.get()) {
			flags |= 1;
		}
		if (Tweaks.BugFixes.MC137127.get()) {
			flags &= ~16;
		}
		return flags;
	}

	@Override
	public boolean continueAction(World world, BlockPos pos, int type) {
		if (type == 0) {
			updateNeighbors(world, pos, world.getBlockState(pos));
		} else if (type == 1) {
			TickSchedulerHelper.schedule(world, world.getBlockState(pos), world.getBlockTickScheduler(), pos, (Block)(Object)this, Tweaks.Observer.DELAY_FALLING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get());
			
			updateNeighbors(world, pos, world.getBlockState(pos));
		}
		
		return false;
	}
}
