package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.interfaces.mixin.RTIWorld;
import redstonetweaks.setting.Tweaks;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean shouldUnpower(World world, BlockPos pos, BlockState state);
	
	@Inject(method = "onStateReplaced", cancellable = true, at = @At(value = "HEAD"))
	private void onOnStateReplacedInjectAtHead(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
		if (!moved) {
			updateNeighbors(world, pos);
		}
		
		ci.cancel();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Tweaks.RedstoneTorch.POWER_WEAK.get();
	}
	
	@Inject(method = "shouldUnpower", at = @At(value = "HEAD"), cancellable = true)
	private void shouldUnpower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		BlockPos blockPos = pos.down();
		
		// If the softInversion setting is enabled, return true if the torch is attached to
		// a piston that is receiving redstone power.
		if (Tweaks.RedstoneTorch.SOFT_INVERSION.get()) {
			BlockState blockState = world.getBlockState(blockPos);
			
			if (blockState.getBlock() instanceof PistonBlock) {
				if (PistonHelper.isReceivingPower(world, blockPos, blockState, blockState.get(Properties.FACING))) {
					cir.setReturnValue(true);
					cir.cancel();
				}
			}
		}
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneTorchBlock;shouldUnpower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onScheduledRedirectShouldUnpower(RedstoneTorchBlock torch, World world, BlockPos pos, BlockState state) {
		boolean powered = state.get(Properties.LIT);
		boolean lazy = powered ? Tweaks.RedstoneTorch.LAZY_FALLING_EDGE.get() : Tweaks.RedstoneTorch.LAZY_RISING_EDGE.get();
		return lazy ? powered : shouldUnpower(world, pos, state);
	}
	
	@ModifyConstant(method = "scheduledTick", constant = @Constant(longValue = 60L))
	private long updateBurnoutTimerDelay(long oldValue) {
		return Tweaks.RedstoneTorch.BURNOUT_TIMER.get();
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos1, T object, int oldDelay, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.RedstoneTorch.DELAY_BURNOUT.get(), Tweaks.RedstoneTorch.TICK_PRIORITY_BURNOUT.get());
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int oldDelay, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean lit = state.get(Properties.LIT);
		
		int delay = lit ? Tweaks.RedstoneTorch.DELAY_FALLING_EDGE.get() : Tweaks.RedstoneTorch.DELAY_RISING_EDGE.get();
		TickPriority priority = lit ? Tweaks.RedstoneTorch.TICK_PRIORITY_FALLING_EDGE.get() : Tweaks.RedstoneTorch.TICK_PRIORITY_RISING_EDGE.get();
		
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, delay, priority);
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(direction == Direction.DOWN && state.get(Properties.LIT) ? Tweaks.RedstoneTorch.POWER_STRONG.get() : 0);
		cir.cancel();
	}
	
	@ModifyConstant(method = "isBurnedOut", constant = @Constant(intValue = 8))
	private static int onIsBurnedOutModifyBurnoutCount(int oldValue) {
		return Tweaks.RedstoneTorch.BURNOUT_COUNT.get();
	}
	
	private void updateNeighbors(World world, BlockPos pos) {
		((RTIWorld)world).dispatchBlockUpdates(pos, null, (RedstoneTorchBlock)(Object)this, Tweaks.RedstoneTorch.BLOCK_UPDATE_ORDER.get());
	}
}
