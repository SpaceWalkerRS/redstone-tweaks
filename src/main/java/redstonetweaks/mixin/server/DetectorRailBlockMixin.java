package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.DetectorRailBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;

import redstonetweaks.settings.Settings;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {
	
	@ModifyConstant(method = "getWeakRedstonePower", constant = @Constant(intValue = 15))
	private int onGetWeakRedstonePower(int oldValue) {
		return Settings.DetectorRail.POWER_WEAK.get();
	}
	
	@ModifyConstant(method = "getStrongRedstonePower", constant = @Constant(intValue = 15))
	private int onGetStrongRedstonePower(int oldValue) {
		return Settings.DetectorRail.POWER_STRONG.get();
	}
	
	@Redirect(method = "updatePoweredStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onUpdatePoweredStatusRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T block, int oldDelay) {
		tickScheduler.schedule(pos, block, Settings.DetectorRail.DELAY.get(), Settings.DetectorRail.TICK_PRIORITY.get());
	}
}
