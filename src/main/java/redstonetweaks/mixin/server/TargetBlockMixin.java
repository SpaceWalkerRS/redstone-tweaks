package redstonetweaks.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.TargetBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.WorldAccess;

import redstonetweaks.setting.Settings;

@Mixin(TargetBlock.class)
public class TargetBlockMixin {
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 20))
	private static int onTriggerPersistentProjectileDelay(int oldValue) {
		return Settings.TargetBlock.DELAY_PERSISTENT_PROJECTILE.get();
	}
	
	@ModifyConstant(method = "trigger", constant = @Constant(intValue = 8))
	private static int onTriggerDefaultDelay(int oldValue) {
		return Settings.TargetBlock.DELAY_DEFAULT.get();
	}
	
	@ModifyConstant(method = "calculatePower", constant = @Constant(doubleValue = 15.0D))
	private static double onCalculatePowerModify15(double oldValue) {
		return Settings.Global.POWER_MAX.get();
	}
	
	@Redirect(method = "setPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/TickScheduler;schedule(Lnet/minecraft/util/math/BlockPos;Ljava/lang/Object;I)V"))
	private static <T> void onSetPowerRedirectSchedule(TickScheduler<T> tickScheduler, BlockPos pos1, T object, int delay1, WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
		if (delay == 0) {
			state.scheduledTick((ServerWorld)world, pos, world.getRandom());
		} else {
			tickScheduler.schedule(pos, object, delay, Settings.TargetBlock.TICK_PRIORITY.get());
		}
	}
}
