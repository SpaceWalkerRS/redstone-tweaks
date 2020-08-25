package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;;

@Mixin(ScaffoldingBlock.class)
public class ScaffoldingBlockMixin {
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, SCAFFOLDING.get(DELAY), SCAFFOLDING.get(TICK_PRIORITY));
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, SCAFFOLDING.get(DELAY), SCAFFOLDING.get(TICK_PRIORITY));
	}
}
