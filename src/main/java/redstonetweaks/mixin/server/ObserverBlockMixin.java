package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

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

import redstonetweaks.helper.AbstractBlockHelper;
import redstonetweaks.helper.ServerWorldHelper;
import redstonetweaks.helper.WorldHelper;
import redstonetweaks.world.server.UnfinishedEvent.Source;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin implements AbstractBlockHelper {
	
	@Shadow protected abstract void updateNeighbors(World world, BlockPos pos, BlockState state);
	
	@Inject(method = "scheduledTick", cancellable = true, at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	private void onScheduledTickInjectAfterSetBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (((WorldHelper)world).shouldSeparateUpdates()) {
			((ServerWorldHelper)world).getUnfinishedEventScheduler().schedule(Source.BLOCK, state, pos, 0);
			ci.cancel();
		}
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, OBSERVER.get(FALLING_DELAY), OBSERVER.get(FALLING_TICK_PRIORITY));
	}
	
	@Redirect(method = "scheduleTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduleTickInjectBeforeSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		if (!OBSERVER.get(DISABLE)) {
			tickScheduler.schedule(pos, object, OBSERVER.get(RISING_DELAY), OBSERVER.get(RISING_TICK_PRIORITY));
		}
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePowerRedirectGetWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(state.get(Properties.POWERED) && state.get(Properties.FACING) == direction ? OBSERVER.get(STRONG_POWER) : 0);
		cir.cancel();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return OBSERVER.get(WEAK_POWER);
	}
	
	// To fix MC-136566 (https://bugs.mojang.com/browse/MC-136566)
	// and MC-137127 (https://bugs.mojang.com/browse/MC-137127)
	// we change the flags argument given to the setBlockState call.
	// Enabling the 1 flag makes sure neighboring blocks are updated,
	// fixing MC-136566. Disabling the 16 flag makes sure neighboring
	// observers are updated, fixing MC-137127.
	@ModifyConstant(method = "onBlockAdded", constant = @Constant(intValue = 18))
	private int onBlockAddedFlags(int oldFlags) {
		int flags = oldFlags;
		if (BUG_FIXES.get(MC136566)) {
			flags |= 1;
		}
		if (BUG_FIXES.get(MC137127)) {
			flags &= ~16;
		}
		return flags;
	}

	@Override
	public boolean continueEvent(World world, BlockState state, BlockPos pos, int type) {
		if (type == 0) {
			if (state.get(Properties.POWERED)) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), OBSERVER.get(FALLING_DELAY), OBSERVER.get(FALLING_TICK_PRIORITY));
			}
			
			updateNeighbors(world, pos, state);
		}
		
		return false;
	}
}
