package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

@Mixin(ChorusFlowerBlock.class)
public class ChorusFlowerBlockMixin {
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = CHORUS_PLANT.get(DELAY);
		if (delay == 0) {
			if (!world.isClient()) {
				state.scheduledTick((ServerWorld)world, pos, world.getRandom());
			}
		} else {
			tickScheduler.schedule(pos, object, delay, CHORUS_PLANT.get(TICK_PRIORITY));
		}
	}
}
