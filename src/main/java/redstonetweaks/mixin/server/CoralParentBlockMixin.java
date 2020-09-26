package redstonetweaks.mixin.server;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.CoralParentBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.settings.Settings;

@Mixin(CoralParentBlock.class)
public class CoralParentBlockMixin {
	
	@Redirect(method = "checkLivingConditions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onCheckLivingConditionsRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, WorldAccess world, BlockPos pos) {
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, block, getDelay(world.getRandom()), Settings.Coral.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T fluid, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.schedule(world, state, tickScheduler, pos, fluid, delay, Settings.Water.TICK_PRIORITY.get());
	}
	
	private int getDelay(Random random) {
		int min = Settings.Coral.DELAY_MIN.get();
		int max = Settings.Coral.DELAY_MAX.get();
		
		int range =  min > max ? 0 : max - min;
		
		return range == 0 ? min : min + random.nextInt(range);
	}
}
