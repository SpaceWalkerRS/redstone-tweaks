package redstonetweaks.mixin.server;

import static redstonetweaks.setting.SettingsManager.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;

@Mixin(FallingBlock.class)
public class FallingBlockMixin {
	
	// Modify the delay gravity blocks have before falling
	@ModifyConstant(method = "getFallDelay", constant = @Constant(intValue = 2))
	private int getGravityBlockDelay(int oldDelay) {
		return GRAVITY_BLOCK.get(DELAY);
	}
	
	@Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onOnBlockAddedRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay) {
		tickScheduler.schedule(pos, object, delay, GRAVITY_BLOCK.get(TICK_PRIORITY));
	}
	
	@Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private <T> void onGetStateForNeighborUpdateRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos, T object, int delay) {
		tickScheduler.schedule(pos, object, delay, GRAVITY_BLOCK.get(TICK_PRIORITY));
	}
}
