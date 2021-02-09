package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.Tweaks;

@Mixin(AbstractPlantStemBlock.class)
public class AbstractPlantStemBlockMixin {
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule0(TickScheduler<T> tickScheduler, BlockPos pos1, T block, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, state, Tweaks.Vines.DELAY.get(), Tweaks.Vines.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule1(TickScheduler<T> tickScheduler, BlockPos pos1, T fluid, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		TickSchedulerHelper.scheduleFluidTick(world, pos, state.getFluidState(), delay, Tweaks.Water.TICK_PRIORITY.get());
	}
}
