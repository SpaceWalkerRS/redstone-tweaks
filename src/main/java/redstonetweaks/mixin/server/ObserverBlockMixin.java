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

import redstonetweaks.helper.BlockHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.settings.Settings;
import redstonetweaks.world.server.UnfinishedEvent.Source;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin implements BlockHelper {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract void scheduleTick(WorldAccess world, BlockPos pos);
	@Shadow protected abstract void updateNeighbors(World world, BlockPos pos, BlockState state);
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos1, T block, int oldDelay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		TickSchedulerHelper.schedule(world, world.getBlockState(pos), tickScheduler, pos, block, Settings.Observer.DELAY_FALLING_EDGE.get(), Settings.Observer.TICK_PRIORITY_FALLING_EDGE.get());
	}
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/block/ObserverBlock;updateNeighbors(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void onScheduledTickInjectBeforeUpdateNeighbors(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (!((WorldHelper)world).updateNeighborsNormally()) {
			((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
		}
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ObserverBlock;scheduleTick(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onGetStateForNeighborUpdateRedirectScheduleTick(ObserverBlock observer, WorldAccess world1, BlockPos pos1, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!(Settings.Observer.DISABLE.get() || world.isClient())) {
			if (Settings.Observer.DELAY_RISING_EDGE.get() == 0) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
			} else {
				scheduleTick(world, pos);
			}
		}
	}
	
	@Redirect(method = "scheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduleTickRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, Settings.Observer.DELAY_RISING_EDGE.get(), Settings.Observer.TICK_PRIORITY_RISING_EDGE.get());
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(state.get(Properties.POWERED) && state.get(Properties.FACING) == direction ? Settings.Observer.POWER_STRONG.get() : 0);
		cir.cancel();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Settings.Observer.POWER_WEAK.get();
	}
	
	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566)
	// and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call.
	// Enabling the 1 flag makes sure neighboring blocks are updated,
	// fixing MC-136566. Disabling the 16 flag makes sure neighboring
	// observers are updated, fixing MC-137127.
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 18))
	private int onBlockAddedFlags(int flags) {
		if (Settings.BugFixes.MC136566.get()) {
			flags |= 1;
		}
		if (Settings.BugFixes.MC137127.get()) {
			flags &= ~16;
		}
		return flags;
	}

	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		if (type == 0) {
			if (state.get(Properties.POWERED)) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), Settings.Observer.DELAY_FALLING_EDGE.get(), Settings.Observer.TICK_PRIORITY_FALLING_EDGE.get());
			}
			
			updateNeighbors(world, pos, state);
		}
		
		return false;
	}
}
