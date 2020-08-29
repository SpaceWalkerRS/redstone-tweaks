package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin {
	
	@Shadow public abstract void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule0(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = BUBBLE_COLUMN.get(DELAY);
		if (delay == 0) {
			if (!world.isClient()) {
				scheduledTick(state, (ServerWorld)world, pos, world.getRandom());
			}
		} else {
			tickScheduler.schedule(pos, object, delay, BUBBLE_COLUMN.get(TICK_PRIORITY));
		}
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule1(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = WATER.get(DELAY);
		if (delay == 0) {
			state.getFluidState().onScheduledTick((World)world, pos);
		} else {
			tickScheduler.schedule(pos, object, delay, WATER.get(TICK_PRIORITY));
		}
	}
}
