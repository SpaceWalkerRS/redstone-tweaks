package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.DELAY;
import static redstonetweaks.setting.SettingsManager.PLANTS;
import static redstonetweaks.setting.SettingsManager.TICK_PRIORITY;
import static redstonetweaks.setting.SettingsManager.WATER;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Mixin(AbstractPlantStemBlock.class)
public class AbstractPlantStemBlockMixin {
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule0(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = PLANTS.get(DELAY);
		if (delay == 0) {
			if (!world.isClient()) {
				state.scheduledTick((ServerWorld)world, pos, world.getRandom());
			}
		} else {
			tickScheduler.schedule(pos, object, delay, PLANTS.get(TICK_PRIORITY));
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
