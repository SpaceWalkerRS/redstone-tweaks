package redstonetweaks.mixin.server;

import static redstonetweaks.setting.Settings.tripwireHookSignal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.block.TripwireHookBlock;

import redstonetweaks.setting.Settings;

@Mixin(TripwireHookBlock.class)
public class TripwireHookBlockMixin {
	
	@ModifyArg(method = "update", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private int modifyGetTickRate(int oldDelay) {
		return Settings.tripwireHookDelay.get();
	}
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return tripwireHookSignal.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return tripwireHookSignal.get();
	}
}
