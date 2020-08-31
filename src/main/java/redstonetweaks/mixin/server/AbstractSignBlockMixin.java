package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.DELAY;
import static redstonetweaks.setting.SettingsManager.TICK_PRIORITY;
import static redstonetweaks.setting.SettingsManager.WATER;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@Mixin(AbstractSignBlock.class)
public class AbstractSignBlockMixin {
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay, BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		delay = WATER.get(DELAY);
		if (delay == 0) {
			state.getFluidState().onScheduledTick((World)world, pos);
		} else {
			tickScheduler.schedule(pos, object, delay, WATER.get(TICK_PRIORITY));
		}
	}
}