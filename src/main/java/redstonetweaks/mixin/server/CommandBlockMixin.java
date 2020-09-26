package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.CommandBlock;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;

import redstonetweaks.settings.Settings;

@Mixin(CommandBlock.class)
public class CommandBlockMixin {
	
	@Redirect(method = "neighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Settings.CommandBlock.DELAY.get(), Settings.CommandBlock.TICK_PRIORITY.get());
	}
	
	@Redirect(method = "scheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerTickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onScheduledTickRedirectSchedule(ServerTickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Settings.CommandBlock.DELAY.get(), Settings.CommandBlock.TICK_PRIORITY.get());
	}
}
