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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
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
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.BEFORE, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectBeforeSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Observer.DELAY_FALLING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get());
		}
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState1(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!((RTIWorld)world).immediateNeighborUpdates() && !Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			((RTIServerWorld)world).getIncompleteActionScheduler().scheduleBlockAction(pos, 1, (ObserverBlock)(Object)this);
			
			ci.cancel();
		}
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos blockPos, T block, int delay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			TickSchedulerHelper.scheduleBlockTick(world, pos, world.getBlockState(pos), Tweaks.Observer.DELAY_FALLING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get());
		}
	}
	
	@Inject(method = "getStateForNeighborUpdate", cancellable = true , at = @At(value = "HEAD"))
	private void onGetStateForNeighborUpdateInjectAtHead(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom, CallbackInfoReturnable<BlockState> cir) {
		if (Tweaks.Observer.OBSERVE_BLOCK_UPDATES.get()) {
			cir.setReturnValue(state);
			cir.cancel();
		}
	}
	
	@Inject(method = "getStateForNeighborUpdate", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/ObserverBlock;scheduleTick(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onGetStateForNeighborUpdateInjectBeforeScheduleTick(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos notifierPos, CallbackInfoReturnable<BlockState> cir) {
		cir.setReturnValue(tryPowerOn(world, pos, state));
		cir.cancel();
	}
	
	@Inject(method = "updateNeighbors", cancellable = true, at = @At(value = "HEAD"))
	private void onUpdateNeighborsInjectAtHead(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		((RTIWorld)world).dispatchBlockUpdates(pos, state.get(Properties.FACING).getOpposite(), state.getBlock(), Tweaks.Observer.BLOCK_UPDATE_ORDER.get());
		
		ci.cancel();
	}
	
	@Inject(method = "getStrongRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetStrongRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(state.get(Properties.POWERED) && state.get(Properties.FACING) == direction ? Tweaks.Observer.POWER_STRONG.get() : 0);
		cir.cancel();
	}
	
	@Inject(method = "getWeakRedstonePower", cancellable = true, at = @At(value = "HEAD"))
	private void onGetWeakRedstonePowerInjectAtHead(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(state.get(Properties.POWERED) && state.get(Properties.FACING) == direction ? Tweaks.Observer.POWER_WEAK.get() : 0);
		cir.cancel();
	}
	
	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566) and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call. Enabling the 1 flag makes sure neighboring blocks are
	// updated, fixing MC-136566. Disabling the 16 flag makes sure neighboring observers are updated, fixing MC-137127.
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 18))
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
	public boolean continueAction(World world, BlockPos pos, int type) {
		BlockState state = world.getBlockState(pos);
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Observer.DELAY_FALLING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_FALLING_EDGE.get());
		updateNeighbors(world, pos, state);
		
		return false;
	}
	
	private BlockState tryPowerOn(WorldAccess world, BlockPos pos, BlockState state) {
		if (!Tweaks.Observer.DISABLE.get() && !world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, state.getBlock())) {
			if (TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Observer.DELAY_RISING_EDGE.get(), Tweaks.Observer.TICK_PRIORITY_RISING_EDGE.get())) {
				return world.getBlockState(pos);
			}
		}
		
		return state;
	}
}
