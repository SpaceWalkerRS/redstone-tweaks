package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = WATER.get(DELAY);
		if (delay == 0) {
			state.getFluidState().onScheduledTick((World)world, pos);
		} else {
			tickScheduler.schedule(pos, object, delay, WATER.get(TICK_PRIORITY));
		}
	}
	
	@Redirect(method = "tryFillWithFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onTryFillWithFluidRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		delay = WATER.get(DELAY);
		if (delay == 0) {
			state.getFluidState().onScheduledTick((World)world, pos);
		} else {
			tickScheduler.schedule(pos, object, delay, WATER.get(TICK_PRIORITY));
		}
	}
}
