package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.PistonHelper;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.Tweaks;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin {
	
	@Shadow public static native boolean canFallThrough(BlockState state);
	
	// Modify the delay gravity blocks have before falling
	@ModifyConstant(method = "getFallDelay", constant = @Constant(intValue = 2))
	private int getGravityBlockDelay(int oldDelay) {
		return Tweaks.GravityBlock.DELAY.get();
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, delay, Tweaks.GravityBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, delay, Tweaks.GravityBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FallingBlock;canFallThrough(Lnet/minecraft/block/BlockState;)Z"))
	private boolean onScheduledTickRedirectCanFallThrough(BlockState belowState, BlockState state, ServerWorld world, BlockPos pos, Random random) {
		return canFallThrough(belowState) && !isSuspended(world, pos, state);
	}
	
	private boolean isSuspended(World world, BlockPos pos, BlockState state) {
		if (Tweaks.GravityBlock.SUSPENDED_BY_STICKY_BLOCKS.get()) {
			for (Direction dir : Direction.values()) {
				BlockPos neighborPos = pos.offset(dir);
				BlockState neighborState = world.getBlockState(neighborPos);
				
				if (PistonHelper.isAdjacentBlockStuck(world, neighborPos, neighborState, pos, state, dir.getOpposite())) {
					return true;
				}
			}
		}
		
		
		return false;
	}
}
