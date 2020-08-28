package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
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

import redstonetweaks.helper.PistonBlockHelper;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	@Shadow protected abstract boolean shouldUnpower(World world, BlockPos pos, BlockState state);
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return REDSTONE_TORCH.get(WEAK_POWER);
	}
	
	@Inject(method = "shouldUnpower", at = @At(value = "HEAD"), cancellable = true)
	private void shouldUnpower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		BlockPos blockPos = pos.down();
		
		// If the softInversion setting is enabled,
		// return true if the torch is attached to a piston that is
		// receiving redstone power.
		if (REDSTONE_TORCH.get(SOFT_INVERSION)) {
			BlockState blockState = world.getBlockState(blockPos);
			
			if (blockState.getBlock() instanceof PistonBlock) {
				if (PistonBlockHelper.isReceivingPower(world, blockPos, blockState, blockState.get(Properties.FACING))) {
					cir.setReturnValue(true);
					cir.cancel();
				}
			}
		}
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneTorchBlock;shouldUnpower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
	private boolean onScheduledRedirectShouldUnpower(RedstoneTorchBlock torch, World world, BlockPos pos, BlockState state) {
		boolean powered = state.get(Properties.LIT);
		boolean lazy = powered ? REDSTONE_TORCH.get(FALLING_LAZY) : REDSTONE_TORCH.get(RISING_LAZY);
		return lazy ? powered : shouldUnpower(world, pos, state);
	}
	
	@ModifyConstant(method = "scheduledTick", constant = @Constant(longValue = 60L))
	private long updateBurnoutTimerDelay(long oldValue) {
		return REDSTONE_TORCH.get(BURNOUT_TIMER);
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		int delay = REDSTONE_TORCH.get(BURNOUT_DELAY);
		if (delay > 0) {
			tickScheduler.schedule(pos, object, delay, REDSTONE_TORCH.get(BURNOUT_TICK_PRIORITY));
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int oldDelay, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		int delay = state.get(Properties.LIT) ? REDSTONE_TORCH.get(FALLING_DELAY) : REDSTONE_TORCH.get(RISING_DELAY);
		if (delay == 0) {
			scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
		} else {
			TickPriority priority = state.get(Properties.LIT) ? REDSTONE_TORCH.get(FALLING_TICK_PRIORITY) : REDSTONE_TORCH.get(RISING_TICK_PRIORITY);
			tickScheduler.schedule(pos, object, delay, priority);
		}
		
	}
	
	@Inject(method = "getStrongRedstonePower", at = @At(value = "HEAD"), cancellable = true)
	private void onGetStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(direction == Direction.DOWN && state.get(Properties.LIT) ? REDSTONE_TORCH.get(STRONG_POWER) : 0);
		cir.cancel();
	}
	
	@ModifyConstant(method = "isBurnedOut", constant = @Constant(intValue = 8))
	private static int onIsBurnedOutModifyBurnoutCount(int oldValue) {
		return REDSTONE_TORCH.get(BURNOUT_COUNT);
	}
}
