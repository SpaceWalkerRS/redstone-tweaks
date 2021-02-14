package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.TripwireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import redstonetweaks.helper.TickSchedulerHelper;
import redstonetweaks.setting.settings.Tweaks;;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin {
	
	@Redirect(method = "updatePowered", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onUpdatePoweredRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int oldDelay, World world, BlockPos pos) {
		TickSchedulerHelper.scheduleBlockTick(world, pos, world.getBlockState(pos), Tweaks.Tripwire.DELAY.get(), Tweaks.Tripwire.TICK_PRIORITY.get());
	}
}
