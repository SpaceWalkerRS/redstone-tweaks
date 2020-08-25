package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.TripwireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin {
	
	@Redirect(method = "updatePowered", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onUpdatePoweredRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int oldDelay) {
		tickScheduler.schedule(pos, object, TRIPWIRE.get(DELAY), TRIPWIRE.get(TICK_PRIORITY));
	}
}
