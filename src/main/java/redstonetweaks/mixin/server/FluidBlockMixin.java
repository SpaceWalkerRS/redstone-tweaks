package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import redstonetweaks.helper.FluidHelper;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
	
	@Shadow @Final private FlowableFluid fluid;
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (delay == 0) {
			fluid.onScheduledTick(world, pos, state.getFluidState());
		} else {
			tickScheduler.schedule(pos, object, delay, ((FluidHelper)fluid).getTickPriority());
		}
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (delay == 0) {
			fluid.onScheduledTick((World)world, pos, state.getFluidState());
		} else {
			tickScheduler.schedule(pos, object, delay, ((FluidHelper)fluid).getTickPriority());
		}
	}
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (delay == 0) {
			fluid.onScheduledTick(world, pos, state.getFluidState());
		} else {
			tickScheduler.schedule(pos, object, delay, ((FluidHelper)fluid).getTickPriority());
		}
	}
}
