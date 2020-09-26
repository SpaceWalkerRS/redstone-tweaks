package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.CoralBlockBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.settings.Settings;

@Mixin(CoralBlockBlock.class)
public abstract class CoralBlockBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, getDelay(world.getRandom()), Settings.CoralBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "getPlacementState", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetPlacementStateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, ItemPlacementContext ctx) {
		delay = getDelay(ctx.getWorld().getRandom());
		if (delay > 0) {
			tickScheduler.schedule(ctx.getBlockPos(), object, delay, Settings.CoralBlock.TICK_PRIORITY.get());
		}
	}
	
	private int getDelay(Random random) {
		int min = Settings.CoralBlock.DELAY_MIN.get();
		int max = Settings.CoralBlock.DELAY_MAX.get();
		
		int range =  min > max ? 0 : max - min;
		
		return range == 0 ? min : min + random.nextInt(range);
	}
}
