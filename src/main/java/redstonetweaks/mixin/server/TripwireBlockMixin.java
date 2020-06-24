package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.tripwireDelay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.TripwireBlock;;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin {
	
	@ModifyArg(method = "updatePowered", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private int modifyGetTickRate(int oldDelay) {
		return tripwireDelay.get();
	}
}
