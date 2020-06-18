package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.TripwireHookBlock;

import redstonetweaks.setting.Settings;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin {
	
	@ModifyArg(method = "update", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private int modifyGetTickRate(int oldDelay) {
		return (int)Settings.tripwireHookDelay.get();
	}
}
